package com.alsaev.analyzer;

import com.alsaev.filters.Filter;
import com.alsaev.operations.ImageOperation;
import com.alsaev.utils.ImageUtils;
import com.alsaev.utils.StatisticsPrinter;
import com.alsaev.utils.Timer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ImageAnalyzer implements Analyzer<AnalysisReport, String> {
    private final ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy;
    private final String outputPath;
    private final int testRuns;

    private ImageAnalyzer(Builder builder) {
        this.outputPath = builder.outputPath;
        this.testRuns = builder.testRuns;
        this.strategy = builder.strategy;
    }

    @Override
    public AnalysisReport analyze(List<String> paths) {
        List<BufferedImage> bufferedImages = ImageUtils.load(paths);
        List<ImageData> imageFilteredData = strategy.getFilter().filter(bufferedImages);

        for (int i = 0; i < imageFilteredData.size(); i++) {
            ImageData data = imageFilteredData.get(i);
            String currentPath = paths.get(i);

            AnalysisResult benchmarkResults = runBenchmark(data);
            saveResults(benchmarkResults.result(), data, currentPath);
            StatisticsPrinter.print(paths.get(i), benchmarkResults.timers);
        }
    }

    private void saveResults(List<byte[]> channels, ImageData data, String sourcePath){



        if (channels != null) {
            String originFileName = Path.of(sourcePath).getFileName().toString();
            String baseName = originFileName.substring(0, originFileName.lastIndexOf('.'));

            for (int k = 0; k < channels.size(); k++) {
                BufferedImage image = ImageUtils.toBufferedImage(channels.get(k), data.width(), data.height());

                String newName = baseName + "_" + k + ".png";
                String path = Path.of(outputPath, newName).toString();

                try {
                    ImageUtils.save(image, path);
                } catch (IOException e) {

                }
            }
        }
    }

    private record AnalysisResult(Timer[] timers, List<byte[]> result) {
    }

    private AnalysisResult runBenchmark(ImageData data) {
        Timer[] timers = new Timer[testRuns];
        List<byte[]> result = null;

        for (int j = 0; j < testRuns; j++) {
            Timer timer = new Timer();

            timer.start();
            result = strategy.apply(data);
            timer.stop();

            timers[j] = timer;
        }

        return new AnalysisResult(timers, result);
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