package com.alsaev;

import com.alsaev.analyzer.AnalysisReport;
import com.alsaev.analyzer.ImageAnalyzer;
import com.alsaev.analyzer.ImageReport;
import com.alsaev.filters.ChannelsFilter;
import com.alsaev.filters.DilateFilter;
import com.alsaev.operations.CudaDilateOperation;
import com.alsaev.filters.DilateFilterImpl;
import com.alsaev.operations.ImageOperation;
import com.alsaev.operations.OpenCLYellowOperation;
import com.alsaev.filters.YellowChannelFilter;
import com.alsaev.utils.ImageUtils;
import com.alsaev.utils.StatisticsPrinter;
import com.alsaev.utils.StatisticsUtils;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> images = List.of(
//                "results/pic1.png",
//                "results/pic2.png",
//                "results/pic3.jpg",
//                "results/pic4.jpg",
//                "results/pic5.jpg",
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

        DilateFilter dilateFilter = new DilateFilterImpl(100);
        ChannelsFilter channelsFilter = new YellowChannelFilter();

        ImageOperation<DilateFilter> cuda = new CudaDilateOperation(dilateFilter, 1);
        ImageOperation<ChannelsFilter> openCL = new OpenCLYellowOperation(channelsFilter);

        ImageAnalyzer analyzer = new ImageAnalyzer(cuda, 3);
        AnalysisReport report = analyzer.analyze(images);

        StatisticsUtils.saveTo(report, "results/output/dilate/");
        StatisticsUtils.print(report, System.out);
    }
}