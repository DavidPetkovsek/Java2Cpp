package easyJNI2.rework;

import java.util.HashSet;

/**
 * JNINestable is a JNIType that can hold nested Types such as nested classes, enums, or interfaces.
 */
public abstract class JNINestable extends JNIType{
	/** The types that this type declares */
	protected HashSet<JNIType> nestedTypes = new HashSet<>();
	
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
	protected JNINestable(Class<?> c) {
		super(c);	
	}
	
	@Override
	protected void init() {
		super.init();
		
		for(var cc : c.getDeclaredClasses()) {
			JNIType type = EJNI.createJNI(cc);
			if(type != null) {
				nestedTypes.add(type);
				addHardDependency(type.hardDep);
				addSoftDependency(type.softDep);
			}
		}
	 }
}
