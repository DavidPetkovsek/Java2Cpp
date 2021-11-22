package easyJNI2.lib;

import java.util.List;

public class StringBuilder2 implements CharSequence{

	private int tabs = 0;
	private StringBuilder sb;

	public StringBuilder2() { sb = new StringBuilder(); }
	public StringBuilder2(CharSequence cs) { sb = new StringBuilder(cs); }
	public StringBuilder2(String s) { sb = new StringBuilder(s); }
	public StringBuilder2(StringBuilder sb) { this.sb = new StringBuilder(sb); }
	public StringBuilder2(StringBuilder2 sb) { this.sb = new StringBuilder(sb.sb); tabs = sb.tabs; }

	public StringBuilder2 setTabs(int tabs) {
		this.tabs = tabs;
		return this;
	}
	
	public int addTab() {
		return ++tabs;
	}
	
	public int removeTab() {
		if(tabs <= 0) {
			tabs = 0;
			return tabs;
		}
		return --tabs;
	}
	
	public StringBuilder2 appendTabs() {
		sb.append("\t".repeat(tabs));
		return this;
	}
	
	public int getTabs() { return tabs; }

	public StringBuilder2 newLine() {
		append("\n").appendTabs();
		return this;
	}
	
	public StringBuilder2 newLine(int change) {
		tabs += change;
		append("\n").appendTabs();
		return this;
	}
	
	public StringBuilder2 append(boolean b) {
		sb.append(b);
		return this;
	}
	
	public StringBuilder2 append(char c) {
		sb.append(c);
		return this;
	}
	
	public StringBuilder2 append(char[] str) {
		sb.append(str);
		return this;
	}
	
	public StringBuilder2 append(CharSequence s) {
		sb.append(s);
		return this;
	}
	
	public StringBuilder2 append(double d) {
		sb.append(d);
		return this;
	}
	
	public StringBuilder2 append(float f) {
		sb.append(f);
		return this;
	}
	
	public StringBuilder2 append(int i) {
		sb.append(i);
		return this;
	}
	
	public StringBuilder2 append(long lng) {
		sb.append(lng);
		return this;
	}
	
	public StringBuilder2 append(StringBuffer sb) {
		sb.append(sb);
		return this;
	}
	
	public StringBuilder2 append(char[] str, int offset, int len) {
		sb.append(str, offset, len);
		return this;
	}
	
	public StringBuilder2 append(CharSequence s, int start, int end) {
		sb.append(s, start, end);
		return this;
	}
	
	public StringBuilder2 append(StringBuilder2 sb) {
		this.sb.append(sb.sb);
		return this;
	}
	
	public StringBuilder2 append(String s, String... ls) {
		sb.append(s);
		for(String ss : ls)
			sb.append(ss);
		return this;
	}
	
	public StringBuilder2 append(Object o, Object... os) {
		sb.append(o);
		for(Object oo : os) {
			if(oo instanceof String)
				sb.append((String)oo);
			else if(oo instanceof CharSequence)
				sb.append((CharSequence)oo);
			else
				sb.append(oo);
		}
		return this;
	}
	
	public StringBuilder2 use(StringBuilder2 sb, StringBuilder2... sbs) {
		assert sb == this;
		for(StringBuilder2 sbbs : sbs)
			assert sbbs == this;
		return this;
	}
	
	public StringBuilder2 join(String div, int group, String s, String... ls) {
		sb.append(s);
		int i = 0;
		for(String ss : ls) {
			if(i++ == group-1) {
				i = 0;
				sb.append(div);
			}
			sb.append(ss);
		}
		return this;
	}
	
	public StringBuilder2 join(String div, int group, Object o, Object... os) {
		sb.append(o);
		int i = 0;
		for(Object oo : os) {
			if(i++ == group-1) {
				i = 0;
				sb.append(div);
			}
			if(oo instanceof String)
				sb.append((String)oo);
			else if(oo instanceof CharSequence)
				sb.append((CharSequence)oo);
			else
				sb.append(oo);
		}
		return this;
	}
	
	public StringBuilder2 joinLists(String div, int group, List<?> o, List<?>... os) {
		assert !o.isEmpty() : "Lists must not be empty";
		for(List<?> s : os)
			assert s.size() == o.size() : "Lists must be of the same size";
		int i = 0;
		for(int j = 0; j < o.size(); j++) {
			if(j != 0 && i++ == group-1) {
				i = 0;
				append(div);
			}
			append(o.get(j));
			for(int k = 0; k < os.length; k++) {
				if(i++ == group-1) {
					i = 0;
					append(div);
				}
				append(os[k].get(j));
			}
		}
		return this;
	}
	
	public StringBuilder2 trimR(int i) {
		sb.delete(sb.length()-i, sb.length());
		return this;
	}
	
	@Override
	public int length() {
		return sb.length();
	}

	@Override
	public char charAt(int index) {
		return sb.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return sb.subSequence(start, end);
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return sb.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return sb.hashCode();
	}

}
