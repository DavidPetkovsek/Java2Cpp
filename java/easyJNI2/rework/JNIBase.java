package easyJNI2.rework;

import java.lang.reflect.Modifier;

import easyJNI2.lib.StringBuilder2;

/**
 * Contains a number of basic functions common among both JNIMembers and JNITypes
 */
public interface JNIBase {
	
	/**
	 * Gets the modifiers
	 * 
	 * @return the integer representation of the modifiers
	 */
	public int getModifiers();
	
	/**
	 * @return true if this is private, false otherwise
	 */
	public default boolean isPrivate() { 
		return Modifier.isPrivate(getModifiers());
	 }

	/**
	 * @return true if this is protected, false otherwise
	 */
	public default boolean isProtected() { 
		return Modifier.isProtected(getModifiers());
	 }

	/**
	 * @return true if this is public, false otherwise
	 */
	public default boolean isPublic() { 
		return Modifier.isPublic(getModifiers());
	 }

	/**
	 * @return true if this is static, false otherwise
	 */
	public default boolean isStatic() { 
		return Modifier.isStatic(getModifiers());
	 }

	/**
	 * @return true if this is final, false otherwise
	 */
	public default boolean isFinal() { 
		return Modifier.isFinal(getModifiers());
	 }

	/**
	 * @return true if this is final, false otherwise
	 */
	public default boolean isAbstract() { 
		return Modifier.isAbstract(getModifiers());
	 }
	
	/**
	 * Create a string with the c++ implementation of this JNI object.
	 * 
	 * @param sb The string builder to use
	 * @return The string builder passed in (sb)
	 */
	public StringBuilder2 buildCppHeader(final StringBuilder2 sb);
}
