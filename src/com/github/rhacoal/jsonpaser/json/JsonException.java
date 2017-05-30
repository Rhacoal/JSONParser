package com.github.rhacoal.jsonpaser.json;

public class JsonException extends RuntimeException {

    public static final int ILLEGAL_NUMBER = 0;
    public static final int ILLEGAL_BOOL = 1;
    public static final int COMMA_NOT_FOUND = 2;
    public static final int ILLEGAL_ESCAPE = 3;
    public static final int COLON_NOT_FOUND = 4;
    public static final int UNEXPECTED_CONTROL_CHAR = 5;
    public static final int ILLEGAL_NULL = 6;
    public static final int OUT_OF_BOUND = 100;

    private final int index;
    private final int type;

    JsonException(String msg, int index, int type) {
        super(msg);
        this.index = index;
        this.type = type;
    }

}
