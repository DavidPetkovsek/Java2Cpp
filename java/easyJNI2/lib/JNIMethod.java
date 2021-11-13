package easyJNI2.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

import easyJNI2.Constants;
import easyJNI2.CppClass;
import easyJNI2.VarType;

public class JNIMethod {

	private final Executable e;
	private final Method m;
	private final Constructor<?> ctr;
	private final List<JNIType> params;
	private final JNIType returnType;
	
	private JNIMethod(Method m, Constructor<?> ctr, Executable e) {
		assert e != null : "Passed null method or constructor to JNIMethod";
		this.e = e;
		this.m = m;
		this.ctr =ctr;
		ArrayList<JNIType> params = new ArrayList<>();
		int start = e.getParameterTypes().length-e.getGenericParameterTypes().length;
		for(int i = start; i < e.getGenericParameterTypes().length; i++)
			params.add(new JNIType(e.getParameters()[i], e.getGenericParameterTypes()[i-start]));
		this.params = Collections.unmodifiableList(params);
		if(m == null)
			returnType = JNIType.Void;
		else
			returnType = new JNIType(m.getReturnType(), m.getGenericReturnType());
	}
	public JNIMethod(Method m) { this(m, null, m); }
	public JNIMethod(Constructor<?> ctr) { this(null, ctr, ctr); }

	public boolean isPublic() { return Modifier.isPublic(e.getModifiers()); }
	public boolean isProtected() { return Modifier.isProtected(e.getModifiers()); }
	public boolean isPrivate() { return Modifier.isPrivate(e.getModifiers()); }
	public boolean isStatic() { return Modifier.isStatic(e.getModifiers()); }
	public boolean isFinal() { return Modifier.isFinal(e.getModifiers()); }
	public boolean isAbstract() { return Modifier.isAbstract(e.getModifiers()); }
	public boolean isSynthetic() { return e.isSynthetic(); }
	public boolean isBridge() { return m != null && m.isBridge(); }
	
	public List<JNIType> getParameters() { return params; }
	public int getParameterCount() { return params.size(); }
	public JNIType getParameter(int i) { return params.get(i); }
	public JNIType getReturnType() { return returnType; }
	
	public boolean isMethod() { return m != null; }
	public boolean isConstructor() { return ctr != null; }
	
	public String getSignature() { return getSignature(null).toString(); }
	
	public StringBuilder2 getSignature(StringBuilder2 sb) {
		if(sb == null) sb = new StringBuilder2();
		sb.append("(");
		for(JNIType t : params)
			t.getSignature(sb);
		sb.append(")").use(returnType.getSignature(sb));
		return sb;
	}
	
	public boolean softMatch(JNIMethod m2) {
		if(!m.getGenericReturnType().equals(m2.m.getGenericReturnType()))
			return false;
		if(!m.getName().contentEquals(m2.m.getName()))
			return false;
		if(m.getParameterCount() != m2.m.getParameterCount())
			return false;
		for(int i = 0; i < m.getParameterCount(); i++)
			if(!m.getGenericParameterTypes()[i].equals(m2.m.getGenericParameterTypes()[i]))
				return false;
		return true;
	}
	
	public String getName() { 
		if(CppClass.ILLEGAL_NAMES.contains(e.getName()))
			return "_"+e.getName();
		return e.getName();
	}
	
	private StringBuilder2 getCppConstructorHead(boolean header, StringBuilder2 sb, CppClass declaring, CppClass parent) {
		if(params.size() <= 1)
			sb.append("explicit ");
		if(!header)
			sb.append(declaring.getCppType(),"::");
		sb.append(declaring.getSimpleName(), "(");
			
		for(int i = 0; i < getParameterCount(); i++) {
			getParameter(i).getCppType(sb);
			sb.append(" j_", getParameter(i).getName());
			if(i+1 < getParameterCount())
				sb.append(", ");
		}
		if(header) {
			sb.append(");\n");
		} else {
			if(parent != null)
				sb.append(") : ",parent.getCppType());
			else
				sb.append(") : ", Constants.namespace, "::", Constants.baseObject);
			sb.append("(nullptr, nullptr) {\n");
		}
		return sb;
	}
	
