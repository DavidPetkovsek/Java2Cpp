package easyJNI2.rework;

/**
 * The JNIClass is meant to parallel a standard java.lang.Class.
 */
public class JNIClass extends JNINestable {

	
	
	
	
	public JNIClass(Class<?> c) {
		super(c);
	}
	
	/**
	 * A test main
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		new JNIClass(String.class);
	}


	@Override
	public JNIClass asClass() { return this; }
}
