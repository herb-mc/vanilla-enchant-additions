package com.herb_mc.vanilla_enchant_additions.helper;

public class ConfigOpt {

    public final Class type;

    public String failMessage;

    private Object value;

    private ConfigCondition acceptedValues;

    public ConfigOpt(Object in, ConfigCondition arr, Class t, String f) {
        value = in;
        acceptedValues = arr;
        type = t;
        failMessage = f;
    }

    public void setValue(Object in) {
        value = in;
    }

    public void setAcceptedValues(ConfigCondition in) {
        acceptedValues = in;
    }

    public Object getValue() {
        return value;
    }

    public int getInt() {
        return (int) value;
    }

    public float getFloat() {
        return (float) value;
    }

    public double getDouble() {
        return (double) value;
    }

    public boolean getBool() {
        return (boolean) value;
    }

    public String getAsString() {
        return value.toString();
    }

    public ConfigCondition getCondition() {
        return acceptedValues;
    }

}