package com.alsaev;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageAnalyzer {

    private final int testRuns;
    private final Filter<ImageData, BufferedImage> filter;
    private final ImageOperation strategy;
    private final String outputPath;

    private ImageAnalyzer(Builder builder) {
        this.outputPath = builder.outputPath;
        this.testRuns = builder.testRuns;
        this.filter = builder.filter;
        this.strategy = builder.strategy;
    }

    public void analyze(String... images) {
        List<BufferedImage> bufferedImages = ImageUtils.load(images);
        List<ImageData> imageFilteredData = filter.filter(bufferedImages);

        for (int i = 0; i < imageFilteredData.size(); i++) {
            ImageData data = imageFilteredData.get(i);
            ImageData result = null;
            Timer[] timers = new Timer[testRuns];

            for (int j = 0; j < testRuns; j++) {
                Timer timer = new Timer();
                ImageData copy = Utils.deepCopy(data);

                timer.start();
                strategy.apply(copy);
                timer.stop();

                timers[j] = timer;

                if (j == testRuns - 1) {
                    result = copy;
                }
            }

//            ImageUtils.save();
            StatisticsPrinter.print(images[i], timers);

        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private int testRuns;
        private Filter<ImageData, BufferedImage> filter;
        private ImageOperation strategy;
        private String outputPath = "results";

        public Builder setFilter(Filter<ImageData, BufferedImage> filter) {
            this.filter = filter;
            return this;
        }

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