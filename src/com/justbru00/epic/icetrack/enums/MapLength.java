package com.justbru00.epic.icetrack.enums;

public enum MapLength {

	SHORT, MEDIUM, LONG, EXTENDED;
	
	
	public static MapLength fromString(String s) {
		if (s.equalsIgnoreCase("short")) {
			return SHORT;
		} else if (s.equalsIgnoreCase("medium")) {
			return MEDIUM;	
		} else if (s.equalsIgnoreCase("long")) {
			return LONG;	
		} else if (s.equalsIgnoreCase("extended")) {
			return EXTENDED;	
		} else {
			return null;
		}
	}
}
