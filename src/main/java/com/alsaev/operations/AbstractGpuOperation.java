package com.alsaev.operations;

public abstract class AbstractGpuOperation implements AutoCloseable {
    protected final String fileName;
    protected final String kernelName;

    public AbstractGpuOperation(String fileName, String kernelName) {
        this.fileName = fileName;
        this.kernelName = kernelName;
        init();
    }

    protected abstract void init();

    @Override
    public abstract void close();

}
