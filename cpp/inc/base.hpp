
#ifndef __EJNI_BASE__
#define __EJNI_BASE__
#include <jni.h>
#include <vector>

namespace ejni{
    typedef union jvalue {
        jboolean z;
        jbyte    b;
        jchar    c;
        jshort   s;
        jint     i;
        jlong    j;
        jfloat   f;
        jdouble  d;
        jobject  l;
    } jvalue;
    
    jvalue callObjectMethod(JNIEnv *env, jobject &obj, const char *name, const char *sig, const std::vector<jvalue> &args);
    jvalue callObjectMethod(JNIEnv *env, jobject &obj, const char *name, const char *sig, const std::vector<jvalue> &args);
    jvalue callObjectMethod(JNIEnv *env, jobject &obj, const char *name, const char *sig, const std::vector<jvalue> &args);

}

#endif



