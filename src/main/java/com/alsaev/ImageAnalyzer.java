package com.alsaev;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageAnalyzer {

    private final int testRuns;
    private final ImageOperation strategy;
    private final String outputPath;

    private ImageAnalyzer(Builder builder) {
        this.outputPath = builder.outputPath;
        this.testRuns = builder.testRuns;
        this.strategy = builder.strategy;
    }

    public void analyze(String... images) {
        List<BufferedImage> bufferedImages = ImageUtils.load(images);
        List<ImageData> imageDataList = ImageUtils.getChannels(bufferedImages);

        for (int i = 0; i < images.length; i++) {
            Timer[] timers = new Timer[testRuns];

            for (int j = 0; j < testRuns; j++) {
                Timer timer = new Timer();

                timer.start();
//                strategy.apply();
                timer.stop();

                timers[j] = timer;
            }

            StatisticsPrinter.print(images[i], timers);
        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private int testRuns;
        private ImageOperation strategy;
        private String outputPath = "results";

        public Builder setOutputPath(String path) {
            this.outputPath = path;
            return this;
        }

        public Builder setTestRuns(int testRuns) {
            this.testRuns = testRuns;
            return this;
        }

        public Builder setStrategy(ImageOperation strategy) {
            this.strategy = strategy;
            return this;
        }

        public ImageAnalyzer build() {
            return new ImageAnalyzer(this);
        }
    }
}

/*
 * public void analyze(String[] ImagesPaths) {
 * List<BufferedImage> bufferedImages = ImageUtils.load(ImagesPaths);
 * List<ImageData> imageDataList = filter.filter(bufferedImages);
 * <p>
 * for (int i = 0; i < imageDataList.size(); i++) {
 * ImageData data = imageDataList.get(i);
 * ImageData result = null;
 * Timer[] timers = new Timer[testRuns];
 * <p>
 * for (int k = 0; k < testRuns; k++) {
 * ImageData copy = Utils.deepCopy(data);
 * Timer timer = new Timer();
 * <p>
 * timer.start();
 * operation.apply(copy);
 * timer.end();
 * <p>
 * timers[k] = timer;
 * <p>
 * if (k == testRuns - 1) {
 * result = copy;
 * }
 * }
 * <p>
 * BufferedImage image = ImageUtils.toBufferedImage(result);
 * ImageUtils.save(image, outputPath);
 * <p>
 * StatisticsPrinter.print(ImagesPaths[i], timers);
 * }
 * <p>
 * formatter.shutdown();
 * }
 */