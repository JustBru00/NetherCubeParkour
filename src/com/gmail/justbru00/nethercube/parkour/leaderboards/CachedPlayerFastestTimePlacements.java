package com.gmail.justbru00.nethercube.parkour.leaderboards;

import java.util.HashMap;

public class CachedPlayerFastestTimePlacements {
	
	private HashMap<String, Integer> mapPositions = new HashMap<String, Integer>();
	
	public void addOrUpdateMapPosition(String mapinternalname, int position) {
		mapPositions.put(mapinternalname, position);
	}
	
	/**
	 * 
	 * @param mapinternalname
	 * @return Returns the position on the given map. -1 if lower than the top 100. -2 if no position calculated yet.
	 */
	public int getMapPosition(String mapinternalname) {
		if (!mapPositions.containsKey(mapinternalname)) {
			return -2;
		}
		
		return mapPositions.get(mapinternalname);
	}

	@Override
	public String toString() {
		return "CachedPlayerFastestTimePlacements [mapPositions=" + mapPositions + "]";
	}	
	
}
