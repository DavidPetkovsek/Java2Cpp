package easyJNI2.rework;

import java.util.Collection;
import java.util.HashSet;

/**
 * JNIType is meant to parallel a standard java.lang.Class. It is a high level type you would use in code.
 */
public abstract class JNIType implements JNIBase{
	/** The class this object represents */
	protected final Class<?> c;
	/** Parent class, if no parent then null */
	protected final JNIClass parentClass;
	/** If this class is a nested class, the following is the declaring class, else null */
	protected final JNINestable declaringClass;
	/** Parent interfaces */
	protected final HashSet<JNIInterface> parentInterfaces = new HashSet<>();
	/**
	 * Dependencies that require a full C++ class definition is required.
	 * 
	 * <p>
	 * 1. C++ Parent class (Java inherited classes & Java implemented interfaces).
	 * 
	 * 2. C++ Parent class of a nested class.
	 * 
	 * 3. If any dependency is a nested. The containing class is promoted to a hard dependency.
	 */
	protected final HashSet<JNIType> hardDep = new HashSet<>();
	/**
	 * Dependencies that require only a C++ class declaration.
	 * 
	 * <p>
	 * The class is used as a return type, parameter, or variable.
	 * 
	 * Soft dependencies can be promoted to hard dependencies. See JNIClass.hardDep.
	 */
	protected final HashSet<JNIType> softDep = new HashSet<>();
	/** All fields the type contains */
	protected final HashSet<JNIField> fields = new HashSet<>();
	/** All constructors the type contains */
	protected final HashSet<JNIConstructor> constructors = new HashSet<>();
	/** All methods the type contains */
	protected final HashSet<JNIMethod> methods = new HashSet<>();
	
	/**
	 * Construct a new JNIType to represent a java.lang.Class
	 * 
	 * @param c Class to represent.
	 */
	public JNIType(Class<?> c) {
		this.c = c;
		if(c.getDeclaringClass() != null)
			declaringClass = EJNI.createJNINestable(c.getDeclaringClass());
		else declaringClass = null;
		if(c.getSuperclass() != null) {
			parentClass = EJNI.createJNIClass(c.getSuperclass()); 
			addHardDependency(parentClass);
		}else parentClass = null;
		for(var i : c.getInterfaces()) {
			JNIInterface j = EJNI.createJNIInterface(i);
			parentInterfaces.add(j);
			addHardDependency(j);
		}
		
		for(var v : c.getDeclaredFields()) {
			JNIField jv = new JNIField(v);
			if(!jv.isPrivate()) {
				fields.add(jv);
				addSoftDependency(jv.getDependencies());
			}
		}
		
		for(var con : c.getDeclaredConstructors()) {
			JNIConstructor jcon = new JNIConstructor(con);
			if(!jcon.isPrivate()) {
				constructors.add(jcon);
				addSoftDependency(jcon.getDependencies());
			}
		}
		
		for(var m : c.getDeclaredMethods()) {
			JNIMethod jm = new JNIMethod(m);
			if(!jm.isPrivate()) {
				methods.add(jm);
				addSoftDependency(jm.getDependencies());
			}
		}
	}
	
	
	/**
	 * Checks if a JNIType is blacklisted to be a dependency. Meaning it should not be included as a dependency.
	 * 
	 * @param dep the JNIType to check
	 * @return true if the JNIType is blacklisted, else false
	 */
	protected boolean isPrivacyBlackListed(JNIType dep) {
		// we do not include null
		// we do not include or declare the declaring type of this nested type
		return dep == null || dep.equals(declaringClass);
	}
	
	/**
	 * Adds a collection of hard dependencies.
	 * 
	 * @param dep The dependencies to add
	 * @return true if any dependency didn't already exist as a hard dependency.
	 */
	protected boolean addHardDependency(Collection<JNIType> dep) {
		boolean b = false;
		for(JNIType t : dep) b = b || addHardDependency(t);
		return b;
	}
	
	/**
	 * Adds a collection of soft dependencies.
	 * 
	 * @param dep The dependencies to add
	 * @return true if any dependency didn't already exist.
	 */
	protected boolean addSoftDependency(Collection<JNIType> dep) {
		boolean b = false;
		for(JNIType t : dep) b = b || addSoftDependency(t);
		return b;
	}
	
	/**
	 * Adds a hard dependency.
	 * 
	 * @param dep The dependency to add
	 * @return true if the dependency didn't already exist as a hard dependency.
	 */
	protected boolean addHardDependency(JNIType dep) {
		if(isPrivacyBlackListed(dep)) return false; // handles dep == null as well
		if(dep.isNested()) return addHardDependency(dep.getTopLevelType());
		boolean b = hardDep.add(dep);
		softDep.remove(dep);
		return b;
	}
	
	/**
	 * Adds a soft dependency.
	 * 
	 * @param dep The dependency to add
	 * @return true if the dependency didn't already exist.
	 */
	protected boolean addSoftDependency(JNIType dep) {
		if(isPrivacyBlackListed(dep)) return false; // handles dep == null as well
		if(dep.isNested()) return addHardDependency(dep.getTopLevelType());
		if(!hardDep.contains(dep))
			return softDep.add(dep);
		return false;
	}
	
	/**
	 * @return true if this type is nested in another, false otherwise
	 */
	public boolean isNested() {
		return declaringClass != null;
	}
	
	/**
	 * @return the top level type that is not nested
	 */
	public JNIType getTopLevelType() {
		Class<?> c = this.c;
		while(true) {
			if(c.getDeclaringClass() != null) {
				c = c.getDeclaringClass();
			}else break;
		}
		return EJNI.createJNI(c);
	}
	
	@Override
	public boolean equals(Object obj) { 
		return c.equals(obj);
	 }
	
	@Override
	public int hashCode() { 
		return c.hashCode();
	 }
	
	@Override
	public int getModifiers() { 
		return c.getModifiers();
	 }

}
