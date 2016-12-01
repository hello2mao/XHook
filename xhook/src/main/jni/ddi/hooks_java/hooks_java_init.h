#ifndef XHOOK_HELLO2MAO_HOOKS_JAVA_INIT_H
#define XHOOK_HELLO2MAO_HOOKS_JAVA_INIT_H

#include "../dalvikhook/dexstuff.h"

struct dexstuff_t dex_stuff;

void hooks_java_init();
void hook_toString_setup();
void hook_getmethod_setup();

void hook_requestLine_get_setup();
void hook_requestLine_requestPath_setup();

#endif //XHOOK_HELLO2MAO_HOOKS_JAVA_INIT_H
