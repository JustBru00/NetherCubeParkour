package com.gmail.justbru00.nethercube.elytra.enums;

public enum MapDifficulty {

	EASY, NORMAL, HARD, EXTREME;
	
	
	public static MapDifficulty fromString(String s) {
		if (s.equalsIgnoreCase("easy")) {
			return EASY;
		} else if (s.equalsIgnoreCase("normal")) {
			return NORMAL;	
		} else if (s.equalsIgnoreCase("hard")) {
			return HARD;	
		} else if (s.equalsIgnoreCase("extreme")) {
			return EXTREME;	
		} else {
			return null;
		}
	}
	
}
