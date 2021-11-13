#ifndef __ejni_ejniBasehppSupport_code__
#define __ejni_ejniBasehppSupport_code__

#include <iostream>
#include <jni.h>
#include <memory>
#include <vector>
#include <type_traits>

namespace ejni {

	jobject delocalify(JNIEnv* env, jobject obj) {
		jobject glob = env->NewGlobalRef(obj);
		env->DeleteLocalRef(obj);
		return glob;
	}

	jmethodID GetStaticMethodID(JNIEnv* env, jclass clazz, const char* name, const char* sig) {
		return env->GetStaticMethodID(clazz, name, sig);
	}
	jmethodID GetMethodID(JNIEnv* env, jclass clazz, const char* name, const char* sig) {
		return env->GetMethodID(clazz, name, sig);
	}

	jclass FindClass(JNIEnv* env, const char* name) {
		jclass j = env->FindClass(name);
		return (jclass) delocalify(env, j);
	}

	std::shared_ptr<jobject> NewObject(JNIEnv* env, jclass clazz, jmethodID mid, std::vector<jvalue> v) {
		// https://stackoverflow.com/questions/2923272/how-to-convert-vector-to-array
		// https://stackoverflow.com/questions/11558390/initialize-a-union-array-at-declaration
		return std::make_shared<jobject>(delocalify(env, env->NewObjectA(clazz, mid, &v[0])));
	}

	std::shared_ptr<jobject> NewObject(JNIEnv* env, jclass clazz, jmethodID mid) {
		return std::make_shared<jobject>(delocalify(env, env->NewObject(clazz, mid)));
	}


	class Object {
	protected:
		std::shared_ptr<jobject> obj;
		static JNIEnv *env;
		Object() {}
		Object(void* n, void* m) {}
		Object(jobject j) {
			obj = std::make_shared<jobject>(env->NewGlobalRef(j));
			env->DeleteLocalRef(j);
		}
		~Object() {
			if(obj.use_count() == 1)
				env->DeleteGlobalRef(*obj);
			obj.reset();
		}
	};

	template<typename T>
	using EnableIfJavaTypePolicy = typename std::enable_if_t<std::is_base_of<ejni::Object, T>::value ||
		std::is_same<T, jdouble>::value ||
		std::is_same<T, jfloat>::value ||
		std::is_same<T, jlong>::value ||
		std::is_same<T, jint>::value ||
		std::is_same<T, jshort>::value ||
		std::is_same<T, jchar>::value ||
		std::is_same<T, jbyte>::value ||
		std::is_same<T, jboolean>::value>::value;


	template<typename T, size_t S>
	class Array {
	public:
		virtual jobject operator*() const { return jobject(); }

	};

	template<typename T>
	class Variable {
		//T obj;
	public:
		Variable() {}
		Variable(JNIEnv *env, const char *className, const char *sig, const char *name) {}
		void init(JNIEnv* env, jclass clazz, jobject obj, const char* name, const char* sig) {}

	};
}

#endif