#include <sys/socket.h>
#include <endian.h>
#include <arpa/inet.h>

#include "util.h"

// Support version：
// (1)android 4.4
ssize_t xhook_recvfrom(int, void *, size_t, unsigned int, const struct sockaddr *, socklen_t *);
HOOK_INFO hook_info_recvfrom = {{}, "libc.", "recvfrom", xhook_recvfrom, xhook_recvfrom};
ssize_t xhook_recvfrom(int sockfd, void *buf, size_t len, unsigned int flags,
                   const struct sockaddr *addr, socklen_t *addrlen) {
    struct timeval t1;
    gettimeofday(&t1,NULL);
    int (*orig_recvfrom)(int, void *, size_t, unsigned int, const struct sockaddr *, socklen_t *);
    struct hook_t eph = hook_info_recvfrom.eph;
    orig_recvfrom = (void*) eph.orig;
    hook_precall(&eph);
    int recv_count = orig_recvfrom(sockfd, buf, len, flags, addr, addrlen);
    hook_postcall(&eph);
    struct timeval t2;
    gettimeofday(&t2,NULL);

    char ipv4buf[16] = {};
    int port = UNKNOWN_PORT;
    if (addr != NULL) {
        struct sockaddr_in6 *address = (struct sockaddr_in6 *) addr;
        char ipv6buf[32] = {};
        port = ntohs(address->sin6_port);
        inet_ntop(AF_INET6, &address->sin6_addr, ipv6buf, 32);
        LOGD("ipv6 = %s\n", ipv6buf);
        // e.g. ::ffff:61.135.162.244
        memcpy(ipv4buf, ipv6buf + 7, sizeof(ipv4buf));
        LOGD("ipv4 = %s, port = %d\n", ipv4buf, port);
    }
    double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
    int timeElapsed = (t2.tv_sec - t1.tv_sec) * 1000 + (t2.tv_usec - t1.tv_usec) / 1000;
    on_socket_received(sockfd, startTime, timeElapsed, ipv4buf, port, recv_count);
    LOGD("--------------------------------------------------------\n"
         "* <%s>\n"
         "* %s\n"
         "* fd:           %d\n"
         "* len:          %d\n"
         "* recv_count:   %d\n"
         "* startTime:    %f\n"
         "* timeElapsed:  %d\n"
         "--------------------------------------------------------\n", "recvfrom", __func__, sockfd,
         len, recv_count, startTime, timeElapsed);
    return recv_count;
}

