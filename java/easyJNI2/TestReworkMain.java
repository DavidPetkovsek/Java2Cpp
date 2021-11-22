package easyJNI2;

import easyJNI2.lib.StringBuilder2;
import easyJNI2.rework.EJNI;
import easyJNI2.rework.JNIType;

/**
 * Holds different tests for trying out the rework
 */
public class TestReworkMain {
	
	/**
	 * A test main
	 * 
	 * @param args unused
	 */
	public static void main(String[] args) {
		JNIType t = EJNI.createJNI(String.class);
		System.out.println(t.buildCppHeader(new StringBuilder2()));
	}

}
