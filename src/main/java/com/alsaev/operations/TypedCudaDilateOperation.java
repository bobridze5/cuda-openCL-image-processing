package com.alsaev.operations;

import com.alsaev.filters.DilateFilter;
import com.alsaev.utils.ImageData;
import jcuda.Pointer;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;

import java.util.List;

import static jcuda.driver.JCudaDriver.*;

public class TypedCudaDilateOperation extends CudaDilateOperation {
    private final DataType dataType;
    private final CUfunction typedFunction;

    public TypedCudaDilateOperation(DilateFilter filter, int step, String fileName, DataType dataType) {
        super(filter, step, fileName);
        this.dataType = dataType;
        this.typedFunction = loadTypedFunction(dataType);
    }

    private CUfunction loadTypedFunction(DataType type) {
        String kernelName = switch (type) {
            case FLOAT32 -> "dilateKernelFloat32";
            case FLOAT16 -> "dilateKernelFloat16";
            case INT32 -> "dilateKernelInt32";
            case INT16 -> "dilateKernelInt16";
            case UINT8 -> null;
        };

        if (kernelName == null) return function;

        CUfunction func = new CUfunction();
        cuModuleGetFunction(func, module, kernelName);
        return func;
    }

    public DataType getDataType() {
        return dataType;
    }


    @Override
    public List<byte[]> apply(ImageData data) {
        Object input = prepareInput(data);
        Object output = executeOnGpu(input, data.width(), data.height());
        return List.of(toBytes(output));
    }


    public Object prepareInput(ImageData data) {
        return switch (dataType) {
            case FLOAT32 -> toFloat32(data.red());
            case FLOAT16 -> toFloat16(data.red());
            case INT32 -> toInt32(data.red());
            case INT16 -> toInt16(data.red());
            case UINT8 -> data.red();
        };
    }

    public Object executeOnGpu(Object input, int width, int height) {
        return switch (dataType) {
            case FLOAT32 -> executeFloat32((float[]) input, width, height);
            case FLOAT16 -> executeFloat16((short[]) input, width, height);
            case INT32 -> executeInt32((int[]) input, width, height);
            case INT16 -> executeInt16((short[]) input, width, height);
            case UINT8 -> executeUint8((byte[]) input, width, height);
        };
    }


    public byte[] toBytes(Object output) {
        return switch (dataType) {
            case FLOAT32 -> float32ToBytes((float[]) output);
            case FLOAT16 -> float16ToBytes((short[]) output);
            case INT32 -> int32ToBytes((int[]) output);
            case INT16 -> int16ToBytes((short[]) output);
            case UINT8 -> (byte[]) output;
        };
    }


    private byte[] executeUint8(byte[] input, int width, int height) {
        long numBytes = (long) width * height;
        byte[] output = new byte[width * height];

        CUdeviceptr dIn = new CUdeviceptr();
        CUdeviceptr dOut = new CUdeviceptr();
        cuMemAlloc(dIn, numBytes);
        cuMemAlloc(dOut, numBytes);
        cuMemcpyHtoD(dIn, Pointer.to(input), numBytes);

        execute(width, height, kernelParams(dIn, dOut, width, height), typedFunction);

        cuMemcpyDtoH(Pointer.to(output), dOut, numBytes);
        cuMemFree(dIn);
        cuMemFree(dOut);
        return output;
    }

    private float[] executeFloat32(float[] input, int width, int height) {
        int n = width * height;
        long numBytes = (long) n * Float.BYTES;
        float[] output = new float[n];

        CUdeviceptr dIn = new CUdeviceptr();
        CUdeviceptr dOut = new CUdeviceptr();
        cuMemAlloc(dIn, numBytes);
        cuMemAlloc(dOut, numBytes);
        cuMemcpyHtoD(dIn, Pointer.to(input), numBytes);

        execute(width, height, kernelParams(dIn, dOut, width, height), typedFunction);

        cuMemcpyDtoH(Pointer.to(output), dOut, numBytes);
        cuMemFree(dIn);
        cuMemFree(dOut);
        return output;
    }

    private short[] executeFloat16(short[] input, int width, int height) {
        int n = width * height;
        long numBytes = (long) n * Short.BYTES;
        short[] output = new short[n];

        CUdeviceptr dIn = new CUdeviceptr();
        CUdeviceptr dOut = new CUdeviceptr();
        cuMemAlloc(dIn, numBytes);
        cuMemAlloc(dOut, numBytes);
        cuMemcpyHtoD(dIn, Pointer.to(input), numBytes);

        execute(width, height, kernelParams(dIn, dOut, width, height), typedFunction);

        cuMemcpyDtoH(Pointer.to(output), dOut, numBytes);
        cuMemFree(dIn);
        cuMemFree(dOut);
        return output;
    }

