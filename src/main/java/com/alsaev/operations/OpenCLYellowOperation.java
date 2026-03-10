package com.alsaev.operations;

import com.alsaev.filters.ChannelsFilter;
import com.alsaev.utils.ImageData;

import java.util.List;

public class OpenCLYellowOperation extends AbstractOpenCLOperation implements ImageOperation<ChannelsFilter> {
    private static final String DEFAULT_PTX = "YelloChannel.cl";
    private static final String DEFAULT_KERNEL = "yellowKernel";

    private final ChannelsFilter filter;

    public OpenCLYellowOperation(ChannelsFilter filter) {
        this(filter, DEFAULT_PTX, DEFAULT_KERNEL);
    }

    public OpenCLYellowOperation(ChannelsFilter filter, String fileName) {
        this(filter,fileName, DEFAULT_KERNEL);
    }

    public OpenCLYellowOperation(ChannelsFilter filter, String fileName, String kernelName) {
        super(fileName, kernelName);
        this.filter = filter;
    }

    @Override
    public List<byte[]> apply(ImageData data) {
        byte[] red = data.red();
        byte[] green = data.green();
        byte[] blue = data.blue();

        int length = red.length;

        byte[] outRed = new byte[length];
        byte[] outGreen = new byte[length];
        byte[] outBlue = new byte[length];
        byte[] outYellow = new byte[length];

        execute(length,
                List.of(red, green, blue),
                List.of(outRed, outGreen, outBlue, outYellow)
        );

        return List.of(outRed, outGreen, outBlue, outYellow);
    }

    @Override
    public ChannelsFilter getFilter() {
        return this.filter;
    }
}
