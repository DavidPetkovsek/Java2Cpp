package easyJNI2.rework;

import easyJNI2.lib.StringBuilder2;

/**
 * The JNIReturnType is meant to represent the return type of a java.lang.reflect.Method.
 */
public class JNIReturnType extends JNIMember{

	/** The return type class this object represents */
	protected final Class<?> c;
	
	/**
	 * Construct a new JNIReturnType.
	 * 
	 * @param returnType The return type to represent.
	 */
	public JNIReturnType(Class<?> returnType) {
		c = returnType;
		Class<?> type = c;
		while(type.isArray()) type = type.getComponentType();
		addDependency(EJNI.createJNI(type));
	 }

	@Override
	public int getModifiers() { 
		return 0; // no modifiers
	 }

	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) { 
		sb.append(JNIConv.getCppParameterType(c));
		return sb;
	 }

}