    private int[] executeInt32(int[] input, int width, int height) {
        int n = width * height;
        long numBytes = (long) n * Integer.BYTES;
        int[] output = new int[n];

        CUdeviceptr dIn = new CUdeviceptr();
        CUdeviceptr dOut = new CUdeviceptr();
        cuMemAlloc(dIn, numBytes);
        cuMemAlloc(dOut, numBytes);
        cuMemcpyHtoD(dIn, Pointer.to(input), numBytes);

        execute(width, height, kernelParams(dIn, dOut, width, height), typedFunction);

        cuMemcpyDtoH(Pointer.to(output), dOut, numBytes);
        cuMemFree(dIn);
        cuMemFree(dOut);
        return output;
    }

    private short[] executeInt16(short[] input, int width, int height) {
        int n = width * height;
        long numBytes = (long) n * Short.BYTES;
        short[] output = new short[n];

        CUdeviceptr dIn = new CUdeviceptr();
        CUdeviceptr dOut = new CUdeviceptr();
        cuMemAlloc(dIn, numBytes);
        cuMemAlloc(dOut, numBytes);
        cuMemcpyHtoD(dIn, Pointer.to(input), numBytes);

        execute(width, height, kernelParams(dIn, dOut, width, height), typedFunction);

        cuMemcpyDtoH(Pointer.to(output), dOut, numBytes);
        cuMemFree(dIn);
        cuMemFree(dOut);
        return output;
    }

    private Pointer kernelParams(CUdeviceptr dIn, CUdeviceptr dOut, int width, int height) {
        return Pointer.to(
                Pointer.to(dIn),
                Pointer.to(dOut),
                Pointer.to(new int[]{width}),
                Pointer.to(new int[]{height}),
                Pointer.to(new int[]{step})
        );
    }

    private static float[] toFloat32(byte[] src) {
        float[] dst = new float[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = src[i] & 0xFF;
        return dst;
    }

    private static short[] toFloat16(byte[] src) {
        short[] dst = new short[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = floatToHalfBits(src[i] & 0xFF);
        return dst;
    }

    private static int[] toInt32(byte[] src) {
        int[] dst = new int[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = src[i] & 0xFF;
        return dst;
    }

    private static short[] toInt16(byte[] src) {
        short[] dst = new short[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = (short) (src[i] & 0xFF);
        return dst;
    }

    private static byte[] float32ToBytes(float[] src) {
        byte[] dst = new byte[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = (byte) Math.min(255, Math.max(0, (int) src[i]));
        return dst;
    }

    private static byte[] float16ToBytes(short[] src) {
        byte[] dst = new byte[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = (byte) Math.min(255, Math.max(0, (int) halfBitsToFloat(src[i])));
        return dst;
    }

    private static byte[] int32ToBytes(int[] src) {
        byte[] dst = new byte[src.length];

        for (int i = 0; i < src.length; i++) {
            dst[i] = (byte) Math.min(255, Math.max(0, src[i]));
        }

        return dst;
    }

    private static byte[] int16ToBytes(short[] src) {
        byte[] dst = new byte[src.length];

        for (int i = 0; i < src.length; i++) {
            dst[i] = (byte) Math.min(255, Math.max(0, src[i]));
        }

        return dst;
    }

    private static short floatToHalfBits(float f) {
        // (-1)^sign × 2^(exp - bias) * (1 + mantissa) -> IEEE 754
        // float32: 1 бит знак | 8 бит экспонента | 23 бита мантисса
        // float16: 1 бит знак | 5 бит экспонента | 10 бит мантисса
        int bits = Float.floatToRawIntBits(f);
        int sign = (bits >>> 31) & 0x1;
        // 30 - 23 биты - экспонента
        // для float32 -> bias = 127
        // для float16 -> bias = 15
        int exp = ((bits >>> 23) & 0xFF) - 127 + 15;
        // 22 - 0 биты - мантисса
        int mantissa = (bits >>> 13) & 0x3FF;

        // денормализация
        if (exp <= 0) return (short) (sign << 15);
        // Переполнение
        if (exp >= 31) return (short) ((sign << 15) | 0x7C00);

        return (short) ((sign << 15) | (exp << 10) | mantissa);
    }

    private static float halfBitsToFloat(short h) {
        int bits = h & 0xFFFF;
        int sign = (bits & 0x8000) << 16; // бит 15 - знак, сдвигаем в позицию float32
        int exp = (bits & 0x7C00) >> 10;  // биты 14-10 - экспонента (5 бит)
        int mantissa = (bits & 0x03FF) << 13; // биты 9-0 - мантисса, выравниваем под float32

        if (exp == 0) return Float.intBitsToFloat(sign | mantissa); // денормализованное
        if (exp == 31) return Float.intBitsToFloat(sign | 0x7F800000 | mantissa);  // Inf / NaN
        return Float.intBitsToFloat(sign | ((exp + 127 - 15) << 23) | mantissa);  // меняем bias: 15 → 127
    }
}
