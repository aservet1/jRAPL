#ifndef JNI_FUNCTION_DECLARATIONS
#define JNI_FUNCTION_DECLARATIONS

#include <jni.h>

/* This isn't that common, I might need to actually use the JNI function calls in C,
    so this header file has independent declarations for the functions I need to
    call in files outside of where they were defined. This isn't that common though,
    I'm currently (at the time of writing this, march 5th 2021) only using the 4 things
    listed below in RuntimeTestUtils.c when I measure their execution time. Other than that,
    I don't think there'd be any reason to extract out JNIEXPORT calls to call directly in
    another C file. It would make more sense most of the time to just use the Pure-C stuff
    in the libNativeRAPL.a library. But if you need the actual JNI wrapper stuff, feel free
    to add to this file, and make sure this documentation blurb is accurately describing what's
    going on with this file and where it's being used. */

void Java_jRAPL_NativeAccess_profileInit(JNIEnv *env, jclass jcls);
jint Java_jRAPL_NativeAccess_getSocketNum(JNIEnv *env, jclass jcls);
jstring Java_jRAPL_NativeAccess_energyStatCheck(JNIEnv *env, jclass jcls);
void Java_jRAPL_NativeAccess_profileDealloc(JNIEnv * env, jclass jcls);

#endif //JNI_FUNCTION_DECLARATIONS
