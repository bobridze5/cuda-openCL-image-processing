package com.alsaev.analyzer;

import com.alsaev.utils.Timer;

import java.util.List;

public record ImageReport(
        String sourcePath,
        String baseName,
        int width,
        int height,
        Timer[] timers,
        List<byte[]> result
) {
}
