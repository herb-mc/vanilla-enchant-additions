package com.herb_mc.vanilla_enchant_additions.helper;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.constant.Constable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.herb_mc.vanilla_enchant_additions.VEAMod.arrayContains;

public class VEAConfigHelper {

    private static Path getFile(MinecraftServer server) {
        return server.getSavePath(WorldSavePath.ROOT).resolve("vanilla_enchant_additions.conf");
    }

    public static void writeSettingToConf(String key, String value, MinecraftServer server) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(server).toFile(), true))) {
            writer.write(key + ": " + value + "\n");
        } catch (IOException e) {
            VEAMod.LOGGER.error("Failed write value for '{}' to vanilla_enchant_additions.conf", key,  e);
        }
    }

    public static void overwriteSettingToConf(String key, String value, MinecraftServer server) throws IOException {
        ArrayList<String> file = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(getFile(server))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.replaceAll("[\\r\\n]", "").split(":\\s");
                if (fields[0].matches(key))
                    file.add(fields[0] + ": " + value);
                else
                    file.add(fields[0] + ": " + fields[1]);
            }
        } catch (NoSuchFileException e) {
            try {
                VEAMod.LOGGER.info("No existing config file, generating defaults for vanilla_enchant_additions.conf");
                Files.createFile(getFile(server));
                writeDefaults(server);
            } catch (IOException e1) {
                VEAMod.LOGGER.error("Unable to generate defaults for vanilla_enchant_additions.conf",  e);
            }
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getFile(server))) {
            for (String line : file) {
                writer.write(line + "\n");
            }
        } catch (IOException e) {
            VEAMod.LOGGER.error("Failed write to vanilla_enchant_additions.conf",  e);
        }
    }

    private static void writeDefaults(MinecraftServer server) {
        String track = null;
        try (BufferedWriter writer = Files.newBufferedWriter(getFile(server))) {
            for (String key: VEAMod.defaultConfigs.keySet()) {
                track = key;
                writer.write(key + ": " + VEAMod.defaultConfigs.get(key).getAsString() + "\n");
            }
        } catch (IOException e) {
            VEAMod.LOGGER.error("Failed write value for '{}' to vanilla_enchant_additions.conf", track,  e);
        }
    }

    public static void loadConf(MinecraftServer server) {
        {
            HashMap<String, ConfigOpt> h = VEAMod.defaultConfigs;
            for (String k : h.keySet())
                VEAMod.configMaps.put(k, new ConfigOpt(h.get(k).getValue(), h.get(k).getAcceptedValues(), h.get(k).type));
            Path path = getFile(server);
            ArrayList<String> confOptions = defaultOpts();
            try (BufferedReader reader = Files.newBufferedReader(path)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = line.replaceAll("[\\r\\n]", "").split(":\\s");
                    if (defaultContains(fields[0])) {
                        if (fields.length > 1) {
                            if (VEAMod.defaultConfigs.get(fields[0]).getAcceptedValues() == VEAMod.any || arrayContains(VEAMod.configMaps.get(fields[0]).getAcceptedValues(), fields[1])) {
                                if (VEAMod.configMaps.get(fields[0]).type == boolean.class && parseBool(fields[0], fields[1]) != null) {
                                    VEAMod.configMaps.get(fields[0]).setValue(parseBool(fields[0], fields[1]));
                                    VEAMod.configMaps.get(fields[0]).setAcceptedValues(VEAMod.bool);
                                } else if (VEAMod.configMaps.get(fields[0]).type == int.class) {
                                    try {
                                        VEAMod.configMaps.get(fields[0]).setValue(Integer.parseInt(fields[1]));
                                    } catch (NumberFormatException e) {
                                        VEAMod.LOGGER.error("Unable to parse value '{}' for '{}' of type int", fields[1], fields[0], e);
                                    }
                                } else if (VEAMod.configMaps.get(fields[0]).type == double.class) {
                                    try {
                                        VEAMod.configMaps.get(fields[0]).setValue(Double.parseDouble(fields[1]));
                                    } catch (NumberFormatException e) {
                                        VEAMod.LOGGER.error("Unable to parse value '{}' for '{}' of type double", fields[1], fields[0], e);
                                    }
                                } else {
                                    try {
                                        VEAMod.configMaps.get(fields[0]).setValue(Float.parseFloat(fields[1]));
                                    } catch (NumberFormatException e) {
                                        VEAMod.LOGGER.error("Unable to parse value '{}' for '{}' of type float", fields[1], fields[0], e);
                                    }
                                }
                            } else {
                                VEAMod.LOGGER.info("Value '{}' not accepted for '{}', using default", fields[1], fields[0]);
                            }
                            confOptions.remove(fields[0]);
                        } else {
                            VEAMod.LOGGER.info("No value set for setting '{}', writing defaults", fields[0]);
                            writeSettingToConf(fields[0], VEAMod.defaultConfigs.get(fields[0]).getAsString(), server);
                        }
                    } else
                        VEAMod.LOGGER.info("'{}' not a configurable setting", fields[0]);
                }
                if (!confOptions.isEmpty()) {
                    VEAMod.LOGGER.info("Config file is missing settings, writing defaults");
                    for (String s : confOptions)
                        writeSettingToConf(s, VEAMod.defaultConfigs.get(s).getAsString(), server);
                }
                VEAMod.LOGGER.info("Successfully loaded configurations");
            } catch (NoSuchFileException e) {
                try {
                    VEAMod.LOGGER.info("No existing config file, generating defaults for vanilla_enchant_additions.conf");
                    Files.createFile(path);
                    writeDefaults(server);
                } catch (IOException e1) {
                    VEAMod.LOGGER.error("Unable to generate defaults for vanilla_enchant_additions.conf",  e);
                }
            }
            catch (IOException e) {
                VEAMod.LOGGER.error("Something went wrong while reading from vanilla_enchant_additions.conf",  e);
            }
        }
    }

    private static boolean defaultContains(String q) {
        for (Map.Entry<String,ConfigOpt> entry : VEAMod.defaultConfigs.entrySet())
            if (q.matches(entry.getKey())) return true;
        return false;
    }

    public static ArrayList<String> defaultOpts() {
        return new ArrayList<>(Arrays.asList(VEAMod.optionList));
    }

    private static Constable parseBool(String key, String value) {
        if (!value.matches("(true|false)")) {
            VEAMod.LOGGER.info("Unable to parse value '{}' for '{}'", value, key);
            return null;
        }
        return value.matches("true");
    }

}
