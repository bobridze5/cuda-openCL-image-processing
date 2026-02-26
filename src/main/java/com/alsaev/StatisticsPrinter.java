package com.alsaev;

public class StatisticsPrinter {
    public static void print(String fileName, long duration) {
        printTitle(fileName);
        System.out.printf("Время:\t%s\n", duration);
    }

    public static void print(String fileName, double duration) {
        printTitle(fileName);
        System.out.printf("Время:\t%f\n", duration);
    }

    public static void print(String fileName, Timer timer) {
        printTitle(fileName);
        printTime(timer);
    }

    public static void print(String fileName, Timer... timers) {
        printTitle(fileName);

        long sumNano = 0;
        double sumMs = 0;
        double sumSec = 0;

        int length = timers.length;

        for (int i = 0; i < length; i++) {
            System.out.println("Запуск №" + (i + 1));
            printTime(timers[i]);
            sumNano += timers[i].getDurationNano();
            sumMs += timers[i].getDurationMs();
            sumSec += timers[i].getDurationSec();
        }
        System.out.println("-".repeat(20));
        System.out.println("Среднее значение:");
        System.out.printf("Наносекунды:  \t%f\n", (double) sumNano / length);
        System.out.printf("Микросекунды:\t%f\n", sumMs / length);
        System.out.printf("Секунды:      \t%f\n\n", sumSec / length);

    }

    private static void printTime(Timer timer) {
        System.out.printf("Время в наносекундах:  \t%d\n", timer.getDurationNano());
        System.out.printf("Время в микросекундах: \t%f\n", timer.getDurationMs());
        System.out.printf("Время в секундах:      \t%f\n", timer.getDurationSec());
    }


    private static void printTitle(String fileName) {
        System.out.println("-".repeat(fileName.length() + 4));
        System.out.println("| " + fileName + " |");
        System.out.println("-".repeat(fileName.length() + 4));
    }
}

