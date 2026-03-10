__kernel void yellowKernel(
    __global const uchar* R,
    __global const uchar* G,
    __global const uchar* B,
    __global uchar* outR,
    __global uchar* outG,
    __global uchar* outB,
    __global uchar* outY,
    int length)
{
    int i = get_global_id(0);
    if (i >= length) return;

    int r = R[i];
    int g = G[i];
    int b = B[i];

    int newR = r - (g + b) / 2;
    int newG = g - (r + b) / 2;
    int newB = b - (r + g) / 2;
    int newY = r + g - 2 * (abs(r - g) + b);

    outR[i] = (uchar)clamp(newR, 0, 255);
    outG[i] = (uchar)clamp(newG, 0, 255);
    outB[i] = (uchar)clamp(newB, 0, 255);
    outY[i] = (uchar)clamp(newY, 0, 255);
}
