//
// Created by maohongbin01 on 16/7/24.
//
#include "util.h"
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/uio.h>

int MAX_DATA_LEN = 300;

/**
 * Socket: socket, connect, bind, listen, accept, sendto, recvfrom
 */
int xhook_socket(int domain, int type, int protocol);
HOOK_INFO system_hook_info_socket = {{}, "libc", "socket", xhook_socket, xhook_socket};
int xhook_socket(int domain, int type, int protocol){
    int (*orig_socket)(int domain, int type, int protocol);
    struct hook_t eph = system_hook_info_socket.eph;
    orig_socket = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_socket(domain, type, protocol);
    hook_postcall(&eph);

    int uid = getuid();

    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"domain\":\"%d\",\"type\":\"%d\",\"protocol\":\"%d\"},"
            "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "socket", domain, type, protocol, status);

    return status;
}

int xhook_connect(int socket, const struct sockaddr *address, socklen_t address_len);
HOOK_INFO system_hook_info_connect = {{}, "libc", "connect", xhook_connect, xhook_connect};
int xhook_connect(int socket, const struct sockaddr *address, socklen_t address_len){
    int (*orig_connect)(int socket, const struct sockaddr *address, socklen_t address_len);
    struct hook_t eph = system_hook_info_connect.eph;
    orig_connect = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_connect(socket, address, address_len);
    hook_postcall(&eph);

    int uid = getuid();

    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"socket\":\"%d\",\"address->sa_family\":\"%d\",\"address->sa_data\":\"%s\",\"address_len\":\"%d\"},"
            "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "connect", socket, address->sa_family, address->sa_data, address_len, status);

    return status;

}

int xhook_bind(int sockfd, const struct sockaddr *addr, socklen_t addrlen);
HOOK_INFO system_hook_info_bind = {{}, "libc", "bind", xhook_bind, xhook_bind};
int xhook_bind(int sockfd, const struct sockaddr *addr, socklen_t addrlen){
    int (*orig_bind)(int sockfd, const struct sockaddr *addr, socklen_t addrlen);
    struct hook_t eph = system_hook_info_bind.eph;
    orig_bind = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_bind(sockfd, addr, addrlen);
    hook_postcall(&eph);

    int uid = getuid();

    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"sockfd\":\"%d\",\"addr->sa_family\":\"%d\",\"addr->sa_data\":\"%s\",\"addrlen\":\"%d\"},"
            "\"return\":{\"int\":\"%d\"}}}", uid, NATIVE_SYSTEM_API, "bind", sockfd, addr->sa_family, addr->sa_data, addrlen, status);

    return status;
}

int xhook_listen(int sockfd, int backlog);
HOOK_INFO system_hook_info_listen = {{}, "libc", "listen", xhook_listen, xhook_listen};
int xhook_listen(int sockfd, int backlog){
    int (*orig_listen)(int sockfd, int backlog);
    struct hook_t eph = system_hook_info_listen.eph;
    orig_listen = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_listen(sockfd, backlog);
    hook_postcall(&eph);

    int uid = getuid();

    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"sockfd\":\"%d\",\"backlog\":\"%d\"},"
            "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "listen", sockfd, backlog, status);

    return status;
}

int xhook_accept(int sockfd, struct sockaddr *addr, socklen_t *addrlen);
HOOK_INFO system_hook_info_accept = {{}, "libc", "accept", xhook_accept, xhook_accept};
int xhook_accept(int sockfd, struct sockaddr *addr, socklen_t *addrlen){
    int (*orig_accept)(int sockfd, struct sockaddr *addr, socklen_t *addrlen);
    struct hook_t eph = system_hook_info_accept.eph;
    orig_accept = (void*)eph.orig;
    hook_precall(&eph);
    int status = orig_accept(sockfd, addr, addrlen);
    hook_postcall(&eph);

    int uid = getuid();
    int sa_family = -1;
    char *sa_data;
    if(addr == NULL){
        sa_family = -1;
        sa_data = "";
    }else{
        sa_family = addr->sa_family;
        sa_data = addr->sa_data;
    }
    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"sockfd\":\"%d\",\"addr->sa_family\":\"%d\",\"addr->sa_data\":\"%s\",\"addrlen\":\"%p\"},"
        "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "accept", sockfd, sa_family, sa_data, addrlen, status);

    return status;
}

