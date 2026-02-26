package com.alsaev.programA;

import com.alsaev.Filter;
import com.alsaev.ImageData;
import com.alsaev.ImageUtils;

import java.awt.image.BufferedImage;

public class DilateFilter implements Filter<ImageData, BufferedImage> {

    private final int threshold;

    public DilateFilter(int threshold) {
        validateThreshold(threshold);
        this.threshold = threshold;
    }

    @Override
    public ImageData filter(BufferedImage image) {
        ImageData imageData = ImageUtils.getChannels(image);
        int[] intensity = ImageUtils.getIntensity(image);
        byte[] origin = imageData.red();

        int w = image.getWidth();
        int h = image.getHeight();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int idx = y * w + x;
                origin[idx] = intensity[idx] > threshold ? (byte) 1 : (byte) 0;
            }
        }


        return new ImageData(origin, null, null);
    }

    private void validateThreshold(int intensity) {
        if (intensity < 1 || intensity > 255) {
            throw new IllegalArgumentException("Значение порога должно быть в пределах от 1 до 255 включительно");
        }
    }
}
