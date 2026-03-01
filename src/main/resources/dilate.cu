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
