package easyJNI2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

import easyJNI2.lib.JNIMethod;
import easyJNI2.lib.JNIType;
import easyJNI2.lib.StringBuilder2;

public class CppClass {
	
	public final static Set<String> ILLEGAL_NAMES = Set.of("or", "and", "negate", "delete", "requires", "register", "UNDERFLOW", "OVERFLOW", "NULL");

	private final static HashMap<Class<?>, CppClass> classes = new HashMap<>();
	private final static HashMap<Class<?>, CppClass> nonNestedClasses = new HashMap<>();
	
	public final Class<?> javaClass;
	public CppClass parentClass; // TODO should not be modifiable and public at the same time
	public HashSet<CppClass> interfaces = new HashSet<>(); // TODO should not be modifiable and public at the same time
	private HashSet<JNIMethod> constructors = new HashSet<>();
	private HashSet<JNIMethod> methods = new HashSet<>();
	private HashSet<JNIMethod> staticMethods = new HashSet<>();
	private HashSet<JNIType> fields = new HashSet<>();
	private HashSet<JNIType> staticFields = new HashSet<>();
	private HashSet<Object> enums = new HashSet<>();
	private HashSet<CppClass> nested = new HashSet<>();
	private LinkedHashSet<CppClass> dependencies = new LinkedHashSet<>();
	private LinkedHashSet<CppClass> hardDependencies = new LinkedHashSet<>();
	private volatile boolean init = false;
	
	private CppClass(Class<?> c) { javaClass = c; }

	// Constructor needs to be delayed and debounced otherwise this will cause an infinite
	// recursive loop
	private synchronized void init() {
		if(init || javaClass == null) return;
		parentClass = buildCppSuperClass(javaClass);
		if(parentClass != null) {
			dependencies.add(parentClass);
			hardDependencies.add(parentClass);
		}
		
		if(javaClass.getDeclaringClass() != null) {
			Class<?> c = javaClass;
 			while(c.getDeclaringClass() != null) {
 				dependencies.add(buildCppClass(c, true));
 				c = c.getDeclaringClass();
 			}
		}
		
		for(Method m_ : javaClass.getDeclaredMethods()) {
			JNIMethod m = new JNIMethod(m_);
			if(!m.isProtected() && !m.isPrivate()) {
				// m is public
				if (m.isBridge() || m.isSynthetic())
					continue;
				else if (m.isStatic())
					staticMethods.add(m);
				else
					methods.add(m);
				
				for(JNIType t : m.getParameters())
					if(t.hasDependencyType())
						dependencies.add(buildCppClass(t));
				if(m.getReturnType().hasDependencyType())
					dependencies.add(buildCppClass(m.getReturnType()));
			}
		}
		
		for(Constructor<?> ctr_ : javaClass.getDeclaredConstructors()) {
			JNIMethod ctr = new JNIMethod(ctr_);
			if(!ctr.isProtected() && !ctr.isPrivate()) {
				// ctr is public
				if (ctr.isSynthetic())
					continue;
				constructors.add(ctr);
				

				for(JNIType t : ctr.getParameters())
					if(t.hasDependencyType())
						dependencies.add(buildCppClass(t));
			}
		}
		
		for(Field f_ : javaClass.getDeclaredFields()) {
			JNIType f = new JNIType(f_);
			if(!f.isProtected() && !f.isPrivate()) {
				// f is public
				if(f.isSynthetic())
					continue;
				else if (f.isStatic())
					staticFields.add(f);
				else
					fields.add(f);
				
				if(f.hasDependencyType())
					dependencies.add(buildCppClass(f));
			}
		}
		
		for(Class<?> i : javaClass.getInterfaces())
			interfaces.add(buildCppClass(i));
		interfaces.remove(null);
		dependencies.addAll(interfaces);
		hardDependencies.addAll(interfaces);
		
		for(Class<?> cc : javaClass.getDeclaredClasses())
			nested.add(buildCppClass(cc));
		nested.remove(null);
		for(CppClass cc : nested)
			dependencies.addAll(cc.dependencies);
		dependencies.remove(null);
		
		if(isEnum())
			enums.addAll(Arrays.asList(javaClass.getEnumConstants()));
	}

	public LinkedHashSet<CppClass> getDependencies(){
		return dependencies;
	}

