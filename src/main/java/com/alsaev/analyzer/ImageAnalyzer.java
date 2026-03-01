package com.alsaev.analyzer;

import com.alsaev.filters.Filter;
import com.alsaev.operations.ImageOperation;
import com.alsaev.utils.ImageData;
import com.alsaev.utils.ImageUtils;
import com.alsaev.utils.Timer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageAnalyzer implements Analyzer<AnalysisReport, String> {
    private final ImageOperation<? extends Filter<ImageData, BufferedImage>> strategy;
    private final int testRuns;

    public ImageAnalyzer(ImageOperation<? extends Filter<ImageData, BufferedImage>> operation) {
        this(operation, 1);
    }

    public ImageAnalyzer(ImageOperation<? extends Filter<ImageData, BufferedImage>> operation, int testRuns) {
        this.strategy = operation;
        this.testRuns = testRuns;
    }

    @Override
    public AnalysisReport analyze(List<String> paths) {
        Timer globalTimer = new Timer();
        globalTimer.start();

        List<BufferedImage> bufferedImages = ImageUtils.load(paths);
        List<ImageData> imageFilteredData = strategy.getFilter().filter(bufferedImages);

        List<ImageReport> reports = new ArrayList<>();

        for (int i = 0; i < imageFilteredData.size(); i++) {
            ImageData data = imageFilteredData.get(i);
            String currentPath = paths.get(i);

            AnalysisResult benchmarkResults = runBenchmark(data);

            reports.add(new ImageReport(
                    currentPath,
                    data.width(),
                    data.height(),
                    benchmarkResults.timers(),
                    benchmarkResults.result()
            ));
        }

        globalTimer.stop();

        return new AnalysisReport(
                strategy.getClass().getSimpleName(),
                globalTimer.getDurationNano(),
                reports
        );
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