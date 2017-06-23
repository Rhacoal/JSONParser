package com.github.rhacoal.jsonpaser.json;

import com.github.rhacoal.jsonpaser.json.value.*;

import java.util.logging.Logger;

public class JsonParser {

    private final boolean strictMode;

    private final String json;
    private final int length;
    private int currentIndex = 0;

    private Logger logger;

    public JsonParser(String json) {
        this.json = json;
        strictMode = false;
        length = json.length();
    }

    public JsonParser(String json, boolean strict) {
        this.json = json;
        this.strictMode = strict;
        length = json.length();
    }

    public boolean isStrict() {
        return strictMode;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public JsonValue parse() throws JsonException {
        return parseValue();
    }

    private void skipBlankCharacters() {
        while (isBlank(currentChar())) {
            ++ currentIndex;
        }
    }

    private JsonNumber parseNumber() {
        if (strictMode) {
            return strictNumberParse();
        } else {
            int begin = currentIndex;
            while (isDigit(charAt(currentIndex))) {
                ++ currentIndex;
            }
            return new JsonNumber(Double.parseDouble(json.substring(begin, currentIndex)));
        }
    }

    private JsonNumber strictNumberParse() {
        int begin = currentIndex;
        boolean zero = false;
        if (!isNumber(currentChar())) {
            char ch = currentChar();
            if (ch == '-') {
                ++ currentIndex;
            } else {
                throwIllegalNumberException();
            }
        }
        if (currentChar() == '0') {
            zero = true;
            ++ currentIndex;
        }
        while (isNumber(charAt(currentIndex))) {
            if (zero) throwIllegalNumberException();
            ++ currentIndex;
        }
        char cur = currentChar();
        if (cur == '.') {
            ++ currentIndex;
            while (isNumber(charAt(currentIndex))) {
                ++ currentIndex;
            }
        }
        if (cur == 'e' || cur == 'E') {
            ++ currentIndex;
            cur = currentChar();
            if (cur == '+' || cur == '-') {
                ++ currentIndex;
            }
            while (isNumber(charAt(currentIndex))) {
                ++ currentIndex;
            }
        }
        return new JsonNumber(Double.parseDouble(json.substring(begin, currentIndex)));
    }
    
    private JsonObject parseObject() {
        info("parsing Object at " + currentIndex);
        ++ currentIndex;
        skipBlankCharacters();
        JsonObject jo = new JsonObject();
        //start of Object parse
        while (currentChar() != '}') {
            info("parsing key at " + currentIndex);
            JsonString key = parseString();
            skipColon();
            info("parsing value at " + currentIndex);
            JsonValue value = parseValue();
            skipComma();
            jo.put(key, value);
        }
        ++ currentIndex;
        return jo;
    }

    private JsonString parseString() {
        StringBuilder stringBuilder = new StringBuilder();
        boolean escape = false;
        char cur;
        ++ currentIndex;
        for (;; ++currentIndex) {
            cur = currentChar();
            if (escape) {
                escape = false;
                switch (currentChar()) {
                    case '"':
                        stringBuilder.append('"');
                        break;
                    case '\\':
                        stringBuilder.append('\\');
                        break;
                    case '/':
                        stringBuilder.append('/');
                        break;
                    case 'b':
                        stringBuilder.append('\b');
                        break;
                    case 'f':
                        stringBuilder.append('\f');
                        break;
                    case 'n':
                        stringBuilder.append('\n');
                        break;
                    case 'r':
                        stringBuilder.append('\r');
                        break;
                    case 't':
                        stringBuilder.append('\t');
                        break;
                    case 'u':
                        stringBuilder.append(parseSurrogatePairs());
                        break;
                    default:
                        if (strictMode) {
                            throw new JsonException("Unexpected escape character: '" + currentChar() + "' at " + currentIndex, currentIndex, JsonException.ILLEGAL_ESCAPE);
                        } else {
                            warning("Unexpected escape character: '" + currentChar() + "' at " + currentIndex);
                        }
                }
            } else {
                if (cur == '"') {
                    ++ currentIndex;
                    break;
                } else if (cur == '\\') {
                    escape = true;
                } else if (Character.isISOControl(cur)) {
                    if (strictMode) {
                        throw new JsonException("Unexpected control character: 0x" + Integer.toHexString(cur) + " at " + currentIndex, currentIndex, JsonException.UNEXPECTED_CONTROL_CHAR);
                    }
                    ++ currentIndex;
                    break;
                } else {
                    stringBuilder.append(cur);
                }
            }
        }
        return new JsonString(stringBuilder.toString());
    }

    private JsonArray parseArray() {
        info("parsing Array at " + currentIndex);
        ++ currentIndex;
        skipBlankCharacters();
        JsonArray ja = new JsonArray();
        //start of Array parse;
        while (currentChar() != ']') {
            info("parsing element at " + currentIndex);
            JsonValue item = parseValue();
            skipComma();
            ja.add(item);
        }
        ++ currentIndex;
        return ja;
    }

    private JsonBoolean parseTrue() {
        if (strictMode) {
            if (!json.substring(currentIndex, currentIndex + 4).equals("true")) {
                throw new JsonException("Illegal value. Expect 'true' here. (" + currentIndex + ")", currentIndex, JsonException.ILLEGAL_BOOL);
            }
            currentIndex += 4;
        }
        skipLetters();
        return JsonBoolean.TRUE;
    }

    private JsonBoolean parseFalse() {
        if (strictMode) {
            if (!json.substring(currentIndex, currentIndex + 5).equals("false")) {
                throw new JsonException("Illegal value. Expect 'false' here. (" + currentIndex + ")", currentIndex, JsonException.ILLEGAL_BOOL);
            }
            currentIndex += 5;
        }
        skipLetters();
        return JsonBoolean.FALSE;
    }

    private JsonNull parseNull() {
        if (strictMode) {
            if (!json.substring(currentIndex, currentIndex + 4).equals("null")) {
                throw new JsonException("Illegal value. Expect 'null' here. (" + currentIndex + ")", currentIndex, JsonException.ILLEGAL_BOOL);
            }
            currentIndex += 4;
        }
        skipLetters();
        return JsonNull.NULL;
    }

    private JsonValue parseValue() {
        switch (currentChar()) {
            case '"':
                return parseString();
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case 't':
                return parseTrue();
            case 'f':
                return parseFalse();
            case 'n':
                return parseNull();
            default:
                return parseNumber();
        }
    }

    private char[] parseSurrogatePairs() {
        char[] val = parseSurrogate(json, currentIndex - 1);
        currentIndex += val.length * 6 - 3;
        return val;
    }

    private boolean skipComma() {
        skipBlankCharacters();
        if (currentChar() != ',') {
            if (currentChar() == '}' || currentChar() == ']') {
                return false;
            }
            if (strictMode) {
                throw new JsonException("Comma expected here. (" + currentIndex + ")", currentIndex, JsonException.COMMA_NOT_FOUND);
            }
        } else {
            ++ currentIndex;
        }
        skipBlankCharacters();
        return true;
    }

    private void skipColon() {
        skipBlankCharacters();
        if (currentChar() != ':') {
            if (strictMode) {
                throw new JsonException("Colon expected here. (" + currentIndex + ")", currentIndex, JsonException.COLON_NOT_FOUND);
            }
        } else {
            ++ currentIndex;
        }
        skipBlankCharacters();
    }

    private void skipLetters() {
        while (isLetter(currentChar())) {
            ++ currentIndex;
        }
    }

    private void throwIllegalNumberException() {
        throw new JsonException("Illegal number at " + currentIndex, currentIndex, JsonException.ILLEGAL_NUMBER);
    }

    private char charAt(int index) {
        if (index >= length || index < 0) {
            throw new JsonException("Out of bound (" + index + ")", index, JsonException.OUT_OF_BOUND);
        }
        return json.charAt(index);
    }

    private char currentChar() {
        if (currentIndex == length) {
            throw new JsonException("Out of bound (" + currentIndex + ")", currentIndex, JsonException.OUT_OF_BOUND);
        }
        return json.charAt(currentIndex);
    }

    private void warning(String msg) {
        if (logger != null) {
            logger.warning(msg);
        }
    }

    private void info(String msg) {
        if (logger != null) {
            logger.info(msg);
        }
    }

    private static boolean isBlank(char ch) {
        return ch == ' ' || ch == '\n' || ch == '\r' || ch == '\t';
    }

    private static boolean isDigit(char ch) {
        return (ch >= '0' && ch <= '9') || ch == 'e' || ch == 'E' || ch == '+' || ch == '-' || ch == '.';
    }

    private static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static boolean isLetter(char ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z');
    }

    private static int parseHex(String hex, int offset, int len) {
        if (len > 8) { //cannot be contained in int
            offset += len - 8;
        }
        int val = 0;
        for (int i = 0; i != len; ++ i) {
            char ch = hex.charAt(i + offset);
            int digit = 0;
            if (ch >= '0' && ch <= '9') {
                digit = ch - '0';
            } else if (ch >= 'A' && ch <= 'Z') {
                digit = ch - 'A' + 10;
            } else if (ch >= 'a' && ch <= 'z') {
                digit = ch - 'a' + 10;
            }
            val |= digit << (len - i - 1) * 4;
        }
        return val;
    }

    private static char[] parseSurrogate(String surrogate, int offset) {
        //assumes that the currentIndex is pointing at the '\' of '\uFFFF'
        int point1 = parseHex(surrogate, offset + 2, 4);
        if (point1 >= 0xD800 && point1 <= 0xDBFF) {
            int point2 = parseHex(surrogate, offset + 8, 4);
            return new char[]{(char)point1, (char)point2};
        } else {
            return new char[]{(char)point1};
        }
    }
}