	private LinkedHashSet<CppClass> getNestedDependencies(boolean hard, LinkedHashSet<CppClass> soFar){
		if(!hard) {
			for(CppClass d : dependencies)
				if(soFar.add(d))
					soFar.addAll(d.getNestedDependencies(hard, soFar));
		} else {
			for(CppClass d : hardDependencies)
				if(soFar.add(d))
					soFar.addAll(d.getNestedDependencies(hard, soFar));
			
		}
		return soFar;
	}
	
	public LinkedHashSet<CppClass> getNestedDependencies(boolean hard){
		return getNestedDependencies(hard, new LinkedHashSet<>());
	}
		
	private StringBuilder2 getCppClassHead(boolean header, StringBuilder2 sb, boolean explicit) {
		if(header) {
			if(!explicit)
				sb.append("class ",javaClass.getSimpleName());
			else
				sb.append("class ",getCppType());
			sb.append(" : public virtual ejni::Object");
			if(parentClass != null)
				sb.append(", public ",parentClass.getCppType());
			for(CppClass i : interfaces)
				sb.append(", public virtual ",i.getCppType());
			sb.append(" {");
			if(isInterface())
				sb.append(" // is interface");
			else if(isEnum())
				sb.append(" // is enum");
			else if(isAbstract())
				sb.append(" // is abstract class");
			sb.append("\n");
		}
		return sb;
	}
	
	public String getCppClass(boolean header) { return getCppClass(header, 0); }
	public StringBuilder2 getCppClass(boolean header, StringBuilder2 sb) { return getCppClass(header, sb, 0); }
	public String getCppClass(boolean header, int tabs) { return getCppClass(header, null, tabs).toString(); }
	public StringBuilder2 getCppClass(boolean header, StringBuilder2 sb, int tabs) {
		if(sb==null) sb = new StringBuilder2();
		getNestedCppClass(header, sb, tabs);
		staticFieldsEnd(header, sb, tabs);
		return sb;
	}
	
	private StringBuilder2 getNestedCppClass(boolean header, StringBuilder2 sb, int tabs) { return getNestedCppClass(header, sb, tabs, false); }
	private StringBuilder2 getNestedCppClass(boolean header, StringBuilder2 sb, int tabs, boolean explicit) {
		if(sb==null) sb = new StringBuilder2();
		sb.append("\t".repeat(tabs)).use(getCppClassHead(header, sb, explicit));
		tabs++;
		
		// TODO enums, nested classes, 
		// private:
		if(header) {
			sb.append("\t".repeat(tabs), "static const char *className;\n");
			sb.append("\t".repeat(tabs), "static jclass clazz;\n");


			sb.append("\t".repeat(tabs-1),"public:\n");
			fields(header, sb, tabs);
			sb.append("\n\n");
			staticFields(header, sb, tabs);
		
			sb.append("\t".repeat(tabs-1),"protected:\n");
			sb.append("\t".repeat(tabs));
		}
		if(header) {
			if(isInterface()) {
				sb.append(javaClass.getSimpleName(),"();\n");
			} else {
				sb.append("explicit ",javaClass.getSimpleName());
				sb.append("(void *n, void *m);\n");
			}
		} else {
			if(isInterface()) {
				sb.append(getCppType(),"::",javaClass.getSimpleName(),"() {}\n");
			} else {
				sb.append("explicit ",getCppType(),"::",javaClass.getSimpleName());
				if(parentClass != null)
					sb.append("(void *n, void *m) : ").append(parentClass.getCppType()).append("(nullptr, nullptr) {\n");
				else
					sb.append("(void *n, void *m) : ejni::Object(nullptr, nullptr) {\n");
				initFields(header, sb, tabs+1);
				sb.append("\t".repeat(tabs),"}\n");
			}
		}
		
		if(header)
			sb.append("\t".repeat(tabs-1),"public:\n");

		constructors(header, sb, tabs, (x,y)->initFields(header, x,y));
		sb.append("\n\n");
		instanceMethods(header, sb, tabs);
		sb.append("\n\n");
		staticMethods(header, sb, tabs);
		sb.append("\n\n");
		if(header) {
			sb.append("\t".repeat(tabs), "virtual jobject operator*() const;\n");
		} else {
			sb.append("\t".repeat(tabs), "virtual jobject ",getCppType(),"::operator*() const { return *obj; }\n");
		}
		if(header) {
			sb.append("\t".repeat(tabs),"virtual ~",javaClass.getSimpleName(),"();\n");
		} else {
			sb.append("\t".repeat(tabs),"virtual ~",getCppType(),"::",javaClass.getSimpleName(),"(){}\n");
		}
		
		sb.append("\n\n");
		LinkedHashSet<CppClass> nestOrder = new LinkedHashSet<>();
		for(CppClass cp : nested)
			nestOrder.addAll(cp.getNestedDependencies(true));
		nestOrder.retainAll(nested);
		nestOrder.addAll(nested);
		if(header)
			for(CppClass cp : nestOrder)
				sb.append("\t".repeat(tabs), "class ", cp.getSimpleName(),";\n");
		
		tabs--;
		sb.append("\t".repeat(tabs),"};\n");
		
		nestedClasses(header, sb, tabs, nestOrder);
		
		return sb;
	}
	
