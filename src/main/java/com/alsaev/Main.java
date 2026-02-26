package com.alsaev;

import com.alsaev.programA.CudaDilateOperation;

public class Main {
    public static void main(String[] args) {
        String[] images = {
//                "image1.png",
//                "image2.jpg",
//                "image3.wtf"
        };

        ImageAnalyzer analyzer = ImageAnalyzer.builder()
                .setStrategy(new CudaDilateOperation(128, 3))
//                .setStrategy(new OpenCLYellowOperation())
                .setTestRuns(3)
                .build();

        analyzer.analyze(images);
    }
}