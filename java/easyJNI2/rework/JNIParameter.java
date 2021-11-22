package easyJNI2.rework;

import java.lang.reflect.Parameter;

import easyJNI2.lib.StringBuilder2;

/**
 * The JNIParameter is meant to parallel a standard java.lang.reflect.Parameter.
 */
public class JNIParameter extends JNIMember{

	/** The parameter this object represents */
	protected final Parameter p;
	
	/**
	 * Construct a new JNIParameter to represent a java.lang.reflect.Parameter
	 * 
	 * @param p The parameter to represent.
	 */
	public JNIParameter(Parameter p) {
		this.p = p;
		Class<?> type = p.getType();
		while(type.isArray()) type = type.getComponentType();
		addDependency(EJNI.createJNI(type));
	 }

	@Override
	public int getModifiers() { 
		return p.getModifiers();
	 }
	
	@Override
	public String toString() { 
		return p.getType() + " " + p.getName();
	 }

	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) {
		if(isFinal())
			sb.append("const ");
		sb.append(JNIConv.getCppParameterType(p.getType()), " ", p.getName());
		return sb;
	 }

}
