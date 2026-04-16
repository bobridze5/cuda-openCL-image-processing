package com.alsaev.operations;

public enum DataType {
    FLOAT32("float32"),
    FLOAT16("float16"),
    INT32("int32"),
    INT16("int16"),
    UINT8("uint8");

    private final String label;

    DataType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
