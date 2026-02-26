package com.alsaev;

import java.awt.image.BufferedImage;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] images = {
//                "image1.png",
//                "image2.jpg",
//                "image3.wtf"
        };

        List<BufferedImage> buffImages = ImageUtils.load(images);
        ImageAnalyzer analyzer = new ImageAnalyzer();

        analyzer.analyze(images);
    }
}