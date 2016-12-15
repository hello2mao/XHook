#include "art_helper.h"
#include "art.h"

static void (*art_quick_to_interpreter_bridge)(void *);
static void (*artInterpreterToInterpreterBridge)(void *);
static int gCodeOffset = 0;
static int gInterpretOffset = 0;
static int gDexOffset = 0;
static int gMethodSize = 0;

void init_parameter(int version) {
    void *handle = dlopen("libart.so", RTLD_LAZY | RTLD_GLOBAL);
    art_quick_to_interpreter_bridge = (void (*)(void*)) dlsym(handle, "art_quick_to_interpreter_bridge");
    artInterpreterToInterpreterBridge = (void (*)(void*)) dlsym(handle, "artInterpreterToInterpreterBridge");
    if (art_quick_to_interpreter_bridge == NULL || artInterpreterToInterpreterBridge == NULL) {
        LOGE("find bridge failed!");
    }

    switch(version) {
        case ART_4_4:
            // for 4.4
            gCodeOffset = 40;
            gInterpretOffset = 44;
            gDexOffset = 32;
            gMethodSize = 80;
            break;
        case ART_5_0:
            // for 5.0
            gCodeOffset = 40;
            gInterpretOffset = 24;
            gDexOffset = 60;
            gMethodSize = 72;
            break;
        case ART_5_1:
            // for 5.1
            gCodeOffset = 44;
            gInterpretOffset = 36;
            gDexOffset = 24;
            gMethodSize = 48;
            break;
        default:
            LOGE("not support ART version");
            break;
    }
    LOGD("gCodeOffset = %d, gInterpretOffset = %d, gDexOffset = %d, gMethodSize = %d\n", gCodeOffset,
         gInterpretOffset, gDexOffset, gMethodSize);
}

void switchQuickToInterpret(void *artmeth) {
    LOGD("switchQuickToInterpret");
    int *bridge = (int *) (artmeth + gCodeOffset);
    *bridge = (int)(art_quick_to_interpreter_bridge);
}

void switchInterpretToInterpret(void *artmeth) {
    LOGD("switchInterpretToInterpret");
    int *bridge = (int *) (artmeth + gInterpretOffset);
    *bridge = (int)(artInterpreterToInterpreterBridge);
}

void doHook(void *origin, void *proxy) {
    LOGD("doHook");
    int *ori_code_item = (int *) (origin + gDexOffset);
    int *pro_code_item = (int *) (proxy + gDexOffset);
    int temp = *ori_code_item;
    *ori_code_item = *pro_code_item;
    memcpy(proxy, origin, gMethodSize);
    *pro_code_item = temp;
}
