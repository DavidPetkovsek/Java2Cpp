package easyJNI2.rework;

import java.util.List;

/**
 * A JNIMember is a member of a JNIType. Methods, Fields, Constructors, etc.
 */
public abstract class JNIMember implements JNIBase{
	
	/**
	 * @return The JNIType to use as a dependency, if the type is primitive or void then null
	 */
	public abstract List<JNIType> getDependencies();

}
