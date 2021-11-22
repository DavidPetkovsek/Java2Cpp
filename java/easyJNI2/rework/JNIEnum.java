package easyJNI2.rework;

import java.util.Arrays;

import easyJNI2.lib.StringBuilder2;

/**
 * The JNIEnum is meant to parallel a java.lang.Class that represents an underlying enumerated (enum) type.
 */
public class JNIEnum extends JNIType{

	/** The enum constants declared by this enum */
	protected Enum<?>[] enumConstants;
	
	/**
	 * Construct a new JNIEnum to represent a java.lang.Class that represents an underlying enumerated (enum) type.
	 * 
	 * @param c The enumerated type to represent.
	 */
	protected JNIEnum(Class<?> c) {
		super(c);
		assert c.isEnum() : "Attempted to create a JNIEnum from a non-enum class!";
		enumConstants = Arrays.stream(c.getEnumConstants()).map(ec->((Enum<?>)ec)).toArray(Enum<?>[]::new);
	}

	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) { 
		// TODO Auto-generated method stub
		// for now just create the different constants without the extra functions or includes or other things
		if(isStatic())
			sb.append("static ");
		sb.append("enum ", c.getSimpleName(), "{").newLine(1);
		if(enumConstants.length > 0)
			sb.append(enumConstants[0].name());
		for(int i = 1; i < enumConstants.length; ++i)
			sb.append(", ", enumConstants[i].name());
		if(enumConstants.length > 0)
			sb.append(";");
		sb.newLine(-1);
		sb.append("};");
		return sb;
	 }
}
