//
// Created by maohongbin01 on 16/7/24.
//
#include "../hooks/util.h"
#include <unistd.h>
#include <stdio.h>
#include <errno.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <sys/uio.h>

int MAX_DATA_LEN = 300;

/**
 *  IO : open, read, write - kernel functions
 *  fopen, fread, fwrite - lib functions, can be ignored
 */
int xhook_open(char *filename, int access, int permission);
HOOK_INFO system_hook_info_open = {{}, "libc", "open", xhook_open, xhook_open};
int xhook_open(char *filename, int access, int permission)
{
    // Declare the original method
    int (*orig_open)(char *filename, int access, int permission);
    // Set variable eph to the HOO_INFO struct
    struct hook_t eph = system_hook_info_open.eph;
    // Set the original method address
    orig_open = (void*)eph.orig;
    // Invoke hook_precall
    hook_precall(&eph);
    // Invoke original method
    int status = orig_open(filename, access, permission);
    // Invoke hook_postcall
    hook_postcall(&eph);

    // Log the information as you need
    int uid = getuid();
    if(filename != NULL)
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"filename\":\"%s\",\"access\":\"%d\",\"permission\":\"%d\"},"
            "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "open", filename, access, permission, status);
    return status;
}


int xhook_read(int handle, void *buffer, int nbyte);
HOOK_INFO system_hook_info_read = {{}, "libc.", "read", xhook_read, xhook_read};
int xhook_read(int handle, void *buffer, int nbyte){
    int (*orig_read)(int handle, void *buffer, int nbyte);
    struct hook_t eph = system_hook_info_read.eph;
    orig_read = (void*)eph.orig;
    hook_precall(&eph);
    int read_count = orig_read(handle, buffer, nbyte);
    hook_postcall(&eph);
    int uid = getuid();

    int id = get_id();
    int pid = getpid();
    char file_path[(2*120)+1] = "";
    if(find_file_path_from_fd(uid, pid, handle, file_path)){
        LOGD("Success finding path for uid:%d, pid:%d, fd:%d", uid, pid, handle);
    }

    char *tmp_buf = buffer;
    int total_count = read_count;
    // Log content in hex format, each character is encoded with 2 bytes
    // Malloc memory and free it at the end of function
    char *read_content = (char*) malloc(MAX_DATA_LEN * 2 + 1);
    while(total_count > 0 && tmp_buf != NULL && read_content != NULL){
        int copy_count = (total_count >= MAX_DATA_LEN ? MAX_DATA_LEN : total_count);
        to_hex(tmp_buf, read_content, copy_count);
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"handle\":\"%d\",\"buffer\":\"%s\",\"nbyte\":\"%d\",\"id\":\"%d\",\"path\":\"%s\"},"
             "\"return\":{\"int\":\"%d\"}}}", uid, NATIVE_SYSTEM_API, "read", handle, read_content, nbyte, id, file_path, read_count);
        total_count -= copy_count;
        tmp_buf += copy_count;
    }
    free(read_content);
    return read_count;
}


int xhook_write(int handle, void *buffer, int nbyte);
HOOK_INFO system_hook_info_write = {{}, "libc.", "write", xhook_write, xhook_write};
int xhook_write(int handle, void *buffer, int nbyte){
    int (*orig_write)(int handle, void *buffer, int nbyte);
    struct hook_t eph = system_hook_info_write.eph;
    orig_write = (void*)eph.orig;
    hook_precall(&eph);
    int write_count = orig_write(handle, buffer, nbyte);
    hook_postcall(&eph);
    int uid = getuid();

    int id = get_id();
    int pid = getpid();
    char file_path[(2*120)+1] = "";
    if(find_file_path_from_fd(uid, pid, handle, file_path)){
        LOGD("Success finding path for uid:%d, pid:%d, fd:%d ", uid, pid, handle);
    }

    char *tmp_buf = buffer;
    int total_count = write_count;
    char *write_content = (char*) malloc(MAX_DATA_LEN * 2 + 1);
    while(total_count > 0 && tmp_buf != NULL && write_content != NULL){
        int copy_count = (total_count >= MAX_DATA_LEN ? MAX_DATA_LEN : total_count);
        to_hex(tmp_buf, write_content, copy_count);
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"handle\":\"%d\",\"buffer\":\"%s\",\"nbyte\":\"%d\",\"id\":\"%d\",\"path\":\"%s\"},"
                "\"return\":{\"int\":\"%d\"}}}",uid, NATIVE_SYSTEM_API, "write", handle, write_content, nbyte, id, file_path, write_count);
        total_count -= copy_count;
        tmp_buf += copy_count;
    }
    free(write_content);
    return write_count;
}

