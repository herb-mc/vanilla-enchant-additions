package com.herb_mc.vanilla_enchant_additions;

import com.herb_mc.vanilla_enchant_additions.helper.ConfigOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class VEAMod {

    public static Logger LOGGER = LoggerFactory.getLogger("Vanilla Enchant Options");
    public static HashMap<String, ConfigOpt> defaultConfigs = new HashMap<>();
    public static HashMap<String, ConfigOpt> configMaps = new HashMap<>();
    public static String[] optionList = {"channelingAlways", "configGetPermLevel", "extendedImpaling", "infinityForAll", "piercingDmgBoost", "riptideAlways", "soulSpeedBreakChance", "voidLoyalty"};
    public static String[] permLevel = {"0","1","2","3","4"};
    public static String[] bool = {"true", "false"};
    public static String[] any = {};

    static {
        defaultConfigs.put("channelingAlways", new ConfigOpt(true, bool, boolean.class));
        defaultConfigs.put("configGetPermLevel", new ConfigOpt(0, permLevel, int.class));
        defaultConfigs.put("extendedImpaling", new ConfigOpt(true, bool, boolean.class));
        defaultConfigs.put("infinityForAll", new ConfigOpt(true, bool, boolean.class));
        defaultConfigs.put("piercingDmgBoost", new ConfigOpt(0.25, any, float.class));
        defaultConfigs.put("riptideAlways", new ConfigOpt(true, bool, boolean.class));
        defaultConfigs.put("soulSpeedBreakChance", new ConfigOpt(0.04, any, float.class));
        defaultConfigs.put("voidLoyalty", new ConfigOpt(true, bool, boolean.class));
    }

    public static boolean arrayContains(String[] a, String f) {
        for (String element : a) {
            if (element.equals(f)) {
                return true;
            }
        }
        return false;
    }

}
