package com.alsaev.filters;

import java.util.List;

public interface Filter<R, V> {
    R filter(V value);

    default List<R> filter(List<V> values) {
        return values.stream().map(this::filter).toList();
    }
}