	public StringBuilder2 getCppConstructor(boolean header, StringBuilder2 sb, CppClass declaring, CppClass parent, int tabs, BiConsumer<StringBuilder2, Integer> fieldInit) {
		assert isConstructor() : "Called getCppConstructor with a method";
		if(sb == null) sb = new StringBuilder2();
		sb.append("\t".repeat(tabs));
		getCppConstructorHead(header, sb, declaring, parent);
		if(!header) {
			fieldInit.accept(sb, ++tabs);
			sb.append("\t".repeat(tabs), "static jmethodID mid = ",Constants.namespace,"::GetMethodID(env, clazz, ");
			sb.append("\"<init>\", \"").use(getSignature(sb)).append("\");\n","\t".repeat(tabs));
			sb.append("obj = ",Constants.namespace,"::NewObject(env, clazz, mid");
			if(getParameterCount() > 0) {
				sb.append(", {");
				for(JNIType t : params)
					sb.append("{",t.getJValueField()," = ",t.hasDependencyType()?"*":"","j_",t.getName(),"}, ");
				sb.trimR(2).append("}");
			}
			sb.append(");\n","\t".repeat(tabs-1),"}\n");
		}
		return sb;
	}
	
	private StringBuilder2 getCppMethodHead(boolean header,StringBuilder2 sb, CppClass declaring, boolean overridden) {
		sb.append(isStatic() ? "static " : "virtual ");
		returnType.getCppType(sb).append(" ");
		if(!header)
			sb.append(declaring.getCppType(),"::");
		sb.append(getName(), "(");
		for(int i = 0; i < getParameterCount(); i++) {
			getParameter(i).getCppType(sb);
			sb.append(" j_", getParameter(i).getName());
			if(i+1 < getParameterCount())
				sb.append(", ");
		}
		sb.append(") ");
		if(!isStatic())
			sb.append("const ");
		if(overridden)
			sb.append("override ");
		if(isFinal())
			sb.append("final ");
		return sb;
	}
	
	public StringBuilder2 getCppMethod(boolean header, StringBuilder2 sb, CppClass declaring, boolean overriden, int tabs) {
		assert isMethod() : "Called getCppMethodHead with a constructor";
		if(sb == null) sb = new StringBuilder2();
		if(isAbstract() && !header)
			return sb;
		
		sb.append("\t".repeat(tabs));
		getCppMethodHead(header, sb, declaring, overriden);
		// Body
		if(isAbstract())
			sb.append("= 0;\n");
		else if(header)
			sb.trimR(1).append(";\n");
		else {
			tabs++;
			sb.append("{\n", "\t".repeat(tabs));
			sb.append("static jmethodID mid = ",Constants.namespace,"::Get",isStatic()?"Static":"","MethodID(env, clazz, ");
			sb.append("\"",getName(),"\", \"").use(getSignature(sb)).append("\");\n","\t".repeat(tabs));
			getCppMethodTail(sb, tabs);
		}
		return sb;
	}
	
	private StringBuilder2 getCppMethodTail(StringBuilder2 sb, int tabs) {
		if(!returnType.equals(void.class))
			sb.append("return ");
		if(returnType.isAny(VarType.jobject, VarType.jarray, VarType.jgeneric))
			returnType.getCppType(sb).append("(");
		sb.append("env->Call",isStatic()?"Static":"",returnType.getCallingID(),"Method(");
		sb.append(isStatic()?"clazz":"*obj",", mid");
		for(JNIType t : params)
			sb.append(", ",t.hasDependencyType()?"*":"","j_", t.getName());
		if(returnType.isAny(VarType.jobject, VarType.jarray, VarType.jgeneric))
			sb.append(")");
		sb.append(");\n","\t".repeat(tabs-1),"}\n");
		return sb;
	}
}
