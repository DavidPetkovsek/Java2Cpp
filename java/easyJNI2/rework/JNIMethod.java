package easyJNI2.rework;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

import easyJNI2.lib.StringBuilder2;


/**
 * The JNIMethod is meant to parallel a standard java.lang.reflect.Method.
 */
public class JNIMethod extends JNIMember {

	/** The method this object represents */
	protected final Method m;
	/** The method's return type */
	protected final JNIReturnType returnType;
	/** The method's parameters */
	protected final ArrayList<JNIParameter> parameters = new ArrayList<>();
	

	/**
	 * Construct a new JNIMethod to represent a java.lang.reflect.Method
	 * 
	 * @param m The method to represent.
	 */
	public JNIMethod(Method m) {
		this.m = m;
		returnType = new JNIReturnType(m.getReturnType());
		addDependency(returnType.getDependencies());
		for(Parameter p : m.getParameters()) {
			JNIParameter jp = new JNIParameter(p);
			parameters.add(jp);
			addDependency(jp.getDependencies());
		}
	}

	@Override
	public int getModifiers() { return m.getModifiers(); }

	@Override
	public StringBuilder2 buildCppHeader(StringBuilder2 sb) {
		sb.append("virtual ");
		if(isStatic())
			sb.append("static ");
		returnType.buildCppHeader(sb).append(" ");
		sb.append(m.getName(),"(");
		if(!parameters.isEmpty())
			parameters.get(0).buildCppHeader(sb);
		for(int i = 1; i < parameters.size(); ++i)
			sb.append(", ").use(parameters.get(i).buildCppHeader(sb));
		sb.append(")");
		if(isFinal())
			sb.append(" final");
		if(isAbstract())
			sb.append(" = 0");
		sb.append(";");
		return sb;
	 }

}
