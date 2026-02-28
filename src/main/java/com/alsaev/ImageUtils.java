package com.alsaev;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Watchable;
import java.util.Arrays;
import java.util.List;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static BufferedImage load(String pathStr) throws IOException {
        return load(Path.of(pathStr));
    }

    public static BufferedImage load(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Файл не найден: " + path.toAbsolutePath());
        }

        BufferedImage image = ImageIO.read(path.toFile());

        if (image == null) {
            throw new IOException("Неподдерживаемый формат: " + path);
        }

        return image;
    }

    public static List<BufferedImage> load(List<String> paths) {
        return load(paths.toArray(String[]::new));
    }

    public static List<BufferedImage> load(String... paths) {
        return Arrays.stream(paths)
                .map(Path::of)
                .map(path -> {
                    try {
                        return load(path);
                    } catch (IOException e) {
                        throw new UncheckedIOException("Ошибка при загрузки файла: " + path, e);
                    }
                })
                .toList();
    }

    public static void save(BufferedImage image, String outputPath) {

    }

    public static BufferedImage toBufferedImage(byte[] channel, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] rgb = new int[height * width];

        for (int i = 0; i < channel.length; i++) {
            int value = channel[i] & 0xFF;
            rgb[i] = (value << 16) | (value << 8) | value;
        }

        image.setRGB(0, 0, width, height, rgb, 0, width);
        return image;
    }

    public static List<ImageData> getChannels(List<BufferedImage> images) {
        return images.stream().map(ImageUtils::getChannels).toList();
    }

    public static int[] getIntensity(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        int[] result = new int[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);
                result[y * w + x] = getIntensity(rgb);
            }
        }

        return result;
    }

    public static ImageData getChannels(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        byte[] r = new byte[w * h];
        byte[] g = new byte[w * h];
        byte[] b = new byte[w * h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);
                r[y * w + x] = (byte) ((rgb >> 16) & 0xFF);
                g[y * w + x] = (byte) ((rgb >> 8) & 0xFF);
                b[y * w + x] = (byte) (rgb & 0xFF);

            }
        }

        return new ImageData(w, h, r, g, b);
    }


    public static int getIntensity(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        return (r + g + b) / 3;
    }
}
