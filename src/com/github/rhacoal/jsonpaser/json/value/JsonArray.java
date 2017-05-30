package com.github.rhacoal.jsonpaser.json.value;

import java.util.ArrayList;

public class JsonArray extends ArrayList<JsonValue> implements JsonValue {

    @Override
    public int type() {
        return ARRAY;
    }

    @Override
    public String encode(boolean compact) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        if (!isEmpty()) {
            for (JsonValue value : this) {
                stringBuilder.append(value.encode(compact))
                        .append(compact ? "," : ", ");
            }
            stringBuilder.delete(stringBuilder.length() - (compact ? 1 : 2), stringBuilder.length());
        }
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

}
