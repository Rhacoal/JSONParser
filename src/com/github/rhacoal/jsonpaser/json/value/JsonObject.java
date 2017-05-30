package com.github.rhacoal.jsonpaser.json.value;

import java.util.HashMap;

public class JsonObject extends HashMap<JsonString, JsonValue> implements JsonValue {

    @Override
    public int type() {
        return OBJECT;
    }

    public JsonValue get(String key) {
        return get(new JsonString(key));
    }

    @Override
    public String toString() {
        return encode(false);
    }

    public String encode(boolean compact) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('{');
        if (!isEmpty()) {
            for (Entry<JsonString, JsonValue> entry : entrySet()) {
                stringBuilder.append(entry.getKey().encode(compact))
                        .append(compact ? ":" : ": ")
                        .append(entry.getValue().encode(compact))
                        .append(compact ? "," : ", ");
            }
            stringBuilder.delete(stringBuilder.length() - (compact ? 1 : 2), stringBuilder.length());
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

}
