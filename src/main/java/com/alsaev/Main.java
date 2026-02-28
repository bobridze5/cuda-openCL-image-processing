package com.alsaev;

import com.alsaev.programA.CudaDilateOperation;
import com.alsaev.programA.DilateFilterImpl;
import com.alsaev.programB.OpenCLYellowOperation;
import com.alsaev.programB.YellowChannelFilter;

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