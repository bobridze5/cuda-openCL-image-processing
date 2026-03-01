package com.alsaev.utils;

import com.alsaev.analyzer.AnalysisReport;
import com.alsaev.analyzer.ImageReport;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.List;

public final class StatisticsUtils {
    public static void saveTo(AnalysisReport report, String outputPath) {
        report.reports().forEach(i -> {
            String fileName = Paths.get(i.sourcePath()).getFileName().toString();

            i.result().forEach(arr -> {
                BufferedImage image = ImageUtils.toBufferedImage(arr, i.width(), i.height());
                try {
                    ImageUtils.save(image, outputPath + fileName);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        });
    }

    public static void print(AnalysisReport report, PrintStream stream) {
        report.reports().forEach(i -> StatisticsPrinter.print(stream, i.sourcePath(), i.timers()));
    }
}
