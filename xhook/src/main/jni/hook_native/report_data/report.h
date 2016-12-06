#ifndef XHOOK_REPORT_H
#define XHOOK_REPORT_H

#include <pthread.h>
#include <jni.h>

extern jmethodID mid;
extern jclass objclass;
extern jobject mobj;
extern JavaVM *m_vm;

typedef struct report_info {
    int fd;
    int type;
    double startTime;
    int timeElapsed;
    int returnValue;
    int errorNum;
    char host[50];
    char address[400]; // increase this if needed for larger ipList
    char desc[200]; // increase this if needed for larger desc
    int port;
} REPORT_INFO;

JNIEnv *getJNIEnv(int *needsDetach);
void *thread_run(void *arg);
void print_report_info(REPORT_INFO *report_info);

// SocketEvent
void on_socket_created(int fd, double startTime, int returnValue, char *desc);
void on_socket_connected(int fd, double startTime, int timeElapsed, int returnValue, char *address,
                         int port);
void on_socket_ssl(int fd, double startTime, int timeElapsed, int returnValue, char *desc);
void on_socket_pollout(int fd, double startTime);
void on_socket_pollin(int fd, double startTime);
void on_dns_event(double startTime, int timeElapsed, int returnValue, const char *host, char *address,
                  char *desc);
void on_socket_received(int fd, double startTime, int timeElapsed, char *address, int port,
                        int returnValue);
void on_socket_send_over(int fd, double startTime, int timeElapsed, char *address, int port,
                         int returnValue);

#endif //XHOOK_REPORT_H
