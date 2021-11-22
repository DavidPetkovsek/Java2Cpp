package easyJNI2.rework;

import java.lang.reflect.Field;

import easyJNI2.lib.StringBuilder2;

/**
 * The JNIField is meant to parallel a standard java.lang.reflect.Field.
 */
public class JNIField extends JNIMember{

	/** The field this object represents */
	private final Field f;
	
	/**
	 * Construct a new JNIField to represent a java.lang.reflect.Field
	 * 
	 * @param f The field to represent.
	 */
	public JNIField(Field f) {
		this.f = f;
		Class<?> type = f.getType();
		while(type.isArray()) type = type.getComponentType();
		addDependency(EJNI.createJNI(type));
	 }
	
	@Override
	public int getModifiers() { 
		return f.getModifiers();
	 }

	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) {
		if(isStatic())
			sb.append("static ");
		if(isFinal())
			sb.append("const ");
		sb.append(JNIConv.getCppFieldType(f.getType()), " ", f.getName(), ";");
		return sb;
	 }

}
