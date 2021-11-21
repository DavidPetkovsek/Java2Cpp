package easyJNI2.rework;

import java.util.Collection;
import java.util.HashSet;


/**
 * A JNIMember is a member of a JNIType. Methods, Fields, Constructors, etc.
 */
public abstract class JNIMember implements JNIBase {
	/**
	 * These are the required types to exist for the member to be valid.
	 * 
	 * <p>
	 * Compared to a JNIType these are soft dependencies. Dependencies that require only a C++ class declaration. For the most part.
	 */
	protected final HashSet<JNIType> dep = new HashSet<>();

	/**
	 * @return The JNIType to use as a dependency, if the type is primitive or void then null
	 */
	public HashSet<JNIType> getDependencies(){ return dep; }

	/**
	 * Checks if a JNIType is blacklisted to be a dependency. Meaning it should not be included as a dependency.
	 * 
	 * @param dep the JNIType to check
	 * @return true if the JNIType is blacklisted, else false
	 */
	protected boolean isDependencyBlackListed(JNIType dep) {
		// we do not include null
		return dep == null;
	}
	
	/**
	 * Adds a collection of dependencies.
	 * 
	 * @param dep The dependencies to add
	 * @return true if any dependency didn't already exist as a dependency.
	 */
	protected boolean addDependency(Collection<JNIType> dep) {
		boolean b = false;
		for(JNIType t : dep) b = b || addDependency(t);
		return b;
	}
	
	/**
	 * Adds a dependency.
	 * 
	 * @param dep The dependency to add
	 * @return true if the dependency didn't already exist as a dependency.
	 */
	protected boolean addDependency(JNIType dep) {
		if(isDependencyBlackListed(dep)) return false; // handles dep == null as well
		if(dep.isNested()) return addDependency(dep.getTopLevelType());
		return this.dep.add(dep);
	}
}
