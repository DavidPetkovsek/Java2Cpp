package easyJNI2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;

import easyJNI2.lib.StringBuilder2;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class EasyJNI {
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		// https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/jniTOC.html
		// https://github.com/classgraph/classgraph
		// http://journals.ecs.soton.ac.uk/java/tutorial/native1.1/implementing/method.html
		// https://stackoverflow.com/questions/10617735/in-jni-how-do-i-cache-the-class-methodid-and-fieldids-per-ibms-performance-r/13940735
		// https://stackoverflow.com/questions/11225261/how-to-return-enum-from-jni
		
		// Note that this may still find unaccessible functions/variables/etc
		System.out.println("Scanning...");
		HashSet<Class<?>> allClasses = new HashSet<>();
		try (ScanResult scanResult = new ClassGraph().enableSystemJarsAndModules()
//				.disableRuntimeInvisibleAnnotations().enableClassInfo().enableFieldInfo().enableMethodInfo()
				.enableAllInfo()
				.scan()){
			allClasses.addAll(scanResult.getAllClasses().loadClasses());
		}
		
		System.out.println(allClasses.size() + " classes scanned");
		
		for(var c : allClasses)
			CppClass.buildCppClass(c);
			
		System.out.println(CppClass.getClassList().size() + " non-synthetic classes found");

		ArrayList<CppClass> classes = new ArrayList<>(CppClass.getNonNestedClassList());
		System.out.println(classes.size() + " class nests found");
				
		try {
			System.out.println("\nCleaning old directory");
			FileUtils.deleteDirectory(new File("ejni"));
			System.out.println("Cleaning complete");
		} catch(IOException e) {
			System.out.println("\nNo Cleaning required");
		}
		System.out.println("\nGENERATING FILES");
//		toFile(CppClass.get(Test4.class,Test3.class,Test2.class,Test.class,Object.class));
		toFile(classes);
		System.out.println("FILE GENERATION COMPLETE");
		
		

////		System.out.println(CppClass.get(Object.class).getCppClass());
////		System.out.println(CppClass.get(Test4.class).getCppClass());
////		System.out.println(CppClass.get(Test3.class).getCppClass());
////		System.out.println(CppClass.get(Test2.class).getCppClass());
////		System.out.println(CppClass.get(Test.class).getCppClass());
//		printSet(CppClass.get(Test4.class,Test3.class,Test2.class,Test.class));
//		System.out.println(CppClass.get(Test.class).getNestedDependencies().size());
////		System.out.println(CppClass.get(ArrayList.class).getCppClass());
////		printAll(CppClass.get(Test4.class));
		
    }

	static int jk = 0;
	private static void toFile(Collection<CppClass> classes) {

		Date d = new Date();
		classes.parallelStream().forEach(c->{
			new File(c.getIncludeDir()).mkdirs();
			File f = new File(c.getInclude()+".hpp");
			try {
				f.createNewFile();
			}catch(IOException e) {
				System.err.println("Failed to create file: " + f.getName());
				System.exit(0);
			}
			try(BufferedWriter bf = new BufferedWriter(new FileWriter(f))){
				bf.write("#ifndef "+c.getIncludeGuard()+"\n");
				bf.write("#define "+c.getIncludeGuard()+"\n");

				bf.write("\n");
				HashMap<Package,HashSet<CppClass>> map = new HashMap<>();
				for(CppClass cc : c.getDependencies()) {
					if(!map.containsKey(cc.getPackage()))
						map.put(cc.getPackage(), new HashSet<>());
					map.get(cc.getPackage()).add(cc);
				}		
				bf.write(getClassDefs(map).toString());
				
				bf.write("\n");
				bf.write("#include <jni.h>\n");
				bf.write("#include \"easyJNI2.hpp\"\n");
				bf.write("\n");

				
				for(CppClass dep : c.getNestedDependencies(true))
					bf.write("#include \"" +dep.getInclude()+".hpp\"\n");
				bf.write("\n");

				StringBuilder2 sb = new StringBuilder2();
				int tabs = 0;
				String[] spaces = c.getPackage().getName().split("[.]");
				for(String space : spaces)
					sb.append("\t".repeat(tabs++),"namespace ",space," {\n");
				c.getCppClass(true, sb, tabs).append("\n");
				sb.trimR(1);
				tabs--;
				for(;tabs >= 0; tabs--)
					sb.append("\t".repeat(tabs),"}\n");
				
				bf.write(sb.toString());
				bf.write("\n");
				
				for(CppClass dep : c.getDependencies())
					bf.write("#include \"" +dep.getInclude()+".hpp\"\n");
				bf.write("\n");
				
				bf.write("#endif\n");
			} catch(IOException e) {
				System.err.println("Failed to write to header file: " + f.getName());
				System.exit(0);
			}

			f = new File(c.getInclude()+".cpp");
			try(BufferedWriter bf = new BufferedWriter(new FileWriter(f))){
				bf.write("#include \""+c.getInclude()+".hpp\"\n");
				bf.write("\n");
				
				bf.write(c.getCppClass(false));
				bf.write("\n");
			} catch(IOException e) {
				System.err.println("Failed to write to source file: " + f.getName());
				System.exit(0);
			}

			System.out.print("Finished: "+((int)(((double)++jk)/((double)classes.size())*100d))+"% "+ jk +"/"+classes.size()+"    \r");
		});
		System.out.print("Finished: "+((int)(((double)++jk)/((double)classes.size())*100d))+"% "+ jk +"/"+classes.size());
		System.out.println(" completed in "+((double)(new Date().getTime()-d.getTime()))/1000d +" seconds");
	}
	
	
	
	
	private static StringBuilder2 getClassDefs(HashMap<Package,HashSet<CppClass>> toPrint) {
		StringBuilder2 sb = new StringBuilder2();
		sb.append("/* start declarations */\n");
		for(Package p : toPrint.keySet()) {
			int tabs = 0;
			String[] spaces = p.getName().split("[.]");
			for(String space : spaces)
				sb.append("\t".repeat(tabs++),"namespace ",space," {\n");
			for(CppClass c : toPrint.get(p))
				sb.append("\t".repeat(tabs),"class ",c.getSimpleName(),";\n");
			tabs--;
			for(;tabs >= 0; tabs--)
				sb.append("\t".repeat(tabs),"}\n");
		}
		sb.append("/* end declarations */\n");
		return sb;
	}
	
	private static StringBuilder2 getClassesFull(boolean header, HashMap<Package,HashSet<CppClass>> toPrint, HashSet<CppClass>printed) {
		StringBuilder2 sb = new StringBuilder2();
		sb.append("/* start definitions */\n");
		for(Package p : toPrint.keySet()) {
			int tabs = 0;
			String[] spaces = p.getName().split("[.]");
			for(String space : spaces)
				sb.append("\t".repeat(tabs++),"namespace ",space," {\n");
			for(CppClass c : toPrint.get(p))
				c.getCppClass(header, sb, tabs).append("\n");
			sb.trimR(1);
			tabs--;
			for(;tabs >= 0; tabs--)
				sb.append("\t".repeat(tabs),"}\n");
		}
		sb.append("/* end definitions */\n");
		return sb;
	}
	
	private static void printSet(boolean header, Collection<CppClass> cs) {
		cs.retainAll(CppClass.getNonNestedClassList());
		HashMap<Package,HashSet<CppClass>> map = new HashMap<>();
		for(CppClass c : cs) {
			if(!map.containsKey(c.getPackage()))
				map.put(c.getPackage(), new HashSet<>());
			map.get(c.getPackage()).add(c);
		}

		System.out.println(getClassDefs(map));
		System.out.println(getClassesFull(header, map, new HashSet<>()));
	}
	
	private static void printSet(boolean header, CppClass... cs) {
		printSet(header, Arrays.asList(cs));
	}

	private static void printAll(boolean header, CppClass c) {
		printAll(header, c, new HashSet<>());
	}
	
	private static void printAll(boolean header, CppClass c, HashSet<CppClass> printed) {
		printed.add(c);
		for(var cc : c.getDependencies())
			if(!printed.contains(cc))
				printAll(header, cc, printed);
		System.out.println(c.getCppClass(header));
		System.out.println();
	}

}


class Test4 <T>{
	public void meme() {}
	
	public final void THingy(T t) {}
}

class Test3 extends Test4<Integer>{
	public void meme() {}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}

class Test extends Test3 implements Test2{
	int i = 0;
	static int j = 2;
	@Override
	public void meme() {
		// TODO Auto-generated method stub
		super.meme();
	}
	static String idcIDK(int i, String j) {
		return "";
	}
	
	void be(int[][] a, String[] b) {}
	
	void k() {}
	
	@Override
	final public void kk() {}
	
	class meh{}
	
}

interface Test2{
	public void kk();
	default void kd() {
		System.out.println('l');
	}
	static void kek() {}
}