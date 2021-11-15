package easyJNI2.rework;

import java.lang.reflect.Field;
import java.util.List;

public class JNIField extends JNIMember{

	private final Field f;
	
	public JNIField(Field f) { 	// TODO Auto-generated constructor stub
		this.f = f;
	 }
	
	

	@Override
	public List<JNIType> getDependencies() { 
		// TODO Auto-generated method stub
		return null;
	 }



	@Override
	public int getModifiers() { 
		return f.getModifiers();
	 }

}
