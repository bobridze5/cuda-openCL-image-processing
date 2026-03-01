package com.alsaev.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourceUtils {
    public static String getAbsolutePath(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                return path.toAbsolutePath().toString();
            }

            URL resource = ResourceUtils.class.getClassLoader().getResource(fileName);
            if (resource == null) {
                throw new FileNotFoundException("Файл не найден ни по пути, ни в ресурсах: " + fileName);
            }

            return Paths.get(resource.toURI()).toAbsolutePath().toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Некорректный синтаксис пути для файла: " + fileName, e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
