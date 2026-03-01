extern "C"
__global__ void dilateKernel(unsigned char* input, unsigned char* output, int width, int height, int step) {
    // Координаты текущего пикселя, который МЫ (этот поток) решаем, красить или нет
    int x = blockIdx.x * blockDim.x + threadIdx.x;
    int y = blockIdx.y * blockDim.y + threadIdx.y;

    if (x < width && y < height) {
        bool shouldBeWhite = false;

        // Проверяем окно вокруг СЕБЯ размером step
        for (int dy = -step; dy <= step; dy++) {
            for (int dx = -step; dx <= step; dx++) {
                int nx = x + dx;
                int ny = y + dy;

                // Если сосед в границах изображения
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    // Если нашли ХОТЯ БЫ ОДИН "не черный" пиксель в радиусе step
                    if (input[ny * width + nx] > 0) {
                        shouldBeWhite = true;
                        break; // Дальше можно не искать
                    }
                }
            }
            if (shouldBeWhite) break;
        }

        // Записываем результат: 255 если нашли белого соседа, иначе 0
        output[y * width + x] = shouldBeWhite ? 255 : 0;
    }
}
