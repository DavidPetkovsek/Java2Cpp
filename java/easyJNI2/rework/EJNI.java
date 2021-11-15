package easyJNI2.rework;

/**
 * Main static functions are here.
 */
public class EJNI {

	/**
	 * Creates the appropriate JNIType of the given class if it does not exist. If it does exist return the existing
	 * instance. If the given type is invalid null is returned.
	 * 
	 * @param  c The java.lang.Class to convert
	 * @return A JNIType or null if (invalid or input is null)
	 */
	public static JNIType createJNI(Class<?> c) { return null; }

	public static JNINestable createJNINestable(Class<?> declaringClass) { 
		// TODO Auto-generated method stub
		return null;
	 }

	public static JNIClass createJNIClass(Class<?> superclass) { 
		// TODO Auto-generated method stub
		return null;
	 }

	public static JNIInterface createJNIInterface(Class<?> i) { 
		// TODO Auto-generated method stub
		return null;
	 }
	
	public static JNIEnum createJNIEnum(Class<?> e) { 
		// TODO Auto-generated method stub
		return null;
	 }
}
