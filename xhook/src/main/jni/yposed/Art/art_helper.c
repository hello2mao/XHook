#include "art_helper.h"

static void (*art_quick_to_interpreter_bridge)(void*);
static void (*artInterpreterToInterpreterBridge)(void*);
static int gCodeOffset = 0;
static int gInterpretOffset = 0;
static int gDexOffset = 0;
static int gMethodSize = 0;

void init_parameter(int version) {
    void* handle = dlopen("libart.so", RTLD_LAZY | RTLD_GLOBAL);
    art_quick_to_interpreter_bridge = (void (*)(void*)) dlsym(handle, "art_quick_to_interpreter_bridge");
    artInterpreterToInterpreterBridge = (void (*)(void*)) dlsym(handle, "artInterpreterToInterpreterBridge");

    switch(version) {
    case 0:
        // for 4.4
        gCodeOffset = 40;
        gInterpretOffset = 44;
        gDexOffset = 32;
        gMethodSize = 80;
        break;
    case 1:
        // for 5.0
        gCodeOffset = 40;
        gInterpretOffset = 24;
        gDexOffset = 60;
        gMethodSize = 72;
        break;
    case 2:
        // for 5.1
        gCodeOffset = 44;
        gInterpretOffset = 36;
        gDexOffset = 24;
        gMethodSize = 48;
        break;
    }
}

void switchQuickToInterpret(void* artmeth) {
    int* bridge = (int*) (artmeth + gCodeOffset);
    *bridge = (int)(art_quick_to_interpreter_bridge);
}

void switchInterpretToInterpret(void* artmeth) {
    int* bridge = (int*) (artmeth + gInterpretOffset);
    *bridge = (int)(artInterpreterToInterpreterBridge);
}

void doHook(void* origin, void* proxy) {
    int* ori_code_item = (int*) (origin + gDexOffset);
    int* pro_code_item = (int*) (proxy + gDexOffset);
    int temp = *ori_code_item;
    *ori_code_item = *pro_code_item;
    memcpy(proxy, origin, gMethodSize);
    *pro_code_item = temp;
}
