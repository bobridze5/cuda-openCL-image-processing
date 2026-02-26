package com.alsaev;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
}
