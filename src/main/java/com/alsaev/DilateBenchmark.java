package com.alsaev;

import com.alsaev.filters.DilateFilter;
import com.alsaev.filters.DilateFilterImpl;
import com.alsaev.operations.DataType;
import com.alsaev.operations.TypedCudaDilateOperation;
import com.alsaev.utils.ImageData;
import com.alsaev.utils.ImageUtils;
import com.alsaev.utils.StatisticsPrinter;
import com.alsaev.utils.Timer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class DilateBenchmark {
    private static final int WARM_UP_RUNS = 3;
    private static final int BENCHMARK_RUNS = 3;
    private static final String RESULTS_DIR = "results";
    private static final String OUTPUT_DIR = "benchmark_output";

    public static void main(String[] args) throws IOException {
        Path path = Path.of(RESULTS_DIR);
        List<String> images = collectImages(path);

        if (images.isEmpty()) {
            System.out.println("Изображения не найдены в папке: " + path.toAbsolutePath());
            return;
        }

        DilateFilter filter = new DilateFilterImpl(140);

        // Сохранение результатов
//        for (DataType type : DataType.values()) {
//            try (TypedCudaDilateOperation op = new TypedCudaDilateOperation(filter, 1, "new_dilate.ptx", type)) {
//                for (String imagePath : images) {
//                    saveResult(op, filter, imagePath);
//                }
//            }
//        }

        for (DataType type : DataType.values()) {
            System.out.println("\n--- [" + type.getLabel() + "] ---");

            try (TypedCudaDilateOperation op = new TypedCudaDilateOperation(filter, 1, "new_dilate.ptx", type)) {
                // Прогрев
                ImageData warmUpData = filter.filter(ImageUtils.load(images.get(0)));
                Object warmUpInput = op.prepareInput(warmUpData);
                for (int i = 0; i < WARM_UP_RUNS; i++) {
                    op.executeOnGpu(warmUpInput, warmUpData.width(), warmUpData.height());
                }

                for (String imagePath : images) {
                    benchmarkImage(op, filter, imagePath);
                }
            }
        }
    }

    private static void saveResult(TypedCudaDilateOperation op, DilateFilter filter, String imagePath) {
        try {
            ImageData data = filter.filter(ImageUtils.load(imagePath));
            byte[] result = op.toBytes(op.executeOnGpu(op.prepareInput(data), data.width(), data.height()));

            String fileName = Path.of(imagePath).getFileName().toString();
            int dot = fileName.lastIndexOf('.');
            String baseName = dot == -1 ? fileName : fileName.substring(0, dot);
            Path outPath = Path.of(OUTPUT_DIR, baseName + "_" + op.getDataType().getLabel() + ".png");

            ImageUtils.save(ImageUtils.toBufferedImage(result, data.width(), data.height()), outPath);
            System.out.println("Сохранено: " + outPath);
        } catch (IOException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static void benchmarkImage(TypedCudaDilateOperation op, DilateFilter filter, String imagePath) {
        ImageData data;
        try {
            data = filter.filter(ImageUtils.load(imagePath));
        } catch (IOException e) {
            System.err.printf("Пропуск %s: %s%n", Path.of(imagePath).getFileName(), e.getMessage());
            return;
        }

        Object input = op.prepareInput(data);

        Timer[] timers = new Timer[BENCHMARK_RUNS];
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            timers[i] = new Timer();
            timers[i].start();
            op.executeOnGpu(input, data.width(), data.height());
            timers[i].stop();
        }

        StatisticsPrinter.print(System.out, imagePath, timers);
    }

    private static List<String> collectImages(Path root) throws IOException {
        if (!Files.isDirectory(root)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(root)) {
            return stream.filter(Files::isRegularFile)
                    .sorted()
                    .map(Path::toString)
                    .toList();
        }
    }
}
