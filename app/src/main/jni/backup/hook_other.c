//
// Created by maohongbin01 on 16/7/24.
//
#include "../hooks/util.h"
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/uio.h>

int MAX_DATA_LEN = 300;

/**
 * Command execution: execve
 */
int xhook_execve(const char *filename, char *const argv[ ], char *const envp[ ]);
HOOK_INFO system_hook_info_execve = {{}, "libc.", "execve", xhook_execve, xhook_execve};
int xhook_execve(const char *filename, char *const argv[ ], char *const envp[ ]){
    int (*orig_execve)(const char *filename, char *const argv[ ], char *const envp[ ]);
    struct hook_t eph = system_hook_info_execve.eph;
    orig_execve = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_execve(filename, argv, envp);
    hook_postcall(&eph);

    int uid = getuid();

    // Log message for argv
    char *argv_string = NULL;
    array_to_string(argv_string, argv);
    if(argv_string == NULL)
        argv_string = "";
    // Log message for envp
    char *envp_string = NULL;
    array_to_string(envp_string, envp);
    if(envp_string == NULL)
        envp_string = "";

    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"filename\":\"%s\",\"argv\":\"[%s]\",\"evnp\":\"[%s]\"},"
        "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "execve", filename, argv_string, envp_string, status);
    if(argv_string != NULL && strcmp(argv_string, ""))
        free(argv_string);
    if(envp_string != NULL && strcmp(envp_string, ""))
        free(envp_string);
    return status;
}

/**
 * Application scope native lib
 */

int xhook_test(int a, char* string);
HOOK_INFO custom_hook_info_test = {{}, "libxhooktest", "test", xhook_test, xhook_test};

int xhook_test(int a, char* string){
    int (*orig_test)(int a, char* string);
    struct hook_t eph = custom_hook_info_test.eph;
    orig_test = (void*)eph.orig;
    hook_precall(&eph);
    int res = orig_test(a, string);
    hook_postcall(&eph);
    int uid = getuid();
    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"a\":\"%d\",\"string\":\"%s\"},\"return\":{\"int\":\"%d\"}}}",
        uid, NATIVE_APP_API, "test", a, string, res);
    return res;
}
