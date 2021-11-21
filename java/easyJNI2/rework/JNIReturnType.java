package easyJNI2.rework;

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
		addDependency(EJNI.createJNI(c));
	 }

	@Override
	public int getModifiers() { 
		return 0; // no modifiers
	 }

}
