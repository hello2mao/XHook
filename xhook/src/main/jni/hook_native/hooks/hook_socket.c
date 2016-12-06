#include <sys/socket.h>

#include <hook_native/report_data/report.h>
#include <hook_native/base/hook.h>

// Support version：
// (1)android 4.4
int xhook_socket(int domain, int type, int protocol);
HOOK_INFO hook_info_socket = {{}, "libc.", "socket", xhook_socket, xhook_socket};
int xhook_socket(int domain, int type, int protocol) {
    struct timeval t1;
    gettimeofday(&t1,NULL);
    int (*orig_socket)(int, int, int);
    struct hook_t eph = hook_info_socket.eph;
    orig_socket = (void *) eph.orig;
    hook_precall(&eph);
    int status = orig_socket(domain, type, protocol);
    hook_postcall(&eph);

//    LOGD("domain=%d, type=%d, protocol=%d, fd=%d\n", domain, type, protocol, status);

    if ((type == SOCK_STREAM) && (domain == AF_INET6)) {
        char desc[200] = {}; // increase if necessary
        if (status < 0) {
            strcpy(desc, strerror(status));
        }
        double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
        on_socket_created(status, startTime, status, desc);
        LOGD("--------------------------------------------------------\n"
             "* <%s>\n"
             "* %s\n"
             "* fd:        %d\n"
             "* startTime: %f\n"
             "* desc:      %s\n"
             "--------------------------------------------------------\n", "socket",
             __func__, status, startTime, desc);
    }
    return status;
}