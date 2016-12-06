#include <openssl/ssl.h>

#include <hook_native/report_data/report.h>
#include <hook_native/base/hook.h>
#include <errno.h>

// Support version：
// (1)android 4.4
int xhook_SSL_do_handshake(SSL *s);
HOOK_INFO hook_info_SSL_do_handshake = {{}, "libssl.", "SSL_do_handshake", xhook_SSL_do_handshake, xhook_SSL_do_handshake};
int xhook_SSL_do_handshake(SSL *ssl) {
    int (*orig_SSL_do_handshake)(SSL *);
    struct timeval t1;
    gettimeofday(&t1,NULL);
    struct hook_t eph = hook_info_SSL_do_handshake.eph;
    orig_SSL_do_handshake = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_SSL_do_handshake(ssl);
    hook_postcall(&eph);
    struct timeval t2;
    gettimeofday(&t2,NULL);

    int fd = SSL_get_fd(ssl);
    double startTime = t1.tv_sec / 1000.0 + t1.tv_usec / 1000.0 / 1000.0 / 1000.0;
    int timeElapsed = (t2.tv_sec - t1.tv_sec) * 1000 + (t2.tv_usec - t1.tv_usec) / 1000;
    char desc[200] = {};
    // see org_conscrypt_NativeCrypto.cpp
    if (status != 1) {
        int sslError = SSL_get_error(ssl, status);
        if (sslError == SSL_ERROR_WANT_READ || sslError == SSL_ERROR_WANT_WRITE) {
            LOGD("sslSelect, try to SSL_do_handshake again");
        } else {
            // clean error. See SSL_do_handshake(3SSL) man page.
            if (status == 0) {
                if (sslError == SSL_ERROR_NONE || (sslError == SSL_ERROR_SYSCALL && errno == 0)) {
                    LOGD("Connection closed by peer");
                    strcpy(desc, "Connection closed by peer");
                } else {
                    LOGD("SSL handshake terminated");
                    strcpy(desc, "SSL handshake terminated");
                }
            }
            // unclean error. See SSL_do_handshake(3SSL) man page.
            if (status < 0) {
                /*
                 * Translate the error and throw exception. We are sure it is an error
                 * at this point.
                 */
                LOGD("SSL handshake aborted");
            }

        }
    } else {
        LOGD("SSL handshake success!");
    }
    on_socket_ssl(fd, startTime, timeElapsed, status, desc);
    LOGD("--------------------------------------------------------\n"
         "* <%s>\n"
         "* %s\n"
         "* status:        %d\n"
         "* fd:            %d\n"
         "* startTime:     %f\n"
         "* timeElapsed:   %d\n"
         "* desc:          %s\n"
         "--------------------------------------------------------\n", "SSL_do_handshake",
         __func__, status, fd, startTime, timeElapsed, desc);

    return status;
}