	private StringBuilder2 nestedClasses(boolean header, StringBuilder2 sb, int tabs, LinkedHashSet<CppClass> nestOrder) {
		sb.append("\t".repeat(tabs), "/* start nested classes */\n");
		for(CppClass cp : nestOrder)
			cp.getNestedCppClass(header, sb, tabs, true);
		sb.append("\t".repeat(tabs), "/* end nested classes */\n");
		return sb;
	}
	
	private StringBuilder2 initFields(boolean header, StringBuilder2 sb, int tabs) {
		if(!header) {
			for(JNIType f : fields) {
				sb.append("\t".repeat(tabs));
				f.getCppFieldInit(sb);
			}
		}
		return sb;
	}
	
	private void fields(boolean header, StringBuilder2 sb, int tabs) {
		if(header) {
			sb.append("\t".repeat(tabs), "/* start fields */\n");
			for(JNIType f : fields) {
				sb.append("\t".repeat(tabs));
				f.getCppField(sb);
			}
			sb.append("\t".repeat(tabs), "/* end fields */\n");
		}
	}
	
	private void staticFields(boolean header, StringBuilder2 sb, int tabs) {
		if(header) {
			sb.append("\t".repeat(tabs), "/* start static fields */\n");
			for(JNIType f : staticFields) {
				sb.append("\t".repeat(tabs));
				f.getCppField(sb);
			}
			sb.append("\t".repeat(tabs), "/* end static fields */\n");
		}
	}
	
	private void staticFieldsEnd(boolean header, StringBuilder2 sb, int tabs) {
		if(!header) {
			for(CppClass c : nested)
				c.staticFieldsEnd(header, sb, tabs);
			sb.append("\t".repeat(tabs), "const char *",getCppType(),"::className = \"",getName(this.javaClass),"\";\n");
			sb.append("\t".repeat(tabs), "jclass ",getCppType(),"::clazz = ejni::FindClass(env, className);\n");
			for(JNIType f : staticFields) {
				sb.append("\t".repeat(tabs));
				f.getCppStaticFieldInit(sb, this);
			}
		}
	}

	private void constructors(boolean header, StringBuilder2 sb, int tabs, BiConsumer<StringBuilder2, Integer> fieldInit) {
		sb.append("\t".repeat(tabs), "/* start constructors */\n");
		
		if(header) {
			sb.append("\t".repeat(tabs), "explicit ", javaClass.getSimpleName());
			sb.append("(jobject obj);\n");
			
			for(JNIMethod ctr : constructors) 
				ctr.getCppConstructor(header, sb, this, parentClass, tabs, fieldInit);
			sb.append("\t".repeat(tabs), "/* end constructors */\n");
		} else {
			sb.append("\t".repeat(tabs), "explicit ", getCppType(),"::",javaClass.getSimpleName());
			if(parentClass != null)
				sb.append("(jobject obj) : ", parentClass.getCppType(), "(obj) {\n");
			else
				sb.append("(jobject obj) : ejni::Object(obj) {\n");
			fieldInit.accept(sb, tabs+1);
			sb.append("\t".repeat(tabs), "}\n");
			
			for(JNIMethod ctr : constructors) 
				ctr.getCppConstructor(header, sb, this, parentClass, tabs, fieldInit);
			sb.append("\t".repeat(tabs), "/* end constructors */\n");
		}
	}
	
	private void instanceMethods(boolean header, StringBuilder2 sb, int tabs) {
		sb.append("\t".repeat(tabs), "/* start instance functions */\n");
		for(JNIMethod m : methods) 
			m.getCppMethod(header, sb, this, isOverriden(m), tabs);
		sb.append("\t".repeat(tabs), "/* end instance functions */\n");
	}

