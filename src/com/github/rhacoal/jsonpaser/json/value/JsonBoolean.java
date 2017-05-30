package com.github.rhacoal.jsonpaser.json.value;

public final class JsonBoolean implements JsonValue {

    private boolean val;
    public static final JsonBoolean TRUE = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private JsonBoolean(boolean val) {
        this.val = val;
    }

    public boolean value() {
        return val;
    }

    @Override
    public boolean asBoolean() {
        return val;
    }

    @Override
    public int type() {
        return val ? JsonValue.TRUE : JsonValue.FALSE;
    }

    @Override
    protected Object clone() {
        return this;
    }

    @Override
    public String toString() {
        return this == TRUE ? "true" : "false";
    }
}
