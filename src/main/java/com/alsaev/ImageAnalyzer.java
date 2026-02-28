package com.alsaev;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
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

    @Override
    public void analyze(List<String> paths) {
        List<BufferedImage> bufferedImages = ImageUtils.load(paths);
        List<ImageData> imageFilteredData = strategy.getFilter().filter(bufferedImages);

        for (int i = 0; i < imageFilteredData.size(); i++) {
            ImageData data = imageFilteredData.get(i);
            Timer[] timers = new Timer[testRuns];
            List<byte[]> result = null;

            for (int j = 0; j < testRuns; j++) {
                Timer timer = new Timer();

                timer.start();
                List<byte[]> current = strategy.apply(data);
                timer.stop();

                timers[j] = timer;
                if (j == testRuns - 1) {
                    result = current;
                }
            }

            if (result != null) {
                String originFileName = Path.of(paths.get(i)).getFileName().toString();
                String baseName = originFileName.substring(0, originFileName.lastIndexOf('.'));

                for (int k = 0; k < result.size(); k++) {
                    BufferedImage image = ImageUtils.toBufferedImage(result.get(k), data.width(), data.height());

                    String newName = baseName + "_" + k + ".png";
                    String path = Path.of(outputPath, newName).toString();

                    try {
                        ImageUtils.save(image, path);
                    } catch (IOException e) {

                    }
                }
            }

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