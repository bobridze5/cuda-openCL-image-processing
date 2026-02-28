package com.alsaev.analyzer;

import java.util.List;

public interface Analyzer<R, T> {
    R analyze(List<T> values);
}
