package easyJNI2.rework;

import java.util.Hashtable;

/**
 * Main static functions are here.
 */
public class EJNI {

	/** All created JNIClass objects */
	private static Hashtable<Class<?>, JNIClass> classes = new Hashtable<>();
	/** All created JNIInterface objects */
	private static Hashtable<Class<?>, JNIInterface> interfaces = new Hashtable<>();
	/** All created JNIEnum objects */
	private static Hashtable<Class<?>, JNIEnum> enums = new Hashtable<>();

	/**
	 * Creates the appropriate JNIType of the given class if it does not exist. If it does exist return the existing
	 * instance. If the given type is invalid null is returned.
	 * 
	 * @param  c The java.lang.Class to convert
	 * @return A JNIType or null if (invalid or input is null)
	 */
	public static JNIType createJNI(Class<?> c) {
		if(c.isSynthetic()) System.out.println("Warning: Using synthetic class " + c.getTypeName());
		if(c.isAnnotation() || c.isArray() || c.isPrimitive()) { return null; }

		if(c.isInterface()) return createJNIInterface(c);
		else if(c.isEnum()) return createJNIEnum(c);
		else return createJNIClass(c);
	}

	/**
	 * Creates the appropriate JNINestable of the given class if it does not exist. If it does exist return the existing
	 * instance. If the given type is invalid an assertion will cause the function to fail.
	 * 
	 * @param  c The java.lang.Class to convert
	 * @return A JNINestable
	 */
	public static JNINestable createJNINestable(Class<?> c) {
		assert !c.isEnum() : "Attempted to create nestable enum";
		assert !c.isAnnotation() && !c.isArray() && !c.isPrimitive() : "Attempted to create a nestable from an invalid type";
		if(c.isSynthetic()) System.out.println("Warning: Using synthetic class " + c.getTypeName());

		if(c.isInterface()) return createJNIInterface(c);
		else return createJNIClass(c);
	 }

	/**
	 * Creates a JNIClass of the given class if it does not exist. If it does exist return the existing
	 * instance. If the given type is invalid an assertion will cause the function to fail.
	 * 
	 * @param  c The java.lang.Class to convert
	 * @return A JNIClass
	 */
	public static JNIClass createJNIClass(Class<?> c) {
		assert !c.isAnnotation() && !c.isInterface() && !c.isEnum() && !c.isArray() && !c.isPrimitive() : "Attempted to create a JNIClass from a non-class class!";
		if(c.isSynthetic()) System.out.println("Warning: Using synthetic class " + c.getTypeName());
		
		if(classes.contains(c))
			return classes.get(c);
		JNIClass jc = new JNIClass(c);
		classes.put(c, jc);
		jc.init();
		return jc;
	 }

	/**
	 * Creates a JNIInterface of the given class if it does not exist. If it does exist return the existing
	 * instance. If the given type is invalid an assertion will cause the function to fail.
	 * 
	 * @param  c The java.lang.Class to convert
	 * @return A JNIInterface
	 */
	public static JNIInterface createJNIInterface(Class<?> c) { 
		assert c.isInterface() : "Attempted to create an interface from a non-interface class!";
		if(c.isSynthetic()) System.out.println("Warning: Using synthetic class " + c.getTypeName());
		
		if(interfaces.contains(c))
			return interfaces.get(c);
		JNIInterface jc = new JNIInterface(c);
		interfaces.put(c, jc);
		jc.init();
		return jc;
	 }
	
	/**
	 * Creates a JNIEnum of the given class if it does not exist. If it does exist return the existing
	 * instance. If the given type is invalid an assertion will cause the function to fail.
	 * 
	 * @param  c The java.lang.Class to convert
	 * @return A JNIEnum
	 */
	public static JNIEnum createJNIEnum(Class<?> c) { 
		assert c.isEnum() : "Attempted to create an enum from a non-enum class!";
		if(c.isSynthetic()) System.out.println("Warning: Using synthetic class " + c.getTypeName());
		
		if(enums.contains(c))
			return enums.get(c);
		JNIEnum jc = new JNIEnum(c);
		enums.put(c, jc);
		jc.init();
		return jc;
	 }
}