int xhook_sendto(int s, const void *msg, size_t len, int flags, const struct sockaddr *to, socklen_t tolen);
HOOK_INFO system_hook_info_sendto = {{}, "libc", "sendto", xhook_sendto, xhook_sendto};
int xhook_sendto(int s, const void *msg, size_t len, int flags, const struct sockaddr *to, socklen_t tolen){
    int (*orig_sendto)(int s, const void *msg, size_t len, int flags, const struct sockaddr *to, socklen_t tolen);
    struct hook_t eph = system_hook_info_sendto.eph;
    orig_sendto = (void*)eph.orig;
    hook_precall(&eph);
    int send_count = orig_sendto(s, msg, len, flags, to, tolen);
    hook_postcall(&eph);

    int uid = getuid();
    int sa_family = -1;
    char *sa_data;
    if(to == NULL){
        sa_family = -1;
        sa_data = "";
    }else{
        sa_family = to->sa_family;
        sa_data = to->sa_data;
    }

    char *tmp_buf = msg;
    int total_count = send_count;
    char *send_content = (char*) malloc(MAX_DATA_LEN * 2 + 1);
    while(total_count > 0 && send_content != NULL && tmp_buf != NULL){
        int copy_count = (total_count >= MAX_DATA_LEN ? MAX_DATA_LEN : total_count);
        to_hex(tmp_buf, send_content, copy_count);
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"s\":\"%d\",\"msg\":\"%s\",\"len\":\"%d\",\"flags\":\"%d\",\"to->sa_family\":\"%d\","
             "\"to->sa_data\":\"%s\",\"tolen\":\"%d\"},\"return\":{\"int\":\"%d\"}}}",
             uid, NATIVE_SYSTEM_API, "sendto", s, send_content, len, flags, sa_family, sa_data, tolen, send_count);
        total_count -= copy_count;
        tmp_buf += copy_count;
    }
    free(send_content);
    return send_count;
}

//int xhook_sendmsg(int s, const struct msghdr *msg, int flags);
//HOOK_INFO system_hook_info_sendmsg = {{}, "libc", "sendmsg", xhook_sendmsg, xhook_sendmsg};
//int xhook_sendmsg(int s, const struct msghdr *msg, int flags){
//    int (*orig_sendmsg)(int s, const struct msghdr *msg, int flags);
//    struct hook_t eph = system_hook_info_sendmsg.eph;
//    orig_sendmsg = (void*)eph.orig;
//    hook_precall(&eph);
//    int status = orig_sendmsg(s, msg, flags);
//    hook_postcall(&eph);
//
//    int uid = getuid();
//
//    char *msg_name = msg->msg_name;
//    if(msg_name == NULL)
//        msg_name = "";
//    struct iovec *msg_iov = msg->msg_iov;
//    if(msg_iov != NULL){
//        size_t iovlen = msg_iov->iov_len;
//        char msg_content[iovlen * 2 + 1];
//        to_hex(msg_iov->iov_base, msg_content, iovlen);
//        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"s\":\"%d\",\"msg->msg_name\":\"%s\",\"msg->msg_iov->iov_base\":\"%s\",\"flags\":\"%d\"},\"return\":{\"int\":\"%d\"}}}",
//                uid, NATIVE_SYSTEM_API, "sendmsg", s, msg_name, msg_content, flags, status);
//    }else
//        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"s\":\"%d\",\"msg->msg_name\":\"%s\",\"msg->msg_iov->iov_base\":\"%s\",\"flags\":\"%d\"},\"return\":{\"int\":\"%d\"}}}",
//                        uid, NATIVE_SYSTEM_API, "sendmsg", s, msg_name, "", flags, status);
//
//    return status;
//}

int xhook_recvfrom(int s, void *buf, size_t len, int flags, struct sockaddr *from, socklen_t *fromlen);
HOOK_INFO system_hook_info_recvfrom = {{}, "libc", "recvfrom", xhook_recvfrom, xhook_recvfrom};
int xhook_recvfrom(int s, void *buf, size_t len, int flags, struct sockaddr *from, socklen_t *fromlen){
    int (*orig_recvfrom)(int s, void *buf, size_t len, int flags, struct sockaddr *from, socklen_t *fromlen);
    struct hook_t eph = system_hook_info_recvfrom.eph;
    orig_recvfrom = (void*)eph.orig;
    hook_precall(&eph);
    int recv_count = orig_recvfrom(s, buf, len, flags, from, fromlen);
    hook_postcall(&eph);

    int uid = getuid();

    int sa_family = -1;
    char *sa_data;
    if(from == NULL){
        sa_family = -1;
        sa_data = "";
    }else{
        sa_family = from->sa_family;
        sa_data = from->sa_data;
    }

    char *tmp_buf = buf;
    int total_count = recv_count;
    char *recv_content = (char*) malloc(MAX_DATA_LEN * 2 + 1);
    while(total_count > 0 && recv_content != NULL && tmp_buf != NULL){
        int copy_count = (total_count >= MAX_DATA_LEN ? MAX_DATA_LEN : total_count);
        to_hex(tmp_buf, recv_content, copy_count);
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"s\":\"%d\",\"buf\":\"%s\",\"len\":\"%d\",\"flags\":\"%d\",\"from->sa_family\":\"%d\","
             "\"from->sa_data\":\"%s\",\"fromlen\":\"%p\"},\"return\":{\"int\":\"%d\"}}}",
            uid, NATIVE_SYSTEM_API, "recvfrom", s, recv_content, len, flags, sa_family, sa_data, fromlen, recv_count);
        total_count -= copy_count;
        tmp_buf += copy_count;
    }
    free(recv_content);
    return recv_count;
}
