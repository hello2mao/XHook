//
// Created by maohongbin01 on 16/11/30.
//

#ifndef XHOOK_HELLO2MAO_HOOKS_JAVA_INIT_H
#define XHOOK_HELLO2MAO_HOOKS_JAVA_INIT_H

#include "dalvikhook/dexstuff.h"

struct dexstuff_t dex_stuff;

void hooks_java_init();
void hook_toString_setup();

#endif //XHOOK_HELLO2MAO_HOOKS_JAVA_INIT_H
