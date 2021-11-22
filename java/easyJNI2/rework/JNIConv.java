package easyJNI2.rework;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * This class contains static functions for converting different strings and types into their JNI counterparts
 */
public class JNIConv {

	private static final Map<Class<?>, String> primitiveCppMap;
	private static final Map<Class<?>, String> primitiveCppArrayMap;
	private static final Map<Class<?>, String> primitiveCppJNIMap;
	private static final Map<Class<?>, String> primitiveCppEJNIMap;
	

	public static String getCppParameterType(Class<?> type) {
		assert type != null; // void is handled
		if(type.isArray()) {
			while(type.isArray()) type = type.getComponentType();
			if(type.isPrimitive()) return primitiveCppArrayMap.get(type);
			System.out.println(type);
			JNIType t = EJNI.createJNI(type);
			assert t != null;
			return "ejni::ObjectArray<"+t.getCppNamespacePath()+">";
		}
		if(type.isPrimitive()) return primitiveCppMap.get(type);
		JNIType t = EJNI.createJNI(type);
		assert t != null;
		return t.getCppNamespacePath();
	}
	
	public static String getCppFieldType(Class<?> type) { 
		assert type != null && type != void.class;

		if(type.isPrimitive()) return primitiveCppEJNIMap.get(type) + (type.isArray() ? "Array" : "");
		return "ejni::ObjectField"+(type.isArray() ? "Array" : "")+"<"+getCppParameterType(type)+">";
	 }

	static {
		Hashtable<Class<?>, String> map = new Hashtable<>();
		map.put(boolean.class, "bool");
		map.put(byte.class, "int8_t");
		map.put(char.class, "char");
		map.put(short.class, "int16_t");
		map.put(int.class, "int32_t");
		map.put(long.class, "int64_t");
		map.put(float.class, "float");
		map.put(double.class, "double");
		map.put(void.class, "void");
		primitiveCppMap = Collections.unmodifiableMap(map);
		Hashtable<Class<?>, String> map1 = new Hashtable<>();
		map1.put(boolean.class, "ejni::BooleanArray");
		map1.put(byte.class, "ejni::ByteArray");
		map1.put(char.class, "ejni::CharArray");
		map1.put(short.class, "ejni::ShortArray");
		map1.put(int.class, "ejni::IntArray");
		map1.put(long.class, "ejni::LongArray");
		map1.put(float.class, "ejni::FloatArray");
		map1.put(double.class, "ejni::DoubleArray");
		primitiveCppArrayMap = Collections.unmodifiableMap(map1);
		Hashtable<Class<?>, String> map2 = new Hashtable<>();
		map2.put(boolean.class, "jboolean");
		map2.put(byte.class, "jbyte");
		map2.put(char.class, "jchar");
		map2.put(short.class, "jshort");
		map2.put(int.class, "jint");
		map2.put(long.class, "jlong");
		map2.put(float.class, "jfloat");
		map2.put(double.class, "jdouble");
		map2.put(void.class, "void");
		primitiveCppJNIMap = Collections.unmodifiableMap(map2);
		Hashtable<Class<?>, String> map3 = new Hashtable<>();
		map3.put(boolean.class, "ejni::BooleanField");
		map3.put(byte.class, "ejni::ByteField");
		map3.put(char.class, "ejni::CharField");
		map3.put(short.class, "ejni::ShortField");
		map3.put(int.class, "ejni::IntField");
		map3.put(long.class, "ejni::LongField");
		map3.put(float.class, "ejni::FloatField");
		map3.put(double.class, "ejni::DoubleField");
		primitiveCppEJNIMap = Collections.unmodifiableMap(map3);
	}
}
