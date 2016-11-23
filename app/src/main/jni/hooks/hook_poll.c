#include <poll.h>

#include "util.h"

// Support version：
// (1)android 4.4
int xhook_poll(struct pollfd *, nfds_t, int);
HOOK_INFO hook_info_poll = {{}, "libc.", "poll", xhook_poll, xhook_poll};
int xhook_poll(struct pollfd fds[], nfds_t nfds, int timeout) {
    struct timeval t1;
    gettimeofday(&t1,NULL);
    int (*orig_poll)(struct pollfd *, nfds_t, int);
    struct hook_t eph = hook_info_poll.eph;
    orig_poll = (void*) eph.orig;
    hook_precall(&eph);
    int status = orig_poll(fds, nfds, timeout);
    hook_postcall(&eph);

    if (status > 0) {
        for (int i = 0; i < nfds; i++) {
            LOGD("fds[%d].revents=%x\n", i, fds[i].revents);
            if (fds[i].revents & POLLOUT) {
                double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
                int fd = fds[i].fd;
                on_socket_pollout(fd, startTime);
                LOGD("--------------------------------------------------------\n"
                     "* <%s>\n"
                     "* %s\n"
                     "* fd:         %d\n"
                     "* startTime:  %f\n"
                     "* nfds:       %d\n"
                     "* status:     %d\n"
                     "* timeout:    %d\n"
                     "--------------------------------------------------------\n", "poll out", __func__,
                     fd, startTime, nfds, status, timeout);
            }
            // TODO:对于Android 4.4.4从源码里看出没有使用POLLIN
            if (fds[i].revents & POLLIN) {
                LOGD("timeout=%d\n", timeout);
                double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
                int fd = fds[i].fd;
                on_socket_pollin(fd, startTime);
                LOGD("--------------------------------------------------------\n"
                     "* <%s>\n"
                     "* %s\n"
                     "* fd:         %d\n"
                     "* startTime:  %f\n"
                     "* nfds:       %d\n"
                     "* status:     %d\n"
                     "* timeout:    %d\n"
                     "--------------------------------------------------------\n", "poll in", __func__,
                     fd, startTime, nfds, status, timeout);
            }
        }
    }

    return status;
}