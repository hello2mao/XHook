#include <openssl/ssl.h>

#include <hook_native/report_data/report.h>
#include <hook_native/base/hook.h>
#include <errno.h>

// Support version：
// (1)android 4.4
int xhook_SSL_read(SSL *,void *,int);
HOOK_INFO hook_info_SSL_read = {{}, "libssl.", "SSL_read", xhook_SSL_read, xhook_SSL_read};
int xhook_SSL_read(SSL *ssl,void *buf,int num) {
    int (*orig_SSL_read)(SSL *,void *,int);
    struct timeval t1;
    gettimeofday(&t1,NULL);
    struct hook_t eph = hook_info_SSL_read.eph;
    orig_SSL_read = (void*)eph.orig;
    hook_precall(&eph);
    int recv_count = orig_SSL_read(ssl, buf, num);
    hook_postcall(&eph);
    struct timeval t2;
    gettimeofday(&t2,NULL);

    int fd = SSL_get_fd(ssl);
    double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
    int timeElapsed = (t2.tv_sec - t1.tv_sec) * 1000 + (t2.tv_usec - t1.tv_usec) / 1000;
    int sslError = SSL_ERROR_NONE;
    if (recv_count <= 0) {
        sslError = SSL_get_error(ssl, recv_count);
    }
    on_socket_received(fd, startTime, timeElapsed, UNKNOWN_ADDRESS, UNKNOWN_PORT, recv_count);
    LOGD("--------------------------------------------------------\n"
                 "* <%s>\n"
                 "* %s\n"
                 "* recv_count:    %d\n"
                 "* fd:            %d\n"
                 "* startTime:     %f\n"
                 "* timeElapsed:   %d\n"
                 "* sslError:      %d\n"
                 "--------------------------------------------------------\n", "SSL_read",
         __func__, recv_count, fd, startTime, timeElapsed, sslError);

    return recv_count;
}