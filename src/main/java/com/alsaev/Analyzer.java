package com.alsaev;

import java.util.List;

public interface Analyzer<T> {
    void analyze(List<T> values);
}
