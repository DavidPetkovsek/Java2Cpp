package easyJNI2.rework;

import java.util.HashSet;

/**
 * JNINestable is a JNIType that can hold nested Types such as nested classes, enums, or interfaces.
 */
public abstract class JNINestable extends JNIType{
	/** The types that this type declares */
	protected final HashSet<JNIType> nestedTypes = new HashSet<>();
	
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
	
	/**
	 * Construct a new JNINestable to represent a java.lang.Class
	 * 
	 * @param c Class to represent.
	 */
	public JNINestable(Class<?> c) {
		super(c);
		
		for(var cc : c.getDeclaredClasses()) {
			JNIType type = EJNI.createJNI(cc);
			nestedTypes.add(type);
			addHardDependency(type.hardDep);
			addSoftDependency(type.softDep);
		}
	}
}
