package com.herb_mc.vanilla_enchant_additions.helper;

public class ConfigOpt {

    public final Class type;

    private Object value;

    private String[] acceptedValues;

    public ConfigOpt(Object in, String[] arr, Class t) {
        value = in;
        acceptedValues = arr;
        type = t;
    }

    public void setValue(Object in) {
        value = in;
    }

    public void setAcceptedValues(String[] in) {
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

    public String[] getAcceptedValues() {
        return acceptedValues;
    }

}