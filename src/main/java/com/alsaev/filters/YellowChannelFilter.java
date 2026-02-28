package com.alsaev.filters;

import com.alsaev.analyzer.ImageData;
import com.alsaev.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class YellowChannelFilter implements ChannelsFilter {
    private static final double RATIO = 0.1d;

    @Override
    public ImageData filter(BufferedImage image) {
        int[] intensities = ImageUtils.getIntensity(image);
        int maxIntensity = Arrays.stream(intensities).summaryStatistics().getMax();

        int w = image.getWidth();
        int h = image.getHeight();

        ImageData data = ImageUtils.getChannels(image);

        byte[] red = data.red();
        byte[] green = data.green();
        byte[] blue = data.blue();


        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);
                int intensity = ImageUtils.getIntensity(rgb);
                int idx = y * w + x;

                if (intensity < maxIntensity * RATIO) {
                    red[idx] = green[idx] = blue[idx] = 0;
                }
            }
        }

        return new ImageData(w, h, red, green, blue);
    }
}
