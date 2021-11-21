package easyJNI2.rework;

/**
 * The JNIInterface is meant to parallel a java.lang.Class that represents an underlying interface.
 */
public class JNIInterface extends JNINestable {

	/**
	 * Construct a new JNIInterface to represent a java.lang.Class that represents an interface.
	 * 
	 * @param c The interface type to represent.
	 */
	public JNIInterface(Class<?> c) {
		super(c);
		assert c.isInterface() : "Attempted to create a JNIInterface from a non-interface class!";
	}

	@Override
	public JNIInterface asInterface() { return this; }

}
