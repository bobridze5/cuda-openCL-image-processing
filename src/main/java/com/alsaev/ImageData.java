package com.alsaev;

public record ImageData(
        int width,
        int height,
        byte[] red,
        byte[] green,
        byte[] blue
) {
}
