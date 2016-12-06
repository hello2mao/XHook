#ifndef DVM_HELPER_H_
#define DVM_HELPER_H_

#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <stdbool.h>
#include <config.h>

typedef uint8_t             u1;
typedef uint16_t            u2;
typedef uint32_t            u4;
typedef uint64_t            u8;
typedef int8_t              s1;
typedef int16_t             s2;
typedef int32_t             s4;
typedef int64_t             s8;

typedef union JValue {
    void *l;
} JValue;

void init_dvm(JNIEnv* env);

#endif
