package com.github.rhacoal.jsonpaser.json.value;

public interface JsonValue {

    int STRING  = 1;
    int NUMBER  = 2;
    int OBJECT  = 3;
    int ARRAY   = 4;
    int TRUE    = 5;
    int FALSE   = 6;
    int NULL    = 7;

    default boolean asBoolean() {
        return false;
    }

    default boolean isNull() {
        return false;
    }

    default JsonObject asObject() {
        return (JsonObject) this;
    }

    default JsonNumber asNumber() {
        return (JsonNumber) this;
    }

    default JsonString asString() {
        return (JsonString) this;
    }

    default JsonArray asArray() {
        return (JsonArray) this;
    }

    int type();

    default String encode(boolean compact) {
        return toString();
    }

    default byte[] bytes() {
        return null;
    }

}
