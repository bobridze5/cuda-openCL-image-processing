package com.alsaev.utils;

import java.io.PrintStream;

public final class StatisticsPrinter {
    private static final String LINE = "-".repeat(20);

    public static void print(PrintStream out, String filePath, long duration) {
        printTitle(out, filePath);
        System.out.printf("Время:\t%s\n", duration);
    }

    public static void print(PrintStream out, String filePath, double duration) {
        printTitle(out, filePath);
        System.out.printf("Время:\t%f\n", duration);
    }

    public static void print(PrintStream out, String filePath, Timer timer) {
        printTitle(out, filePath);
        printTime(out, timer);
    }

    public static void print(PrintStream out, String filePath, Timer... timers) {
        printTitle(out, filePath);

        long sumNano = 0;
        double sumMs = 0;
        double sumSec = 0;

        int length = timers.length;

        for (int i = 0; i < length; i++) {
            out.println("Запуск №" + (i + 1));
            printTime(out, timers[i]);
            sumNano += timers[i].getDurationNano();
            sumMs += timers[i].getDurationMs();
            sumSec += timers[i].getDurationSec();
        }
        out.println("-".repeat(20));
        out.println("Среднее значение:");
        out.printf("Наносекунды:  \t%f\n", (double) sumNano / length);
        out.printf("Микросекунды:\t%f\n", sumMs / length);
        out.printf("Секунды:      \t%f\n\n", sumSec / length);

    }

    private static void printTime(PrintStream out, Timer timer) {
        out.printf("Наносекунды:  \t%d\n", timer.getDurationNano());
        out.printf("Микросекунды: \t%f\n", timer.getDurationMs());
        out.printf("Секунды:      \t%f\n", timer.getDurationSec());
    }


    private static void printTitle(PrintStream out, String title) {
        String border = "=".repeat(title.length() + 4);
        out.println(border);
        out.println("| " + title + " |");
        out.println(border);
    }
}

