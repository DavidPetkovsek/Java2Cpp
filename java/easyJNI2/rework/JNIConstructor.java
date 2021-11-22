package easyJNI2.rework;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import easyJNI2.lib.StringBuilder2;

/**
 * The JNIConstructor is meant to parallel a standard java.lang.reflect.Constructor.
 */
public class JNIConstructor extends JNIMember{

	/** The constructor this object represents */
	private final Constructor<?> con;
	/** The constructor's parameters */
	protected final ArrayList<JNIParameter> parameters = new ArrayList<>();
	
	/**
	 * Construct a new JNIConstructor to represent a java.lang.reflect.Constructor
	 * 
	 * @param con The constructor to represent.
	 */
	public JNIConstructor(Constructor<?> con) {
		this.con = con;
		for(Parameter p : con.getParameters()) {
			JNIParameter jp = new JNIParameter(p);
			parameters.add(jp);
			addDependency(jp.getDependencies());
		}
	 }


	@Override
	public int getModifiers() { 
		return con.getModifiers();
	 }


	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) {
		sb.append(con.getDeclaringClass().getSimpleName(),"(");
		if(!parameters.isEmpty())
			parameters.get(0).buildCppHeader(sb);
		for(int i = 1; i < parameters.size(); ++i)
			sb.append(", ").use(parameters.get(i).buildCppHeader(sb));
		sb.append(");");
		return sb;
	 }

}
