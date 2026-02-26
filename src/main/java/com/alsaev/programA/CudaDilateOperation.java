package com.alsaev.programA;

import com.alsaev.ImageData;
import com.alsaev.ImageOperation;

public class CudaDilateOperation implements ImageOperation {
    private final int step;

    public CudaDilateOperation(int step) {
        validateStep(step);
        this.step = step;
    }

    @Override
    public byte[] apply(ImageData data) {
        System.out.println("Здесь пока пусто");
        // Здесь логика CUDA
        return null;
    }

    private void validateStep(int step) {
        if (step < 1 || step > 3) {
            throw new IllegalArgumentException("Значение шага должно быть в пределах от 1 до 3 включительно");
        }
    }

}
