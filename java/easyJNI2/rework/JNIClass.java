package easyJNI2.rework;

/**
 * The JNIClass is meant to parallel a standard java.lang.Class.
 */
public class JNIClass extends JNINestable {

	/**
	 * Construct a new JNIClass to represent a java.lang.Class
	 * 
	 * @param c The class to represent.
	 */
	public JNIClass(Class<?> c) {
		super(c);
		assert !c.isAnnotation() && !c.isInterface() && !c.isEnum() && !c.isArray() && !c.isPrimitive() : "Attempted to create a JNIClass from a non-class class!";
	}

	@Override
	public JNIClass asClass() { return this; }
}
