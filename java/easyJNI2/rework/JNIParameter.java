package easyJNI2.rework;

import java.lang.reflect.Parameter;

/**
 * The JNIParameter is meant to parallel a standard java.lang.reflect.Parameter.
 */
public class JNIParameter extends JNIMember{

	/** The parameter this object represents */
	protected final Parameter p;
	
	/**
	 * Construct a new JNIParameter to represent a java.lang.reflect.Parameter
	 * 
	 * @param p The parameter to represent.
	 */
	public JNIParameter(Parameter p) {
		this.p = p;
		addDependency(EJNI.createJNI(p.getType()));
	 }

	@Override
	public int getModifiers() { 
		return p.getModifiers();
	 }

}
