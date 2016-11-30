#include <stdio.h>
#include <pthread.h>
#include <jni.h>

#include "report.h"
#include "../config.h"

JNIEnv* getJNIEnv(int *needsDetach) {
    JNIEnv *env = NULL;
    if ((*m_vm)->GetEnv(m_vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK){
        int status = (*m_vm)->AttachCurrentThread(m_vm, &env, 0);
        if (status < 0) {
            LOGE("Failed to attach current thread");
            return NULL;
        }
        *needsDetach = 1;
    }
    if (NULL == env) {
        LOGE("GetEnv failed");
    }
    return env;
}

// unused
void *thread_run(void *arg) {
    LOGD("report thread start");
    REPORT_INFO *report_info = (REPORT_INFO *) arg;
    if (NULL == report_info) {
        LOGE("report_info is null");
        pthread_exit((void *) 1);
    }
    int needsDetach = 0;
    JNIEnv *evn=getJNIEnv(&needsDetach);
    print_report_info(report_info);
    jint fd = (jint)report_info->fd;
    jint type = (jint)report_info->type;
    jdouble startTime = (jdouble)report_info->startTime;
    jint timeElapsed = (jint)report_info->timeElapsed;
    jint returnValue = (jint)report_info->returnValue;
    jint errorNum = (jint)report_info->errorNum;
    jstring host = (*evn)->NewStringUTF(evn, report_info->host);
    jstring address = (*evn)->NewStringUTF(evn, report_info->address);
    jstring desc = (*evn)->NewStringUTF(evn, report_info->desc);
    jint port = (jint)report_info->port;
    (*evn)->CallVoidMethod(evn, mobj, mid,
                           fd,
                           type,
                           startTime,
                           timeElapsed,
                           returnValue,
                           errorNum,
                           host,
                           address,
                           desc,
                           port);
    jthrowable exception = (*evn)->ExceptionOccurred(evn);
    if (exception) {
        (*evn)->ExceptionDescribe(evn);
    }
    if (needsDetach == 1) {
        (*m_vm)->DetachCurrentThread(m_vm);
    }
    pthread_exit((void *) 0);
}

// unused
void print_report_info(REPORT_INFO *report_info) {
    LOGD("report info: fd=%d, type=%d, startTime=%f, timeElapsed=%d, returnValue=%d, errorNum=%d, host=%s, "
                 "address=%s, desc=%s, prot=%d\n", report_info->fd, report_info->type,
         report_info->startTime, report_info->timeElapsed, report_info->returnValue,
         report_info->errorNum, report_info->host, report_info->address, report_info->desc,
         report_info->port);
}

void report_data(int fd,
                 int type,
                 double startTime,
                 int timeElapsed,
                 int returnValue,
                 int errorNum,
                 const char *host,
                 char *address,
                 char *desc,
                 int port) {
    LOGD("Start to report data");
    int needsDetach = 0;
    JNIEnv *evn=getJNIEnv(&needsDetach);
    // prepare data
    jint jfd = (jint) fd;
    jint jtype = (jint) type;
    jdouble jstartTime = (jdouble) startTime;
    jint jtimeElapsed = (jint) timeElapsed;
    jint jreturnValue = (jint) returnValue;
    jint jerrorNum = (jint) errorNum;
    jstring jhost = (*evn)->NewStringUTF(evn, host);
    jstring jaddress = (*evn)->NewStringUTF(evn, address);
    jstring jdesc = (*evn)->NewStringUTF(evn, desc);
    jint jport = (jint) port;
    // callback
    (*evn)->CallVoidMethod(evn, mobj, mid,
                           jfd,
                           jtype,
                           jstartTime,
                           jtimeElapsed,
                           jreturnValue,
                           jerrorNum,
                           jhost,
                           jaddress,
                           jdesc,
                           jport);
    // handle exception
    jthrowable exception = (*evn)->ExceptionOccurred(evn);
    if (exception) {
        (*evn)->ExceptionDescribe(evn);
    }
    // detach current thread
    if (needsDetach == 1) {
        LOGD("Try to detach current thread");
        (*m_vm)->DetachCurrentThread(m_vm);
    }
}

void on_socket_created(int fd, double startTime, int returnValue, char *desc) {
    report_data(fd, SOCKET_CREATED, startTime, UNKNOWN_TIMEELAPSED, returnValue, UNKNOWN_ERRORNUM,
                UNKNOWN_HOST, UNKNOWN_ADDRESS, desc, UNKNOWN_PORT);
}

void on_socket_connected(int fd, double startTime, int timeElapsed, int returnValue, char *address,
                         int port) {
    report_data(fd, SOCKET_CONNECTED, startTime, timeElapsed, returnValue, UNKNOWN_ERRORNUM,
                UNKNOWN_HOST, address, UNKNOWN_DESC, port);
}

void on_socket_ssl(int fd, double startTime, int timeElapsed, int returnValue, char *desc) {
    report_data(fd, SOCKET_SSL, startTime, timeElapsed, returnValue, UNKNOWN_ERRORNUM,
                UNKNOWN_HOST, UNKNOWN_ADDRESS, desc, UNKNOWN_PORT);
}

void on_socket_pollout(int fd, double startTime) {
    report_data(fd, SOCKET_POLLOUT, startTime, UNKNOWN_TIMEELAPSED, UNKNOWN_RETURNVALUE,
                UNKNOWN_ERRORNUM, UNKNOWN_HOST, UNKNOWN_ADDRESS, UNKNOWN_DESC, UNKNOWN_PORT);
}

void on_socket_pollin(int fd, double startTime) {
    report_data(fd, SOCKET_POLLIN, startTime, UNKNOWN_TIMEELAPSED, UNKNOWN_RETURNVALUE,
                UNKNOWN_ERRORNUM, UNKNOWN_HOST, UNKNOWN_ADDRESS, UNKNOWN_DESC, UNKNOWN_PORT);
}

void on_dns_event(double startTime, int timeElapsed, int returnValue, const char *host, char *address,
                  char *desc) {
    report_data(UNKNOWN_FD, DNS_EVENT, startTime, timeElapsed, returnValue, UNKNOWN_ERRORNUM, host,
                address, desc, UNKNOWN_PORT);
}

void on_socket_send_over(int fd, double startTime, int timeElapsed, char *address, int port,
                         int returnValue) {
    report_data(fd, SOCKET_SEND_OVER, startTime, timeElapsed, returnValue, UNKNOWN_ERRORNUM,
                UNKNOWN_HOST, address, UNKNOWN_DESC, port);
}

void on_socket_received(int fd, double startTime, int timeElapsed, char *address, int port,
                        int returnValue) {
    report_data(fd, SOCKET_RECEIVED, startTime, timeElapsed, returnValue, UNKNOWN_ERRORNUM,
                UNKNOWN_HOST, address, UNKNOWN_DESC, port);
}

