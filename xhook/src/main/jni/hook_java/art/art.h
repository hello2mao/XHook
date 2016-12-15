#ifndef ART_H_
#define ART_H_
#include <jni.h>
#include <dlfcn.h>
#include <stdio.h>
#include <stdlib.h>
#include "config.h"

void art_jni_onload(JNIEnv* env, int version);
#endif