#include <sys/socket.h>
#include <arpa/inet.h>
#include <sys/time.h>
#include <string.h>
#include <hook_native/report_data/report.h>
#include <hook_native/base/hook.h>

// Support version：
// (1)android 4.4
int xhook_connect(int socket, const struct sockaddr *address, socklen_t address_len);
HOOK_INFO hook_info_connect = {{}, "libc.", "connect", xhook_connect, xhook_connect};
int xhook_connect(int socket, const struct sockaddr *address, socklen_t address_len) {
    struct timeval t1;
    gettimeofday(&t1,NULL);
    int (*orig_connect)(int, const struct sockaddr *, socklen_t);
    struct hook_t eph = hook_info_connect.eph;
    orig_connect = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_connect(socket, address, address_len);
    hook_postcall(&eph);
    struct timeval t2;
    gettimeofday(&t2,NULL);

    // http://androidxref.com/4.4.4_r1/xref/libcore/luni/src/main/native/NetworkUtilities.cpp#103
    struct sockaddr_in6 *addr = (struct sockaddr_in6 *) address;
    // fd = Libcore.os.socket(AF_INET6, stream ? SOCK_STREAM : SOCK_DGRAM, 0);
    // We use AF_INET6 sockets, so we want an IPv6 address (which may be a IPv4-mapped address).
    if (addr->sin6_family == AF_INET6) {
        char ipv6buf[32] = {};
        char ipv4buf[16] = {};
        int port = ntohs(addr->sin6_port);
        inet_ntop(AF_INET6, &addr->sin6_addr, ipv6buf, 32);
        LOGD("ipv6 = %s\n", ipv6buf);
        // e.g. ::ffff:61.135.162.244
        memcpy(ipv4buf, ipv6buf + 7, sizeof(ipv4buf));
        double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
        int timeElapsed = (t2.tv_sec - t1.tv_sec) * 1000 + (t2.tv_usec - t1.tv_usec) / 1000;
        on_socket_connected(socket, startTime, timeElapsed, status, ipv4buf, port);
        LOGD("--------------------------------------------------------\n"
             "* <%s>\n"
             "* %s\n"
             "* fd:          %d\n"
             "* address:     %s\n"
             "* port:        %d\n"
             "* startTime:   %f\n"
             "* timeElapsed: %d\n"
             "* status:      %d\n"
             "--------------------------------------------------------\n", "connect", __func__,
             socket, ipv4buf, port, startTime, timeElapsed, status);
    }

    return status;
}