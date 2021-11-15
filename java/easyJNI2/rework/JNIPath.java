package easyJNI2.rework;

import java.util.ArrayList;

/**
 * Holds the path information about a JNI Object.
 * 
 * <p> Information is held as a list of 'segments'
 */
public class JNIPath {
	
	/** Holds information on where each segment came from */
	public ArrayList<Source> pathDetails = new ArrayList<>();
	/** What is the text for each segment */
	public ArrayList<String> path = new ArrayList<>();
	
	/**
	 * Append another 'segment' to the path
	 * 
	 * @param name The text linked to the path segment
	 * @param src The type of segment
	 */
	public void appendToPath(String name, Source src) {
		assert pathDetails.get(pathDetails.size()-1) == Source.PACKAGE || src == Source.CLASS : "You cannot have a package after a class segment";
		pathDetails.add(src);
		path.add(name);
	}
	
	/**
	 * This represents where a segment of a JNIPath came from
	 */
	public static enum Source{
		/** The segment came from the package */
		PACKAGE,
		/** The segment came from being declared inside of a class */
		CLASS
	}
}
