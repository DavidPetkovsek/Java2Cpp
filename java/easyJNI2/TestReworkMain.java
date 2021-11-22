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
		JNIType t = EJNI.createJNI(String.class); // loads everything related to the String class
		System.out.println(t.buildCppHeader(new StringBuilder2()));

//		for(var v : EJNI.classes.values())
//			System.out.println(v.buildCppHeader(new StringBuilder2()));
//
//		for(var v : EJNI.interfaces.values())
//			System.out.println(v.buildCppHeader(new StringBuilder2()));
//
//		for(var v : EJNI.enums.values())
//			System.out.println(v.buildCppHeader(new StringBuilder2()));
		
	}

}
