package com.alsaev.programA;

import com.alsaev.ImageOperation;

import java.awt.image.BufferedImage;

public class CudaDilateOperation implements ImageOperation {

    private final int threshold;
    private final int step;

    public CudaDilateOperation(int threshold, int step) {
        this.threshold = threshold;
        this.step = step;
    }

    @Override
    public byte[] apply(byte[] image) {
        // Здесь логика CUDA
        return null;
    }


}
