package com.alsaev;

import com.alsaev.analyzer.ImageAnalyzer;
import com.alsaev.filters.ChannelsFilter;
import com.alsaev.filters.DilateFilter;
import com.alsaev.operations.CudaDilateOperation;
import com.alsaev.filters.DilateFilterImpl;
import com.alsaev.operations.ImageOperation;
import com.alsaev.operations.OpenCLYellowOperation;
import com.alsaev.filters.YellowChannelFilter;


import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> images = List.of(
                "results/1024x768.jpg",
                "results/1280x960.jpg",
                "results/2048x1536.jpg",
                "results/5464x6830.jpg",
                "results/8736x4896.jpg",
                "results/9408x5376.jpg",
                "results/15360x4096.jpg",
                "results/23040x4800.jpg",
                "results/img.jpg"
        );

        DilateFilter dilateFilter = new DilateFilterImpl(140);
        ChannelsFilter channelsFilter = new YellowChannelFilter();

        ImageOperation<DilateFilter> cuda = new CudaDilateOperation(dilateFilter, 1);
        ImageOperation<ChannelsFilter> openCL = new OpenCLYellowOperation(channelsFilter,
                "program.cl", "yellowKernel");


        ImageAnalyzer analyzer = ImageAnalyzer.builder()
                .setOperation(openCL)
                .setOutputPath("results/output/B/")
                .setPrintStream(System.out)
                .setTestRuns(3)
                .build();

        analyzer.analyze(images);
        analyzer.close();

    }
}