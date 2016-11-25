add by mhb， 2016/10/31
hijack.c作为参考代码


> usage: hijack -p PID
                -l LIBNAME
                [-d (debug on)]
                [-z (zygote)]
                [-m (no mprotect)]
                [-s (appname)]
                [-Z (trace count)]
                [-D (debug level)]
  e.g.: #./hijack -d -p PID -l /data/local/tmp/libexample.so


## 参考
ADBI框架
https://github.com/crmulliner/adbi

## 概述
这个文件实现了一个注入工具，可以向 -p 参数指定的进程注入一个so。

## 实现方式
1. 利用ptrace()函数attach到一个进程上
2. 在其调用序列中插入一个调用dlopen()函数的步骤，将一个实现预备好的.so文件加载到要hook的进程中
3. 用动态加载的so里的函数覆盖原有内存里的函数


