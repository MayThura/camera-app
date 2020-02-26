#include <jni.h>
#include <string>
#include <android/bitmap.h>


extern "C"
JNIEXPORT void JNICALL
Java_com_example_user_cameraapp_MainActivity_processImage(
        JNIEnv *env,
        jobject /* this */, jobject buffer, jint width, jint height , jobject bitmap) {

    unsigned char *inputPtr = (unsigned char *) env->GetDirectBufferAddress(buffer);

    unsigned char *targetPixels;
    AndroidBitmapInfo androidBitmapInfo;
    AndroidBitmap_getInfo(env, bitmap, &androidBitmapInfo);
    AndroidBitmap_lockPixels(env, bitmap, (void **) &targetPixels);
    for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {

            int y1 = inputPtr[(y * androidBitmapInfo.width + x)];
            int u = inputPtr[(y / 2) * (androidBitmapInfo.height) + (x / 2) * 2 + height * width];
            int v = inputPtr[(y / 2) * (androidBitmapInfo.height) + (x / 2) * 2 + (height * width) +
                             1];

            int r = (255 / 219) * (y1 - 16) + (255 / 112) * .701 * (u - 128);
            int g = (255 / 219) * (y1 - 16) - (255 / 112) * .886 * (.114 / .587) * (v - 128) -
                    (255 / 112) * .701 * (.299 / .587) * (u - 128);
            int b = (255 / 219) * (y1 - 16) - (255 / 112) * .886 * (v - 128);

            targetPixels[(y * androidBitmapInfo.width + x) * 4] = r > 255 ? 255: r<0? 0: r;
            targetPixels[(y * androidBitmapInfo.width + x) * 4 + 1] = g >255 ? 255: g<0 ? 0:g ;
            targetPixels[(y * androidBitmapInfo.width + x) * 4 + 2] = b > 255 ? 255 : b< 0? 0: b;
            targetPixels[(y * androidBitmapInfo.width + x) * 4 + 3] = 255;

        }


        AndroidBitmap_unlockPixels(env, bitmap);
    }
}

