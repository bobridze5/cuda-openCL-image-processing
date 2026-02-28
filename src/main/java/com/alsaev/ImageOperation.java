package com.alsaev;

import java.awt.image.BufferedImage;
import java.util.List;

public interface ImageOperation<F extends Filter<ImageData, BufferedImage>> {
    List<byte[]> apply(ImageData data);

    F getFilter();
}
