package easyJNI2.rework;

import java.util.ArrayList;
import java.util.regex.Pattern;

import easyJNI2.lib.StringBuilder2;

/**
 * The JNIClass is meant to parallel a standard java.lang.Class.
 */
public class JNIClass extends JNINestable {

	/**
	 * Construct a new JNIClass to represent a java.lang.Class
	 * 
	 * @param c The class to represent.
	 */
	protected JNIClass(Class<?> c) {
		super(c);
		assert !c.isAnnotation() && !c.isInterface() && !c.isEnum() && !c.isArray() && !c.isPrimitive() : "Attempted to create a JNIClass from a non-class class!";
	}

	@Override
	public JNIClass asClass() { return this; }

	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) { 
		String[] cns = c.getPackageName().split(Pattern.quote("."));

		if(!isNested()) {
			sb.append("#pragma once").newLine();
			sb.append("#include \"ejni.hpp\"").newLine();
			for(JNIType dep : hardDep)
				sb.append("#include \"", dep.getCppIncludePath(), "\"").newLine();
			sb.newLine();

			sb.append("namespace ejni{").newLine(1);
			JNINamespace ns = new JNINamespace();
			for(JNIType dep : softDep)
				ns.add(dep);
			ns.buildForwardDecl(sb);
			sb.newLine();
			
			for(String s : cns)
				sb.append("namespace ", s,"{").newLine(1);
		}
		
		
		if(isStatic())
			sb.append("static ");
		sb.append("class ", c.getSimpleName());
		if(isFinal()) sb.append(" final");
		
		ArrayList<JNIType> parents = new ArrayList<>();
		if(parentClass != null) parents.add(parentClass);
		if(!parentInterfaces.isEmpty()) parents.addAll(parentInterfaces);
		if(!parents.isEmpty()) sb.append(": public ", parentClass != null ? "" : "virtual ", parents.get(0).getCppNamespacePath());
		for(int i = 1; i < parents.size(); ++i) sb.append(", public virtual ", parents.get(i).getCppNamespacePath());
		sb.append("{").newLine(1);
		
		
		// CONTENTS GO HERE
		sb.append("protected: // fields").newLine();
		for(JNIField f : fields) {
			if(f.isProtected())
				f.buildCppHeader(sb).newLine();
		}
		sb.append("public: // fields").newLine();
		for(JNIField f : fields) {
			if(f.isPublic())
				f.buildCppHeader(sb).newLine();
		}
		sb.newLine();
		
		sb.append("protected: // constructors").newLine();
		for(JNIConstructor f : constructors) {
			if(f.isProtected())
				f.buildCppHeader(sb).newLine();
		}
		sb.append("public: // constructors").newLine();
		for(JNIConstructor f : constructors) {
			if(f.isPublic())
				f.buildCppHeader(sb).newLine();
		}
		sb.newLine();
		
		sb.append("protected: // methods").newLine();
		for(JNIMethod f : methods) {
			if(f.isProtected())
				f.buildCppHeader(sb).newLine();
		}
		sb.append("public: // methods").newLine();
		for(JNIMethod f : methods) {
			if(f.isPublic())
				f.buildCppHeader(sb).newLine();
		}
		sb.newLine();
		
		sb.append("protected: // nested types").newLine();
		for(JNIType f : nestedTypes) {
			if(f.isProtected())
				f.buildCppHeader(sb).newLine();
		}
		sb.append("public: //  nested types").newLine();
		for(JNIType f : nestedTypes) {
			if(f.isPublic())
				f.buildCppHeader(sb).newLine();
		}
		sb.newLine();
		
		
		sb.newLine(-1);
		sb.append("};");

		if(!isNested()) {
			for(int i = 0; i < cns.length; ++i)
				sb.newLine(-1).append("}");
			sb.newLine(-1).append("}");
		}
		
		return sb;
	 }
}
