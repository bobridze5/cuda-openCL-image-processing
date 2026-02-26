package com.alsaev;

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

    public static ImageData deepCopy(ImageData data) {
        // TOOD: добить
        return new ImageData(null, null, null);
    }
}
