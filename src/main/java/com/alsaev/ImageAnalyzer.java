package com.alsaev;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ImageAnalyzer implements Analyzer<String> {
    private final ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy;
    private final String outputPath;
    private final int testRuns;

    private ImageAnalyzer(Builder builder) {
        this.outputPath = builder.outputPath;
        this.testRuns = builder.testRuns;
        this.strategy = builder.strategy;
    }

    public void analyze(List<String> paths) {
        List<BufferedImage> bufferedImages = ImageUtils.load(paths);
        List<ImageData> imageFilteredData = strategy.getFilter().filter(bufferedImages);

        for (int i = 0; i < imageFilteredData.size(); i++) {
            ImageData data = imageFilteredData.get(i);
            ImageData result = null;
            Timer[] timers = new Timer[testRuns];

            for (int j = 0; j < testRuns; j++) {
                Timer timer = new Timer();
                ImageData copy = Utils.deepCopy(data);

                timer.start();
                List<byte[]> list = strategy.apply(copy);
                timer.stop();

                timers[j] = timer;

                if (j == testRuns - 1) {
                    result = copy;
                }
            }

//            ImageUtils.toBufferedImage();
//            ImageUtils.save();
            StatisticsPrinter.print(paths.get(i), timers);

        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private int testRuns;
        private ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy;
        private String outputPath = "results";

        public Builder setOutputPath(String path) {
            this.outputPath = path;
            return this;
        }

        public Builder setTestRuns(int testRuns) {
            this.testRuns = testRuns;
            return this;
        }

        public Builder setStrategy(ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy) {
            this.strategy = strategy;
            return this;
        }

        public ImageAnalyzer build() {
            return new ImageAnalyzer(this);
        }
    }
}