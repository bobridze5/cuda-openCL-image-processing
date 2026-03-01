package com.alsaev.operations;

import com.alsaev.utils.ResourceUtils;
import jcuda.Pointer;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;

import static jcuda.driver.JCudaDriver.*;

public abstract class AbstractCudaOperation extends AbstractGpuOperation {
    private static final String DEFAULT_FILE_NAME = "program.ptx";
    private static final String DEFAULT_KERNEL_NAME = "kernel";

    protected CUcontext context;
    protected CUmodule module;
    protected CUfunction function;

    public AbstractCudaOperation(){
        super(DEFAULT_FILE_NAME, DEFAULT_KERNEL_NAME);
    }

    public AbstractCudaOperation(String fileName) {
        super(fileName, DEFAULT_KERNEL_NAME);
    }

    public AbstractCudaOperation(String fileName, String kernelName) {
        super(fileName, kernelName);
    }

    @Override
    protected void init() {
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        context = new CUcontext();
        cuCtxCreate(context, 0, device);

        String ptxPath = ResourceUtils.getAbsolutePath(fileName);
        module = new CUmodule();
        cuModuleLoad(module, ptxPath);

        function = new CUfunction();
        cuModuleGetFunction(function, module, kernelName);
    }

    protected void execute(int width, int height, Pointer kernelParameters) {
        int blockSizeX = 32;
        int blockSizeY = 32;
        int gridSizeX = (int) Math.ceil((double) width / blockSizeX);
        int gridSizeY = (int) Math.ceil((double) height / blockSizeY);

        cuLaunchKernel(function,
                gridSizeX, gridSizeY, 1,
                blockSizeX, blockSizeY, 1,
                0, null,
                kernelParameters, null
        );
        cuCtxSynchronize();
    }

    @Override
    public void close() {
        if (module != null) cuModuleUnload(module);
        if (context != null) cuCtxDestroy(context);
    }
}
