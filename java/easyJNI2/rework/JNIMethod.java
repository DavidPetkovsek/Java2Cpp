package easyJNI2.rework;

import java.lang.reflect.Method;
import java.util.List;

public class JNIMethod extends JNIMember{

	private final Method m;
	
	public JNIMethod(Method m) { 	// TODO Auto-generated constructor stub
		this.m = m;
	 }

	@Override
	public List<JNIType> getDependencies() { 
		// TODO Auto-generated method stub
		return null;
	 }

	@Override
	public int getModifiers() { 
		return m.getModifiers();
	 }

}
