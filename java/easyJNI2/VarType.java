package easyJNI2;

import java.lang.reflect.Type;
import java.util.function.Function;

public enum VarType {
	jdouble		((f,t)->"D", f->"jdouble", "Double", ".d"),
	jfloat		((f,t)->"F", f->"jfloat", "Float", ".f"),
	jlong		((f,t)->"J", f->"jlong", "Long", ".j"),
	jint		((f,t)->"I", f->"jint", "Int", ".i"),
	jshort		((f,t)->"S", f->"jshort", "Short", ".s"),
	jbyte		((f,t)->"B", f->"jbyte", "Byte", ".b"),
	jchar		((f,t)->"C", f->"jchar", "Char", ".c"),
	jboolean	((f,t)->"Z", f->"jboolean", "Boolean", ".z"),
	jvoid		((f,t)->"V", f->"void", "Void", ""),
	jobject		((f,t)->"L"+(t==null?f.getTypeName():t.getTypeName()).replaceAll("[.]", "/")+";", f->CppClass.buildCppClass(f, true).getCppType(), "Object", ".l"),
	jarray		((f,t)->"", f->CppClass.buildCppClass(f, true).getCppType(), "Object", ".l"),
	jgeneric	((f,t)->"T"+t.getTypeName(), f->CppClass.buildCppClass(f, true).getCppType(), "Object", ".l");
	
	@FunctionalInterface
	interface TypeFunction <T> { public T run(Class<?> f, Type t); }
	private TypeFunction<String> getSignature;
	private Function<Class<?>, String> getCppType;
	private String callingID;
	private String jvalueField;
	
	VarType(TypeFunction<String> getSig, Function<Class<?>, String> getCppType, String callingID, String jvalueField){
		this.getSignature = getSig;
		this.getCppType = getCppType;
		this.callingID = callingID;
		this.jvalueField = jvalueField;
	}
	
	public String getCppType(Class<?> field) {
		return getCppType.apply(field).replaceAll("\\[\\]", "");
	}
	
	public String getSignature(Class<?> field, Type type) {
		return getSignature.run(field, type).replaceAll("\\[\\]", "");
	}
	
	public static VarType get(Class<?> field, Type type) {
		if(field.isArray())
			return jarray;
		else if(field.isPrimitive()) {
			if (field.isAssignableFrom(double.class))
				return jdouble;
			else if (field.isAssignableFrom(float.class))
				return jfloat;
			else if (field.isAssignableFrom(long.class))
				return jlong;
			else if (field.isAssignableFrom(int.class))
				return jint;
			else if (field.isAssignableFrom(short.class))
				return jshort;
			else if (field.isAssignableFrom(byte.class))
				return jbyte;
			else if (field.isAssignableFrom(char.class))
				return jchar;
			else if (field.isAssignableFrom(boolean.class))
				return jboolean;
			else if(field.isAssignableFrom(void.class))
				return jvoid;
			else
				throw new NullPointerException("Unknown type being parsed: "+field.getTypeName());
		}else if(field == Object.class && type != null && !type.getTypeName().contains("."))
			return jgeneric;
		else
			return jobject;
	}

	public String getCallingID() {
		return callingID;
	}
	
	public String getJValueField() {
		return jvalueField;
	}
}