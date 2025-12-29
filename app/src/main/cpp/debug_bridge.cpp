#include <jni.h>
#include <android/log.h>

#define TAG "DebugBridgeNative"

static JavaVM* gJvm = nullptr;

/*
 * Called automatically when the library is loaded
 */
jint JNI_OnLoad(JavaVM* vm, void*) {
    gJvm = vm;
    __android_log_print(ANDROID_LOG_INFO, TAG, "JNI_OnLoad called");
    return JNI_VERSION_1_6;
}

/*
 * Internal helper: get JNIEnv for current thread
 */
static JNIEnv* getEnv() {
    if (!gJvm) return nullptr;

    JNIEnv* env = nullptr;
    jint res = gJvm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);

    if (res == JNI_EDETACHED) {
        if (gJvm->AttachCurrentThread(&env, nullptr) != JNI_OK) {
            return nullptr;
        }
    }

    return env;
}

/*
 * Native -> Kotlin toast dispatcher
 * SAFE for Frida Gadget
 */
extern "C"
JNIEXPORT void JNICALL
Java_dev_jch0029987_libretibs_debug_DebugBridge_nativeToastFromNative(
        JNIEnv* env,
        jclass,
        jstring msg
) {
    jclass cls = env->FindClass("dev/jch0029987/libretibs/debug/DebugBridge");
    if (!cls) return;

    jmethodID mid = env->GetStaticMethodID(
            cls,
            "toast",
            "(Ljava/lang/String;)V"
    );
    if (!mid) return;

    env->CallStaticVoidMethod(cls, mid, msg);
}

/*
 * Native -> Kotlin log dispatcher
 */
extern "C"
JNIEXPORT void JNICALL
Java_dev_jch0029987_libretibs_debug_DebugBridge_nativeLogFromNative(
        JNIEnv* env,
        jclass,
        jstring msg
) {
    jclass cls = env->FindClass("dev/jch0029987/libretibs/debug/DebugBridge");
    if (!cls) return;

    jmethodID mid = env->GetStaticMethodID(
            cls,
            "log",
            "(Ljava/lang/String;)V"
    );
    if (!mid) return;

    env->CallStaticVoidMethod(cls, mid, msg);
}

/*
 * ENTRYPOINT FOR FRIDA
 * No Java bridge involved
 */
extern "C"
JNIEXPORT void JNICALL
debugbridge_toast_from_frida(const char* msg) {
    JNIEnv* env = getEnv();
    if (!env) return;

    jstring jmsg = env->NewStringUTF(msg);

    jclass cls = env->FindClass("dev/jch0029987/libretibs/debug/DebugBridge");
    if (!cls) return;

    jmethodID mid = env->GetStaticMethodID(
            cls,
            "toast",
            "(Ljava/lang/String;)V"
    );
    if (!mid) return;

    env->CallStaticVoidMethod(cls, mid, jmsg);
}

/*
 * LOG ENTRYPOINT FOR FRIDA
 */
extern "C"
JNIEXPORT void JNICALL
debugbridge_log_from_frida(const char* msg) {
    JNIEnv* env = getEnv();
    if (!env) return;

    jstring jmsg = env->NewStringUTF(msg);

    jclass cls = env->FindClass("dev/jch0029987/libretibs/debug/DebugBridge");
    if (!cls) return;

    jmethodID mid = env->GetStaticMethodID(
            cls,
            "log",
            "(Ljava/lang/String;)V"
    );
    if (!mid) return;

    env->CallStaticVoidMethod(cls, mid, jmsg);
}
