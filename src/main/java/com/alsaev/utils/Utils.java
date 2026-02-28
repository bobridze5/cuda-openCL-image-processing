package com.alsaev.utils;

public final class Utils {
    public static byte[][] deepCopy(byte[][] source) {
        if (source == null) return null;

        int height = source.length;
        byte[][] result = new byte[height][];
        for (int i = 0; i < height; i++) {
            result[i] = source[i].clone();
        }

        return result;
    }
}
