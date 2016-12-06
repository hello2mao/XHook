#include <jni.h>
#include <dlfcn.h>
#include <stdio.h>
#include <stdlib.h>

#define JNIHOOK_CLASS "com/catfish/yposed/HookManager"

void art_jni_onload(JNIEnv* env, int version);
