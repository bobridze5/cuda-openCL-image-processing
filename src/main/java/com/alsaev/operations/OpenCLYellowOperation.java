package com.alsaev.operations;

import com.alsaev.filters.ChannelsFilter;
import com.alsaev.analyzer.ImageData;

import java.util.List;

public class OpenCLYellowOperation implements ImageOperation<ChannelsFilter> {
    private final ChannelsFilter filter;

    public OpenCLYellowOperation(ChannelsFilter filter) {
        this.filter = filter;
    }

    @Override
    public List<byte[]> apply(ImageData data) {


        return null;
    }

    @Override
    public ChannelsFilter getFilter() {
        return this.filter;
    }
}
