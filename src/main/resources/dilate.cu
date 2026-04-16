#include <cuda_fp16.h>

extern "C"
__global__ void dilateKernel(unsigned char* input, unsigned char* output, int width, int height, int step) {
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x < width && y < height) {
        bool shouldBeWhite = false;

        for (int dy = -step; dy <= step; dy++) {
            for (int dx = -step; dx <= step; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (input[ny * width + nx] > 0) {
                        shouldBeWhite = true;
                        break;
                    }
                }
            }
            if (shouldBeWhite) break;
        }

        output[y * width + x] = shouldBeWhite ? 255 : 0;
    }
}

extern "C"
__global__ void dilateKernelFloat32(float* input, float* output, int width, int height, int step) {
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x < width && y < height) {
        bool shouldBeWhite = false;

        for (int dy = -step; dy <= step; dy++) {
            for (int dx = -step; dx <= step; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (input[ny * width + nx] > 0.0f) {
                        shouldBeWhite = true;
                        break;
                    }
                }
            }
            if (shouldBeWhite) break;
        }

        output[y * width + x] = shouldBeWhite ? 255.0f : 0.0f;
    }
}

extern "C"
__global__ void dilateKernelFloat16(__half* input, __half* output, int width, int height, int step) {
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x < width && y < height) {
        bool shouldBeWhite = false;
        __half zero = __float2half(0.0f);

        for (int dy = -step; dy <= step; dy++) {
            for (int dx = -step; dx <= step; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (__hgt(input[ny * width + nx], zero)) {
                        shouldBeWhite = true;
                        break;
                    }
                }
            }
            if (shouldBeWhite) break;
        }

        output[y * width + x] = shouldBeWhite ? __float2half(255.0f) : zero;
    }
}

extern "C"
__global__ void dilateKernelInt32(int* input, int* output, int width, int height, int step) {
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x < width && y < height) {
        bool shouldBeWhite = false;

        for (int dy = -step; dy <= step; dy++) {
            for (int dx = -step; dx <= step; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (input[ny * width + nx] > 0) {
                        shouldBeWhite = true;
                        break;
                    }
                }
            }
            if (shouldBeWhite) break;
        }

        output[y * width + x] = shouldBeWhite ? 255 : 0;
    }
}

extern "C"
__global__ void dilateKernelInt16(short* input, short* output, int width, int height, int step) {
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x < width && y < height) {
        bool shouldBeWhite = false;

        for (int dy = -step; dy <= step; dy++) {
            for (int dx = -step; dx <= step; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    if (input[ny * width + nx] > 0) {
                        shouldBeWhite = true;
                        break;
                    }
                }
            }
            if (shouldBeWhite) break;
        }

        output[y * width + x] = shouldBeWhite ? (short)255 : (short)0;
    }
}
