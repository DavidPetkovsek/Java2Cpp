package easyJNI2.rework;

public class JNIInterface extends JNINestable {


	public JNIInterface(Class<?> c) {
		super(c);
	}

	@Override
	public JNIInterface asInterface() { return this; }

}
