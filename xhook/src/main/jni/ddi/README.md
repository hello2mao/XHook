## ddi - Dynamic Dalvik Instrumentation Toolkit

Simple and easy to use toolkit for dynamic instrumentation of Dalvik code. 
Instrumentation is based on library injection and hooking method entry points (in-line hooking). 
The actual instrumentation code is written using the JNI interface.

The DDI further supports loading additional dex classes into a process. 
This enables instrumentation code to be partially written in Java and thus simplifies interacting with the instrumented process and the Android framework.

Ref: [ddi](https://github.com/crmulliner/ddi)