package com.github.rhacoal.jsonpaser.json.value;

public class JsonNumber implements JsonValue {

    private final double val;

    public JsonNumber(double val) {
        this.val = val;
    }

    public double value() {
        return val;
    }

    @Override
    public int type() {
        return NUMBER;
    }

    @Override
    public String toString() {
        return Double.toString(val);
    }

}