	private void staticMethods(boolean header, StringBuilder2 sb, int tabs) {
		sb.append("\t".repeat(tabs), "/* start static functions */\n");
		for(JNIMethod m : staticMethods) 
			m.getCppMethod(header, sb, this, false, tabs);
		sb.append("\t".repeat(tabs), "/* end static functions */\n");
	}
	
	private boolean contains(JNIMethod m) {
		if(m.isStatic())
			throw new NullPointerException("called contains with static method");
		for(JNIMethod m2 : methods)
			if(m.softMatch(m2))
				return true;
		return false;
	}
	
	private boolean isOverriden(JNIMethod m) {
		if(m.isStatic())
			return false;
		if(parentClass != null && parentClass.contains(m))
			return true;
		for(var v : interfaces)
			if(v.contains(m))
				return true;
		return false;
	}
	
	public String getCppType() {
		return javaClass.getTypeName().replaceAll("[.$]", "::");
	}
	
	
	public boolean isEnum() { return javaClass == null ? false : javaClass.isEnum(); }
	public boolean isInterface() { return javaClass == null ? false : javaClass.isInterface(); }
	public boolean isAbstract() { return javaClass == null ? false : Modifier.isAbstract(javaClass.getModifiers()); }
	
	private static CppClass buildCppSuperClass(Class<?> c) {
		if(c == null) return null;
		return buildCppClass(c.getSuperclass());
	}
	
	public static CppClass buildCppClass(Class<?> c) {
		return buildCppClass(c, false);
	}
	
	private CppClass buildCppClass(JNIType t) {
		return buildCppClass(t.getDependencyType());
	}
	
	public static CppClass buildCppClass(Class<?> c, boolean force) {
		if(!force && (c == null || c.isArray() || c.isSynthetic() || c.isPrimitive() || c.isAnnotation() || c.getCanonicalName() == null || Modifier.isProtected(c.getModifiers()) || Modifier.isPrivate(c.getModifiers())))
			return null;
		else if(classes.containsKey(c))
			return classes.get(c);
		CppClass cc = new CppClass(c);
		classes.put(c, cc);
		if(c.getEnclosingClass() == null && c.getDeclaringClass() == null && !c.isMemberClass() && !c.isLocalClass() && !c.toString().contains("$"))
			nonNestedClasses.put(c, cc);
		cc.init();
		return cc;
	}

	public static Collection<CppClass> getClassList() { return classes.values(); }
	public static Collection<CppClass> getNonNestedClassList() { return nonNestedClasses.values(); }
	
	public boolean isEmpty() { return (parentClass == null || parentClass.isEmpty()) && methods.isEmpty() && staticMethods.isEmpty() && fields.isEmpty() && staticFields.isEmpty(); }
	
	@Override
	public int hashCode() { return javaClass.hashCode(); }
	
	@Override
	public boolean equals(Object obj) { return javaClass.equals(obj); }
	
	@Override
	public String toString() { return javaClass.toString(); }
	
	public static String getName(Class<?> c) {
		return c.getTypeName().replaceAll("[.]", "/");
	}


	public static CppClass get(Class<?> class1) {
		return classes.get(class1);
	}
	
	public static ArrayList<CppClass> get(Class<?> class1, Class<?>... class2) {
		ArrayList<CppClass> cp = new ArrayList<>();
		cp.add(classes.get(class1));
		for(Class<?> c : class2)
			cp.add(classes.get(c));
		return cp;
	}

	public String getSimpleName() {
		return javaClass.getSimpleName().replaceAll(".*\\$", "");
	}
	
	public Package getPackage() {
		return javaClass.getPackage();
	}
	
	public String getIncludeGuard() {
		return "__ejni_"+getPackage().getName().replaceAll("[.]", "_")+"_"+getSimpleName()+"__";
	}
	public String getInclude() {
		if(javaClass.getDeclaringClass() != null)
			return CppClass.buildCppClass(javaClass.getDeclaringClass(), true).getInclude();
		return "ejni/"+getPackage().getName().replaceAll("[.]", "/")+"/"+getSimpleName();
	}
	public String getIncludeDir() {
		if(javaClass.getDeclaringClass() != null)
			return CppClass.get(javaClass.getDeclaringClass()).getIncludeDir();
		return "ejni/"+getPackage().getName().replaceAll("[.]", "/");
	}
}
