package easyJNI2.rework;

import java.lang.reflect.Field;

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
		addDependency(EJNI.createJNI(f.getType()));
	 }
	


	@Override
	public int getModifiers() { 
		return f.getModifiers();
	 }

}