/*
FILE* xhook_fopen(const char* path, const char* mode);
HOOK_INFO system_hook_info_fopen = {{}, "libc.", "fopen", xhook_fopen, xhook_fopen};
FILE* xhook_fopen(const char* path, const char* mode)
{
    FILE* (*orig_fopen)(const char* path, const char* mode);
    struct hook_t eph = system_hook_info_fopen.eph;
    orig_fopen = (void*)eph.orig;
    hook_precall(&eph);
    FILE* file = orig_fopen(path, mode);
    hook_postcall(&eph);
    int uid = getuid();
    LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"path\":\"%s\",\"mode\":\"%s\"},\"return\":{\"FILE\":\"%p\"}}}",
        uid, NATIVE_SYSTEM_API, "fopen", path, mode, file);
    return file;
}

size_t xhook_fread(void *buf, size_t size, size_t count, FILE *fp);
HOOK_INFO system_hook_info_fread = {{}, "libc.", "fread", xhook_fread, xhook_fread};
size_t xhook_fread(void *buf, size_t size, size_t count, FILE *fp){
    int (*orig_fread)(void *buf, size_t size, size_t count, FILE *fp);
    struct hook_t eph = system_hook_info_fread.eph;
    orig_fread = (void*)eph.orig;
    hook_precall(&eph);
    size_t read_count = orig_fread(buf, size, count, fp);
    hook_postcall(&eph);
    int uid = getuid();
    if(read_count == 0)
        return read_count;

    int id = get_id();
    int fd = fileno(fp);
    int pid = getpid();
    char file_path[(2*120)+1] = "";
    if(find_file_path_from_fd(uid, pid, fd, file_path)){
        LOGD("Success finding path for uid:%d, pid:%d, fd:%d", uid, pid, fd);
    }

    int total_count = size * read_count;
    char *tmp_buf = buf;

    while(total_count > 0){
        int copy_count = (total_count >= MAX_DATA_LEN ? MAX_DATA_LEN : total_count);
        char read_content[copy_count * 2];
        to_hex(tmp_buf, read_content, copy_count);
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"buf\":\"%s\",\"size\":\"%d\",\"count\":\"%d\",\"id\":\"%d\",\"path\":\"%s\"},"
                "\"return\":{\"size_t\":\"%d\"}}}",
            uid, NATIVE_SYSTEM_API, "fread", read_content, size, count, id, file_path, read_count);
        total_count -= copy_count;
        tmp_buf += copy_count;
    }
    return read_count;
}


size_t xhook_fwrite(const void *buf, size_t size, size_t count, FILE *fp);
HOOK_INFO system_hook_info_fwrite = {{}, "libc.", "fwrite", xhook_fwrite, xhook_fwrite};
size_t xhook_fwrite(const void *buf, size_t size, size_t count, FILE *fp){
    size_t (*orig_fwrite)(const void *buf, size_t size, size_t count, FILE *fp);
    struct hook_t eph = system_hook_info_fwrite.eph;
    orig_fwrite = (void*)eph.orig;
    hook_precall(&eph);
    size_t write_count = orig_fwrite(buf, size, count, fp);
    hook_postcall(&eph);
    int uid = getuid();
    if(write_count == 0)
        return write_count;

    int id = get_id();
    int fd = fileno(fp);
    int pid = getpid();
    char file_path[(2*120)+1] = "";
    if(find_file_path_from_fd(uid, pid, fd, file_path)){
        LOGD("Success finding path for uid:%d, pid:%d, fd:%d ", uid, pid, fd);
    }

    int total_count = size * write_count;
    char *tmp_buf = buf;

    while(total_count > 0){
        int copy_count = (total_count >= MAX_DATA_LEN ? MAX_DATA_LEN : total_count);
        char write_content[copy_count * 2];
        to_hex(tmp_buf, write_content, copy_count);
        LOGI("{\"Basic\":[\"%d\",\"%d\",\"false\"],\"InvokeApi\":{\"%s\":{\"buf\":\"%s\",\"size\":\"%d\",\"count\":\"%d\",\"id\":\"%d\",\"path\":\"%s\"},"
                "\"return\":{\"size_t\":\"%d\"}}}",
            uid, NATIVE_SYSTEM_API, "fwrite", write_content, size, count, id, file_path, write_count);
        total_count -= copy_count;
        tmp_buf += copy_count;
    }
    return write_count;
}*/
