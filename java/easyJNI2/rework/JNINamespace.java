package easyJNI2.rework;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import easyJNI2.lib.StringBuilder2;

public class JNINamespace {

	public final HashSet<JNIType> types = new HashSet<>();
	public final Hashtable<String, JNINamespace> nameSpaces = new Hashtable<>();
	public final String name;
	
	public JNINamespace() { name = null; }
	public JNINamespace(String name) { this.name = name; }
	
	private void add(JNIType t, List<String> namespace) {
		if(namespace == null || namespace.size() <= 0) {
			types.add(t);
			return;
		}
		if(!nameSpaces.containsKey(namespace.get(0)))
			nameSpaces.put(namespace.get(0), new JNINamespace(namespace.get(0)));
		nameSpaces.get(namespace.get(0)).add(t, namespace.subList(1, namespace.size()));
	}
	
	public void add(JNIType t) {
		String[] ns = t.c.getPackageName().split(Pattern.quote("."));
		add(t, Arrays.asList(ns));
	}
	
	public StringBuilder2 buildForwardDecl(StringBuilder2 sb) {
		for(JNIType t : types)
			sb.append("class ", t.c.getSimpleName(), ";").newLine();
		for(String s : nameSpaces.keySet()) {
			sb.append("namespace ", s, "{").newLine(1);
			nameSpaces.get(s).buildForwardDecl(sb);
			sb.trimR(1);
			sb.append("}").newLine(-1);
		}
		return sb;
	 }
}
