package com.alsaev;

public class ImageAnalyzer {

    public void analyze(String... images) {

    }
}

/*
 * public void analyze(String[] ImagesPaths) {
 * List<BufferedImage> bufferedImages = ImageUtils.load(ImagesPaths);
 * List<ImageData> imageDataList = filter.filter(bufferedImages);
 * <p>
 * for (int i = 0; i < imageDataList.size(); i++) {
 * ImageData data = imageDataList.get(i);
 * ImageData result = null;
 * Timer[] timers = new Timer[testRuns];
 * <p>
 * for (int k = 0; k < testRuns; k++) {
 * ImageData copy = Utils.deepCopy(data);
 * Timer timer = new Timer();
 * <p>
 * timer.start();
 * operation.apply(copy);
 * timer.end();
 * <p>
 * timers[k] = timer;
 * <p>
 * if (k == testRuns - 1) {
 * result = copy;
 * }
 * }
 * <p>
 * BufferedImage image = ImageUtils.toBufferedImage(result);
 * ImageUtils.save(image, outputPath);
 * <p>
 * StatisticsPrinter.print(ImagesPaths[i], timers);
 * }
 * <p>
 * formatter.shutdown();
 * }
 */