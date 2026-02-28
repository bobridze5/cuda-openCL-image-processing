package com.alsaev.operations;

import com.alsaev.filters.DilateFilter;
import com.alsaev.analyzer.ImageData;

import java.util.List;

public class CudaDilateOperation implements ImageOperation<DilateFilter> {
    private final DilateFilter filter;
    private final int step;

    public CudaDilateOperation(DilateFilter filter, int step) {
        validateStep(step);
        this.filter = filter;
        this.step = step;
    }

    @Override
    public List<byte[]> apply(ImageData data) {
        System.out.println("Здесь пока пусто");
        // Здесь логика CUDA
        return null;
    }

    @Override
    public DilateFilter getFilter() {
        return this.filter;
    }

    private void validateStep(int step) {
        if (step < 1 || step > 3) {
            throw new IllegalArgumentException("Значение шага должно быть в пределах от 1 до 3 включительно");
        }
    }

}
