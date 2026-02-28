package com.alsaev.operations;

import com.alsaev.utils.ImageData;
import com.alsaev.filters.Filter;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageOperation<F extends Filter<ImageData, BufferedImage>> {
    List<byte[]> apply(ImageData data);

    F getFilter();
}
