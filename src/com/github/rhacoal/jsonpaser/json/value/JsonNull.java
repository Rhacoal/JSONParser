package com.github.rhacoal.jsonpaser.json.value;

public class JsonNull implements JsonValue {

    public static final JsonNull NULL = new JsonNull();

    private JsonNull(){}

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public int type() {
        return JsonValue.NULL;
    }

    @Override
    protected Object clone() {
        return this;
    }

    @Override
    public String toString() {
        return "null";
    }
}
