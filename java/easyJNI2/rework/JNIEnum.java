package easyJNI2.rework;

import java.util.Arrays;

/**
 * The JNIEnum is meant to parallel a java.lang.Class that represents an underlying enumerated (enum) type.
 */
public class JNIEnum extends JNIType{

	/** The enum constants declared by this enum */
	protected final Enum<?>[] enumConstants;
	
	/**
	 * Construct a new JNIEnum to represent a java.lang.Class that represents an underlying enumerated (enum) type.
	 * 
	 * @param c The enumerated type to represent.
	 */
	public JNIEnum(Class<?> c) {
		super(c);
		assert c.isEnum() : "Attempted to create a JNIEnum from a non-enum class!";
		enumConstants = Arrays.stream(c.getEnumConstants()).map(ec->((Enum<?>)ec)).toArray(Enum<?>[]::new);
	}
}
