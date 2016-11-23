#include <android/log.h>
#include <stdbool.h>
#include <time.h>
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>


#include "../base/hook.h"
#include "../config.h"
#include "../report_data/report.h"

typedef struct hook_info {
    struct hook_t eph;
    char *libname;
    char *funcname;
    void *hook_arm;
    void *hook_thumb;
} HOOK_INFO;

typedef struct hooked_info_node {
    char *libname;
    char *funcname;
    struct hooked_info_node *next;
} HOOKED_INFO_NODE;

bool find_file_path_from_fd(int uid, int pid, int fd, char *buffer);
bool is_func_hooked(HOOKED_INFO_NODE *root, HOOK_INFO hook_info);
void add_hooked_info(HOOKED_INFO_NODE *root, HOOK_INFO hook_info);
int get_id();
void to_hex(char *input_string, char *output_string, int count);
void array_to_string(char *dest_string, char *const src_array[]);
