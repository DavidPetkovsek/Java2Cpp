package easyJNI2.lib;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import easyJNI2.Constants;
import easyJNI2.CppClass;
import easyJNI2.VarType;

public class JNIType {

	public static final JNIType Void = new JNIType(void.class, null);
	private final Parameter p;
	private final Field f;
	private final Class<?> c;
	private final Type t;
	private final VarType v;
	private final JNIType inner;
	private final int dims;
	
	private JNIType(Parameter p, Class<?> c, Type t, Field f) {
		assert c != null : "Class passed to JNIType is null";
		this.p = p;
		this.t = t;
		this.c = c;
		this.f = f;
		this.v = VarType.get(this.c, this.t);
		if(c.isArray()) {
			Class<?> cc = c;
			int i = 0;
			for(; cc.isArray(); i++)
				cc = cc.getComponentType();
			dims = i;
			inner = new JNIType(cc, null);
		} else {
			dims = 0;
			inner = null;
		}
	}
	public JNIType(Parameter p, Type t) {
		this(p, p.getType(), t, null);
	}

	public JNIType(Class<?> c, Type t) {
		this(null, c, t, null);
	}

	public JNIType(Field f) {
		this(null, f.getType(), f.getGenericType(), f);
	}

	public boolean isAny(VarType type, VarType... types ) {
		if(type == v)
			return true;
		for(VarType t : types)
			if(t == v)
				return true;
		return false;
	}
	public int getArrayDims() { return dims; }
	public JNIType getArrayType() { return inner; }
	public boolean isPrimitive() { return c.isPrimitive(); }
	public boolean isArray() { return c.isArray(); }
	public boolean isGeneric() { return isAny(VarType.jgeneric); }
	public boolean hasDependencyType() { return getDependencyType() != null; }
	public boolean isPublic() { return f != null && Modifier.isPublic(f.getModifiers()); }
	public boolean isProtected() { return f != null && Modifier.isProtected(f.getModifiers()); }
	public boolean isPrivate() { return f != null && Modifier.isPrivate(f.getModifiers()); }
	public boolean isStatic() { return f != null && Modifier.isStatic(f.getModifiers()); }
	public boolean isSynthetic() { return f != null && f.isSynthetic(); }

	public Class<?> getDependencyType() {
		if(isArray() && inner.isAny(VarType.jobject, VarType.jgeneric)) {
			if(inner.isGeneric())
				return Object.class;
			return inner.c;
		} else if(isGeneric())
			return Object.class;
		else if(isAny(VarType.jobject))
			return c;
		return null;
	}
	
	public Class<?> getTypeClass() { return c; }
	
	@Override
	public int hashCode() { return c.hashCode(); }
	
	@Override
	public boolean equals(Object obj) {
		if(t != null && obj instanceof Type)
			return t.equals(obj);
		else if(obj instanceof Parameter)
			return p.equals(obj);
		else if(obj instanceof VarType)
			return v.equals(obj);
		else if(obj instanceof Field)
			return f.equals(obj);
		else
			return c.equals(obj);
	}

	public boolean equals(Type obj) { return t.equals(obj); }
	public boolean equals(Parameter obj) { return p.equals(obj); }
	public boolean equals(VarType obj) { return v.equals(obj); }
	public boolean equals(Field obj) { return f.equals(obj); }
	public boolean equals(Class<?> obj) { return c.equals(obj); }
	
	public String getTypeName() { return t != null ? t.getTypeName() : c.getTypeName(); }
	public String getName() {
		if(f == null) {
			if(p != null && CppClass.ILLEGAL_NAMES.contains(p.getName()))
				return "_"+p.getName();
		}else if(CppClass.ILLEGAL_NAMES.contains(f.getName()))
			return "_"+f.getName();
		return f == null ? (p == null ? null : p.getName()) : f.getName();
	}

	public boolean isField() { return f != null; }
	
	public String getSignature() { return getSignature(null).toString(); }
	public StringBuilder2 getSignature(StringBuilder2 sb) {
		if(sb == null) sb = new StringBuilder2();
		if(isArray()) {
			sb.append("[".repeat(dims));
			inner.getSignature(sb);
			return sb;
		}
		
		return sb.append(v.getSignature(c, t));
	}
	
	
	public String getCppType() { return getCppType(null).toString(); }
	public StringBuilder2 getCppType(StringBuilder2 sb) {
		if(sb == null) sb = new StringBuilder2();
		if(isArray()) {
			sb.append(Constants.namespace, "::", Constants.arrayTypeName, "<");
			inner.getCppType(sb);
			sb.append(", ", dims, ">");
		} else 
			sb.append(v.getCppType(c));
		return sb;
	}
	
	public StringBuilder2 getCppField(StringBuilder2 sb) {
		assert isField();
		if(isStatic())
			sb.append("static ");
		sb.append(Constants.namespace,"::",Constants.varTypeName,"<");
		return getCppType(sb).append("> ",getName(),";\n");
	}	
	public StringBuilder2 getCppStaticFieldInit(StringBuilder2 sb, CppClass declaringClass) {
		assert isField();
		assert isStatic();

		sb.append(Constants.namespace,"::",Constants.varTypeName,"<");
		getCppType(sb).append("> ",declaringClass.getCppType(),"::",getName());
		sb.append("(env, className, \"",getName(),"\", \"").use(getSignature(sb)).append("\");\n");
		return sb;
	}
	public StringBuilder2 getCppFieldInit(StringBuilder2 sb) {
		assert isField();
		assert !isStatic();
		return sb.append(getName(),".init(env, clazz, *(this->obj), \"",getName(),"\", \"").use(getSignature(sb)).append("\");\n");
	}
	
	public String getCallingID() { return v.getCallingID(); }
	
	public String getJValueField() { return v.getJValueField(); }
}
