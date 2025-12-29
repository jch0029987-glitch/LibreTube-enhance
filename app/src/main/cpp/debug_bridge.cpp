#include <jni.h>

extern "C"
JNIEXPORT void JNICALL
Java_dev_jch0029987_libretibs_debug_DebugBridge_nativeToast(
        JNIEnv *env,
        jclass,
        jstring msg
) {
    jclass cls = env->FindClass("dev/jch0029987/libretibs/debug/DebugBridge");

    jmethodID mid = env->GetStaticMethodID(
        cls,
        "toast",
        "(Ljava/lang/String;)V"
    );

    env->CallStaticVoidMethod(cls, mid, msg);
}

extern "C"
JNIEXPORT void JNICALL
Java_dev_jch0029987_libretibs_debug_DebugBridge_nativeLog(
        JNIEnv *env,
        jclass,
        jstring msg
) {
    jclass cls = env->FindClass("dev/jch0029987/libretibs/debug/DebugBridge");

    jmethodID mid = env->GetStaticMethodID(
        cls,
        "log",
        "(Ljava/lang/String;)V"
    );

    env->CallStaticVoidMethod(cls, mid, msg);
}
