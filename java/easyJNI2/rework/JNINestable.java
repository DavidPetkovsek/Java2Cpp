package easyJNI2.rework;

import java.util.HashSet;

/**
 * JNINestable is a JNIType that can hold nested Types such as nested classes, enums, or interfaces.
 */
public abstract class JNINestable extends JNIType{
	protected final HashSet<JNIClass> nestedClasses = new HashSet<>();
	protected final HashSet<JNIEnum> nestedEnums = new HashSet<>();
	protected final HashSet<JNIInterface> nestedInterfaces = new HashSet<>();
	
	/**
	 * Casts this class to a JNIClass.
	 * 
	 * @return the JNIClass or null if not a JNIClass
	 */
	public JNIClass asClass() { return null; }
	
	/**
	 * Casts this class to a JNIInterface.
	 * 
	 * @return the JNIInterface or null if not a JNIInterface
	 */
	public JNIInterface asInterface() { return null; }
	
	public JNINestable(Class<?> c) {
		super(c);
	}
}
