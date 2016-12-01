#include "hooks_java_init.h"

void hooks_java_init() {
    // resolve symbols from DVM
    // 获得在libdvm.so动态库中所有hook需要使用的函数和全局变量的地址
    dexstuff_resolv_dvm(&dex_stuff);
}