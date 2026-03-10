package com.alsaev.operations;

import com.alsaev.filters.DilateFilter;
import com.alsaev.utils.ImageData;
import jcuda.Pointer;
import jcuda.driver.*;

import java.util.List;

import static jcuda.driver.JCudaDriver.*;

public class CudaDilateOperation extends AbstractCudaOperation implements ImageOperation<DilateFilter> {
    private static final String DEFAULT_PTX = "dilate.ptx";
    private static final String DEFAULT_KERNEL = "dilateKernel";

    private final DilateFilter filter;
    private final int step;

    public CudaDilateOperation(DilateFilter filter, int step) {
        this(filter, step, DEFAULT_PTX, DEFAULT_KERNEL);
    }

    public CudaDilateOperation(DilateFilter filter, int step, String fileName) {
        this(filter, step, fileName, DEFAULT_KERNEL);
    }

    public CudaDilateOperation(DilateFilter filter, int step, String fileName, String kernelName) {
        super(fileName, kernelName);
        validateStep(step);
        this.filter = filter;
        this.step = step;
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

        Pointer kernelParameters = Pointer.to(
                Pointer.to(deviceInput),
                Pointer.to(deviceOutput),
                Pointer.to(new int[]{width}),
                Pointer.to(new int[]{height}),
                Pointer.to(new int[]{this.step})
        );

        execute(width, height, kernelParameters);

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
