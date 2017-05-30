package com.github.rhacoal.jsonpaser.json.value;

public class JsonString implements JsonValue {

    private final String val;

    public JsonString(String val) {
        this.val = val;
    }

    public String value() {
        return val;
    }

    @Override
    public int type() {
        return STRING;
    }

    @Override
    public int hashCode() {
        return val.hashCode();
    }

    @Override
    public String toString() {
        return val;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof JsonString)) {
            return false;
        } else {
            return ((JsonString) obj).val.equals(val);
        }
    }

    @Override
    public String encode(boolean compact) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"');
        for (int i = 0; i != val.length(); ++ i) {
            char cur = val.charAt(i);
            switch (cur) {
                case '"':
                    stringBuilder.append('\"');
                    break;
                case '\\':
                    stringBuilder.append("\\\\");
                    break;
                case '\b':
                    stringBuilder.append("\\b");
                    break;
                case '\f':
                    stringBuilder.append("\\f");
                    break;
                case '\n':
                    stringBuilder.append("\\n");
                    break;
                case '\r':
                    stringBuilder.append("\\r");
                    break;
                case '\t':
                    stringBuilder.append("\\t");
                    break;
                default:
                    if (cur < 32 || cur > 127) {
                        stringBuilder.append("\\u").append(Integer.toHexString(cur).toUpperCase());
                    } else {
                        stringBuilder.append(cur);
                    }
            }
        }
        stringBuilder.append('"');
        return stringBuilder.toString();
    }

}
