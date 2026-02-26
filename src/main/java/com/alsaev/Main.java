package com.alsaev;

import com.alsaev.programA.CudaDilateOperation;
import com.alsaev.programA.DilateFilter;

public class Main {
    public static void main(String[] args) {
        String[] images = {
                "results/img.png",
//                "image1.png",
//                "image2.jpg",
//                "image3.wtf"
        };

        String img = "results/img.png";

        ImageAnalyzer analyzer = ImageAnalyzer.builder()
                .setFilter(new DilateFilter(128))
                .setStrategy(new CudaDilateOperation( 3))
//                .setStrategy(new OpenCLYellowOperation())
                .setTestRuns(3)
                .build();

        analyzer.analyze(images);

    }
}