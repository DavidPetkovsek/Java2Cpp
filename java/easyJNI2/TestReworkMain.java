package easyJNI2;

import easyJNI2.rework.EJNI;

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
		EJNI.createJNI(String.class);
	}

}
