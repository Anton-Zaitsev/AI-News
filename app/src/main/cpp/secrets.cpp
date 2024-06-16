#include "secrets.hpp"

#include <jni.h>

#include "sha256.hpp"
#include "sha256.cpp"

/* Copyright (c) 2020-present Klaxit SAS
*
* Permission is hereby granted, free of charge, to any person
* obtaining a copy of this software and associated documentation
* files (the "Software"), to deal in the Software without
* restriction, including without limitation the rights to use,
* copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following
* conditions:
*
* The above copyright notice and this permission notice shall be
* included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
* EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
* OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
* NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
* HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
* OTHER DEALINGS IN THE SOFTWARE.
*/

char *customDecode(char *str) {
    /* Add your own logic here
    * To improve your key security you can encode it before to integrate it in the app.
    * And then decode it with your own logic in this function.
    */
    return str;
}

jstring getOriginalKey(
        char *obfuscatedSecret,
        int obfuscatedSecretSize,
        jstring obfuscatingJStr,
        JNIEnv *pEnv) {

    // Get the obfuscating string SHA256 as the obfuscator
    const char *obfuscatingStr = pEnv->GetStringUTFChars(obfuscatingJStr, NULL);
    char buffer[2 * SHA256::DIGEST_SIZE + 1];

    sha256(obfuscatingStr, buffer);
    const char *obfuscator = buffer;

    // Apply a XOR between the obfuscated key and the obfuscating string to get original string
    char out[obfuscatedSecretSize + 1];
    for (int i = 0; i < obfuscatedSecretSize; i++) {
        out[i] = obfuscatedSecret[i] ^ obfuscator[i % strlen(obfuscator)];
    }

    // Add string terminal delimiter
    out[obfuscatedSecretSize] = 0x0;

    // (Optional) To improve key security
    return pEnv->NewStringUTF(customDecode(out));
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getIV(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x54, 0x30, 0x34, 0x4f, 0x2c, 0x77, 0x2f, 0x5d, 0x66, 0x5d, 0x65, 0x6, 0x2d, 0x59, 0x1, 0x55, 0x65, 0x66, 0x27, 0x53, 0x61, 0x70, 0x5e, 0xd };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getSalt(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x67, 0x31, 0x9, 0x72, 0x2c, 0x79, 0x2f, 0x5d, 0x79, 0x62, 0x7e, 0x63, 0x32, 0x64, 0x38, 0x53, 0x52, 0x74, 0x9, 0x42, 0x60, 0x2, 0x36, 0xd };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getSecretKey(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x42, 0x2d, 0x50, 0x60, 0x36, 0x44, 0x8, 0x1e, 0x70, 0x66, 0x5c, 0xf, 0xf, 0x7a, 0xe, 0x23, 0x48, 0x4a, 0x0, 0x7, 0x6e, 0x67, 0x10, 0x5d, 0x55, 0x77, 0x74, 0x5c, 0x64, 0x8, 0x50, 0x4e, 0x7b, 0x5a, 0x51, 0x2c, 0x7d, 0x31, 0x7a, 0x23, 0x4, 0x4b, 0x62, 0x5b };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getGoogleNewsUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x5a, 0x53, 0x43, 0x44, 0x4d, 0x54, 0xd, 0xe, 0x57, 0x5f, 0x4, 0x1c, 0x55, 0x5e, 0xe };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getTassUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x40, 0x57, 0x47, 0x44, 0x4d, 0x41, 0x17 };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getMainNewsUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x5a, 0x53, 0x43, 0x44, 0x4d, 0x5e, 0x3, 0x8, 0x5c, 0x1d, 0x13, 0x47 };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getBankiNewsUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x43, 0x41, 0x43, 0x19, 0x1, 0x52, 0xc, 0xa, 0x59, 0x1d, 0x13, 0x47 };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getAstroBeneNewsUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x52, 0x53, 0x51, 0x53, 0x10, 0x1d, 0x4, 0x4, 0x55, 0x57, 0x3, 0x47, 0x44, 0x5f, 0x6, 0x42, 0x1e, 0x50, 0x58, 0x5e };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getBBCNewsUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x52, 0x53, 0x51, 0x53, 0x10, 0x1d, 0x0, 0x3, 0x53, 0x5a, 0x4f, 0x51, 0x59, 0x1f, 0x16, 0x5b, 0x1f };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_zaitsev_a_d_mirea_diplom_secret_Secrets_getNewYorkTimesNewsUrl(
        JNIEnv* pEnv,
        jobject pThis,
        jstring packageName) {
     char obfuscatedSecret[] = { 0x5e, 0x12, 0x11, 0x45, 0x11, 0xb, 0x4e, 0x1a, 0x46, 0x45, 0x47, 0x19, 0xd, 0x4a, 0x16, 0x8, 0x5d, 0x56, 0x12, 0x1c, 0x55, 0x5e, 0xe, 0x1f };
     return getOriginalKey(obfuscatedSecret, sizeof(obfuscatedSecret), packageName, pEnv);
}
