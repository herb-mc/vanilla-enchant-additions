package com.herb_mc.vanilla_enchant_additions;

import com.herb_mc.vanilla_enchant_additions.helper.ConfigCondition;
import com.herb_mc.vanilla_enchant_additions.helper.ConfigOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class VEAMod {

    public static Logger LOGGER = LoggerFactory.getLogger("Vanilla Enchant Options");
    public static HashMap<String, ConfigOpt> defaultConfigs = new HashMap<>();
    public static HashMap<String, ConfigOpt> configMaps = new HashMap<>();
    public static String[] optionList = {
            "channelingAlways",
            "configGetPermLevel",
            "extendedImpaling",
            "infinityForAll",
            "multishotBurstDelay",
            "multishotCount",
            "piercingDmgBoost",
            "riptideAlways",
            "soulSpeedBreakChance",
            "voidLoyalty"
    };

    public static ConfigCondition atLeastZeroInteger = in -> isInt(in) && Integer.parseInt(in) >= 0;
    public static ConfigCondition atLeastZeroNumeric = in -> isDouble(in) && Double.parseDouble(in) >= 0;
    public static ConfigCondition permLevel = in -> isInt(in) && Integer.parseInt(in) >= 0 && Integer.parseInt(in) <= 4;
    public static ConfigCondition isBoolean = in -> in.equals("true") || in.equals("false");
    public static ConfigCondition any = in -> true;

    public static String isBool = "must be true/false";
    public static String atLeastZeroError = "must be an integer > 0";
    public static String atLeastZeroNumericError = "must be a double/float > 0";
    public static String permLevelError = "must be an an int >= 0 but <= 4";

    static {
        defaultConfigs.put("channelingAlways", new ConfigOpt(true, isBoolean, boolean.class, isBool));
        defaultConfigs.put("configGetPermLevel", new ConfigOpt(0, permLevel, int.class, permLevelError));
        defaultConfigs.put("extendedImpaling", new ConfigOpt(true, isBoolean, boolean.class, isBool));
        defaultConfigs.put("infinityForAll", new ConfigOpt(true, isBoolean, boolean.class, isBool));
        defaultConfigs.put("multishotBurstDelay", new ConfigOpt(0, atLeastZeroInteger, int.class, atLeastZeroError));
        defaultConfigs.put("multishotCount", new ConfigOpt(0, atLeastZeroInteger, int.class, atLeastZeroError));
        defaultConfigs.put("piercingDmgBoost", new ConfigOpt(0.25, atLeastZeroNumeric, double.class, atLeastZeroNumericError));
        defaultConfigs.put("riptideAlways", new ConfigOpt(true, isBoolean, boolean.class, isBool));
        defaultConfigs.put("soulSpeedBreakChance", new ConfigOpt(0.04f, atLeastZeroNumeric, float.class, atLeastZeroNumericError));
        defaultConfigs.put("voidLoyalty", new ConfigOpt(true, isBoolean, boolean.class, isBool));
    }

    public static boolean isInt(String in) {
        try {
            Integer.parseInt(in);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String in) {
        try {
            Double.parseDouble(in);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
