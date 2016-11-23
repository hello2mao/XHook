#include <netdb.h>
#include <arpa/inet.h>

#include "util.h"


/**
 * getaddrinfo
 * getnameinfo
 * gethostbyname
 */

// Support version：
// (1)android 4.4
int xhook_getaddrinfo(const char *, const char *, const struct addrinfo *, struct addrinfo **);
HOOK_INFO hook_info_getaddrinfo = {{}, "libc.", "getaddrinfo", xhook_getaddrinfo, xhook_getaddrinfo};
int xhook_getaddrinfo(const char *hostname, const char *servname, const struct addrinfo *hints,
                      struct addrinfo **res) {
    int (*orig_getaddrinfo)(const char *, const char *, const struct addrinfo *, struct addrinfo **);
    struct timeval t1;
    gettimeofday(&t1,NULL);
    struct hook_t eph = hook_info_getaddrinfo.eph;
    orig_getaddrinfo = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_getaddrinfo(hostname, servname, hints, res);
    hook_postcall(&eph);
    struct timeval t2;
    gettimeofday(&t2,NULL);

    // InetAddress.java#getAllByNameImpl
    // (1)parseNumericAddressNoThrow
    // (2)lookupHostByName
    // TODO:
    if ((hints != NULL) && (hints->ai_socktype == SOCK_STREAM)
        && (hints->ai_flags == AI_ADDRCONFIG)) {
        LOGD("<dns>hostname=%s, servname=%s, ai_family=%d, ai_socktype=%d, ai_flags=0x%X, ai_protocol=%d",
             hostname, servname, hints->ai_family, hints->ai_socktype, hints->ai_flags, hints->ai_protocol);

        struct addrinfo *cur;
        struct sockaddr_in *addr;
        char ipbuf[16] = {};
        char address[400] = {};
        char desc[200] = {};
        for (cur = *res; cur != NULL; cur = cur->ai_next) {
            addr = (struct sockaddr_in *) cur->ai_addr;
            inet_ntop(AF_INET, &addr->sin_addr, ipbuf, 16);
            LOGD("<dns>ip=%s, port=%d, ai_canonname=%s\n", ipbuf, addr->sin_port, cur->ai_canonname);
            if (strlen(address) == 0) {
                strcpy(address, ipbuf);
            } else {
                strcat(address, ipbuf);
            }
            strcat(address, ";");
        }
        if (status != 0) {
            strcpy(desc, gai_strerror(status));
        }
        double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
        int timeElapsed = (t2.tv_sec - t1.tv_sec) * 1000 + (t2.tv_usec - t1.tv_usec) / 1000;
        on_dns_event(startTime, timeElapsed, status, hostname, address, desc);
        LOGD("--------------------------------------------------------\n"
             "* <%s>\n"
             "* %s\n"
             "* startTime:     %f\n"
             "* timeElapsed:   %d\n"
             "* status:        %d\n"
             "* hostname:      %s\n"
             "* address:       %s\n"
             "* desc:          %s\n"
             "--------------------------------------------------------\n", "dns", __func__,
             startTime, timeElapsed, status, hostname, address, desc);
    }
    return status;
}


