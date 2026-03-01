package com.alsaev.analyzer;

import com.alsaev.filters.Filter;
import com.alsaev.operations.ImageOperation;
import com.alsaev.utils.ImageData;
import com.alsaev.utils.ImageUtils;
import com.alsaev.utils.StatisticsUtils;
import com.alsaev.utils.Timer;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class ImageAnalyzer implements Analyzer<String>, AutoCloseable {
    private final ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy;
    private final String outputPath;
    private final int testRuns;
    private final PrintStream stream;

    private ImageAnalyzer(Builder builder) {
        this.strategy = builder.strategy;
        this.testRuns = builder.testRuns;
        this.outputPath = builder.outputPath;
        this.stream = builder.stream;
    }

    @Override
    public void analyze(List<String> paths) {
        for (String path : paths) {
            try {
                BufferedImage bufferedImage = ImageUtils.load(path);
                ImageData data = strategy.getFilter().filter(bufferedImage);

                AnalysisResult benchmarkResults = runBenchmark(data);

                ImageReport report = new ImageReport(
                        path,
                        data.width(),
                        data.height(),
                        benchmarkResults.timers(),
                        benchmarkResults.result()
                );

                StatisticsUtils.saveTo(report, outputPath);
                StatisticsUtils.print(report, stream);

            } catch (IOException e) {
                System.err.printf("Пропуск файла %s: %s%n", path, e.getMessage());
                continue;
            }
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void close() {
        if (strategy != null) {
            strategy.close();
        }
    }

    public static class Builder {
        private ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy;
        private int testRuns = 1;
        private String outputPath = "results/";
        private PrintStream stream = System.out;

        public Builder setTestRuns(int testRuns) {
            this.testRuns = testRuns;
            return this;
        }

        public Builder setPrintStream(PrintStream stream) {
            this.stream = stream;
            return this;
        }

        public Builder setOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder setOperation(ImageOperation<? extends Filter<ImageData, BufferedImage>> operation) {
            this.strategy = operation;
            return this;
        }

        public ImageAnalyzer build() {
            if (strategy == null) {
                throw new IllegalArgumentException("Операция не задана");
            }
            return new ImageAnalyzer(this);
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
}