package easyJNI2.rework;

import java.lang.reflect.Constructor;
import java.util.List;

public class JNIConstructor extends JNIMember{

	private final Constructor<?> con;
	
	public JNIConstructor(Constructor<?> con) { 	// TODO Auto-generated constructor stub
		this.con = con;
	 }

	@Override
	public List<JNIType> getDependencies() { 
		// TODO Auto-generated method stub
		return null;
	 }

	@Override
	public int getModifiers() { 
		return con.getModifiers();
	 }

}
