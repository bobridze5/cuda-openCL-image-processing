package com.alsaev.operations;

import com.alsaev.utils.ResourceUtils;
import org.jocl.*;

import java.util.ArrayList;
import java.util.List;

import static org.jocl.CL.*;

public abstract class AbstractOpenCLOperation extends AbstractGpuOperation {
    private static final String DEFAULT_FILE_NAME = "program.cl";
    private static final String DEFAULT_KERNEL_NAME = "kernel";

    protected cl_context context;
    protected cl_command_queue commandQueue;
    protected cl_kernel kernel;
    protected cl_program program;

    public AbstractOpenCLOperation(){
        super(DEFAULT_FILE_NAME, DEFAULT_KERNEL_NAME);
    }

    public AbstractOpenCLOperation(String fileName){
        super(fileName, DEFAULT_KERNEL_NAME);
    }

    public AbstractOpenCLOperation(String fileName, String kernelName) {
        super(fileName, kernelName);
    }

    @Override
    protected void init() {
        CL.setExceptionsEnabled(true);

        // Выбор платформы: NVIDIA / AMD
        cl_platform_id[] platforms = new cl_platform_id[1];
        clGetPlatformIDs(1, platforms, null);

        // Выбор устройства
        cl_device_id[] devices = new cl_device_id[1];
        clGetDeviceIDs(platforms[0], CL_DEVICE_TYPE_GPU, 1, devices, null);

        // Создание контекста
        context = clCreateContext(null, 1, devices, null, null, null);
        // Создание очереди
        commandQueue = clCreateCommandQueueWithProperties(context, devices[0], null, null);

        // Код для выполнения
        String kernelSource = ResourceUtils.readResource(fileName);

        // Создание программы
        program = clCreateProgramWithSource(context, 1, new String[]{kernelSource}, null, null);
        clBuildProgram(program, 0, null, null, null, null);

        // Создание функции
        kernel = clCreateKernel(program, kernelName, null);
    }

    protected void execute(long globalWorkSize, List<byte[]> inputs, List<byte[]> outputs) {
        int argIndex = 0;
        List<cl_mem> inputMem = new ArrayList<>();
        List<cl_mem> outputMem = new ArrayList<>();

        try {
            for (byte[] data : inputs) {
                cl_mem mem = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                        Sizeof.cl_char * data.length, Pointer.to(data), null);

                inputMem.add(mem);
                clSetKernelArg(kernel, argIndex++, Sizeof.cl_mem, Pointer.to(mem));
            }

            for (byte[] data : outputs) {
                cl_mem mem = clCreateBuffer(context, CL_MEM_WRITE_ONLY,
                        Sizeof.cl_char * data.length, null, null);

                outputMem.add(mem);
                clSetKernelArg(kernel, argIndex++, Sizeof.cl_mem, Pointer.to(mem));
            }

            int length = (int) globalWorkSize;
            clSetKernelArg(kernel, argIndex, Sizeof.cl_int, Pointer.to(new int[]{length}));

            clEnqueueNDRangeKernel(commandQueue, kernel, 1,
                    null, new long[]{globalWorkSize}, null,
                    0, null, null);
            clFinish(commandQueue);

            for (int i = 0; i < outputs.size(); i++) {
                clEnqueueReadBuffer(commandQueue, outputMem.get(i), CL_TRUE, 0,
                        Sizeof.cl_char * outputs.get(i).length, Pointer.to(outputs.get(i)),
                        0, null, null);
            }
        } finally {
            inputMem.forEach(CL::clReleaseMemObject);
            outputMem.forEach(CL::clReleaseMemObject);
        }

    }

    @Override
    public void close() {
        if (kernel != null) clReleaseKernel(kernel);
        if (program != null) clReleaseProgram(program);
        if (commandQueue != null) clReleaseCommandQueue(commandQueue);
        if (context != null) clReleaseContext(context);
    }
}
