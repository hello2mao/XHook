#include <termios.h>

typedef struct lib_hook_info_node {
    char *hook_info_name;
    struct lib_hook_info_node *next;
}LIB_HOOK_INFO_NODE;

int find_name(pid_t pid, char *name, char *libn, unsigned long *addr);
int find_libbase(pid_t pid, char *libn, unsigned long *addr);
LIB_HOOK_INFO_NODE* build_hook_info_list_v1(char *filepath, char *condition);
LIB_HOOK_INFO_NODE* build_hook_info_list_v2(const char *filepath, char **condition_list);



