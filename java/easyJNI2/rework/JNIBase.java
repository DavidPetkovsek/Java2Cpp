package easyJNI2.rework;

import java.lang.reflect.Modifier;

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
}
