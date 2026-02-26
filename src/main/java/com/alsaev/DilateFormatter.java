package com.alsaev;

import java.util.ArrayList;
import java.util.List;

public class DilateFormatter extends BaseFormatter {
    public DilateFormatter(int threadCount) {
        super(threadCount);
    }

    public void dilate(byte[][] pixels, int width, int height, int step) {
        validateStep(step);
        byte[][] source = Utils.deepCopy(pixels);
        if (threadCount > 1) {
            applyDilateParallel(source, pixels, width, height, step);
        } else {
            applyDilate(source, pixels, 0, width, 0, height, step);
        }
    }

    private void applyDilateParallel(byte[][] source, byte[][] target, int width, int height, int step) {
        int rows = (int) Math.sqrt(threadCount);
        int cols = (int) Math.ceil((double) threadCount / rows);

        int tileWidth = width / cols;
        int tileHeight = height / rows;

        List<Thread> threads = new ArrayList<>();

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                if (threads.size() >= threadCount) break;

                int xStart = x * tileWidth;
                int xEnd = (x == cols - 1) ? width : (x + 1) * tileWidth;
                int yStart = y * tileHeight;
                int yEnd = (y == rows - 1) ? height : (y + 1) * tileHeight;

                Thread thread = new Thread(() -> applyDilate(source, target, xStart, xEnd, yStart, yEnd, step));
                threads.add(thread);
            }
        }

        threads.forEach(Thread::start);

        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

    private void applyDilate(
            byte[][] source,
            byte[][] target,
            int xStart,
            int xEnd,
            int yStart,
            int yEnd,
            int step
    ) {
        int width = source[0].length;
        int height = source.length;

        for (int y = yStart; y < yEnd; y++) {
            for (int x = xStart; x < xEnd; x++) {
                if (source[y][x] != 0) {
                    for (int dy = -step; dy <= step; dy++) {
                        for (int dx = -step; dx <= step; dx++) {
                            int nx = x + dx;
                            int ny = y + dy;

                            if (nx < 0 || nx >= width || ny < 0 || ny >= height) {
                                continue;
                            }

                            target[ny][nx] = (byte) 1;
                        }
                    }
                }
            }
        }
    }

    private void validateStep(int step) {
        if (step < 1 || step > 3) {
            throw new IllegalArgumentException("Шаг должен быть 1, 2 или  3");
        }
    }
}
