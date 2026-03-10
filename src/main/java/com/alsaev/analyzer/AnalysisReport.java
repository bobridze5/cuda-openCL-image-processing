package com.alsaev.analyzer;

import java.util.List;

public record AnalysisReport(
        String name,
        long totalExecutionTime,
        List<ImageReport> reports
) {
}
