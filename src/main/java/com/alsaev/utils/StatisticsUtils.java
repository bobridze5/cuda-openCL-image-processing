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
    private static int counter = 1;

    public static void saveTo(ImageReport report, String outputPath) {
        String fileName = Paths.get(report.sourcePath()).getFileName().toString();

        report.result().forEach(arr -> {
            BufferedImage image = ImageUtils.toBufferedImage(arr, report.width(), report.height());

            try {
                ImageUtils.save(image, outputPath + counter++ + "__" + fileName);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static void saveTo(AnalysisReport report, String outputPath) {
        report.reports().forEach(i -> {
            String fileName = Paths.get(i.sourcePath()).getFileName().toString();

            i.result().forEach(arr -> {
                BufferedImage image = ImageUtils.toBufferedImage(arr, i.width(), i.height());
                try {
                    ImageUtils.save(image, outputPath + counter++ + "__" + fileName);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        });
    }

    public static void print(AnalysisReport report, PrintStream stream) {
        report.reports().forEach(i -> StatisticsPrinter.print(stream, i.sourcePath(), i.timers()));
    }

    public static void print(ImageReport report, PrintStream stream) {
        StatisticsPrinter.print(stream, report.sourcePath(), report.timers());
    }
}
