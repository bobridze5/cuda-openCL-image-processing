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
                "results/img.png"
        );

        DilateFilter dilateFilter = new DilateFilterImpl(128);
        ChannelsFilter channelsFilter = new YellowChannelFilter();

        ImageOperation<DilateFilter> cuda = new CudaDilateOperation(dilateFilter, 3);
        ImageOperation<ChannelsFilter> openCL = new OpenCLYellowOperation(channelsFilter);

        ImageAnalyzer analyzer = ImageAnalyzer.builder()
                .setStrategy(cuda)
                .setTestRuns(3)
                .build();

        analyzer.analyze(images);

    }
}