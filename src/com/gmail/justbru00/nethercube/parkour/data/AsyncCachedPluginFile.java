package com.gmail.justbru00.nethercube.parkour.data;

import java.util.UUID;

import com.gmail.justbru00.nethercube.parkour.utils.PluginFile;

public class AsyncCachedPluginFile {

	private PluginFile file;
	private boolean saveNeeded;
	private UUID playerUuid;

	public AsyncCachedPluginFile(PluginFile file, boolean saveNeeded, UUID playerUuid) {
		super();
		this.file = file;
		this.saveNeeded = saveNeeded;
		this.playerUuid = playerUuid;
	}

	public PluginFile getFile() {
		return file;
	}
	
	public void setFile(PluginFile file) {
		this.file = file;
	}
	
	
	public boolean isSaveNeeded() {
		return saveNeeded;
	}
	
	public void setSaveNeeded(boolean saveNeeded) {
		this.saveNeeded = saveNeeded;
	}

	public UUID getPlayerUuid() {
		return playerUuid;
	}

	public void setPlayerUuid(UUID playerUuid) {
		this.playerUuid = playerUuid;
	}	
	
}
