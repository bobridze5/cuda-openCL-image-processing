package com.alsaev.operations;

import com.alsaev.filters.DilateFilter;
import com.alsaev.utils.ImageData;
import com.alsaev.utils.ResourceUtils;
import jcuda.Pointer;
import jcuda.driver.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static jcuda.driver.JCudaDriver.*;

public class CudaDilateOperation implements ImageOperation<DilateFilter> {
    private static final String DEFAULT_PTX = "dilate.ptx";
    private static final String DEFAULT_KERNEL = "dilateKernel";

    private final String ptxFile;
    private final String kernelName;
    private final DilateFilter filter;
    private final int step;

    private CUcontext context;
    private CUmodule module;
    private CUfunction function;

    public CudaDilateOperation(DilateFilter filter, int step) {
        this(filter, step, DEFAULT_PTX, DEFAULT_KERNEL);
    }

    public CudaDilateOperation(DilateFilter filter, int step, String ptxFile, String kernelName) {
        validateStep(step);
        this.filter = filter;
        this.step = step;
        this.ptxFile = ptxFile;
        this.kernelName = kernelName;
        initCuda();
    }

    private void initCuda() {
        cuInit(0);
        CUdevice device = new CUdevice();
        cuDeviceGet(device, 0);
        context = new CUcontext();
        cuCtxCreate(context, 0, device);

        String ptxPath = ResourceUtils.getAbsolutePath(ptxFile);
        module = new CUmodule();
        cuModuleLoad(module, ptxPath);

        function = new CUfunction();
        cuModuleGetFunction(function, module, kernelName);
    }

    @Override
    public List<byte[]> apply(ImageData data) {
        int width = data.width();
        int height = data.height();
        long numBytes = (long) width * height;

        CUdeviceptr deviceInput = new CUdeviceptr();
        CUdeviceptr deviceOutput = new CUdeviceptr();
        cuMemAlloc(deviceInput, numBytes);
        cuMemAlloc(deviceOutput, numBytes);

        cuMemcpyHtoD(deviceInput, Pointer.to(data.red()), numBytes);

        int blockSizeX = 32;
        int blockSizeY = 32;
        int gridSizeX = (int) Math.ceil((double) width / blockSizeX);
        int gridSizeY = (int) Math.ceil((double) height / blockSizeY);


        Pointer kernelParameters = Pointer.to(
                Pointer.to(deviceInput),
                Pointer.to(deviceOutput),
                Pointer.to(new int[]{width}),
                Pointer.to(new int[]{height}),
                Pointer.to(new int[]{this.step})
        );

        cuLaunchKernel(function,
                gridSizeX, gridSizeY, 1,
                blockSizeX, blockSizeY, 1,
                0, null,
                kernelParameters, null
        );

        cuCtxSynchronize();

        byte[] result = new byte[width * height];
        cuMemcpyDtoH(Pointer.to(result), deviceOutput, numBytes);

        cuMemFree(deviceInput);
        cuMemFree(deviceOutput);

        return List.of(result);
    }

    @Override
    public DilateFilter getFilter() {
        return this.filter;
    }

    private void validateStep(int step) {
        if (step < 1 || step > 3) {
            throw new IllegalArgumentException("Значение шага должно быть в пределах от 1 до 3 включительно");
        }
    }

}
