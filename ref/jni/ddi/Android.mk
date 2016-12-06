# TODO: dalvik hook
DALVIK_HOOK := ddi/dalvikhook/dexstuff.c.arm ddi/dalvikhook/dalvik_hook.c ddi/hooks_java/hooks_java_init.c
HOOKS_JAVA := ddi/hooks_java/hook_toString.c ddi/hooks_java/hook_getMethod.c ddi/hooks_java/hook_Http1xStream_writeRequestHeaders.c
HOOKS_JAVA += ddi/hooks_java/hook_requestLine_get.c ddi/hooks_java/hook_requestLine_requestPath.c
HOOKS_JAVA += ddi/hooks_java/hook_Http1xStream_writeRequest.c
LOCAL_SRC_FILES += $(DALVIK_HOOK) $(HOOKS_JAVA)
LOCAL_SHARED_LIBRARIES += dvm