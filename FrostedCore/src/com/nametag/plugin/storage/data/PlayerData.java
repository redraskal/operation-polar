package com.nametag.plugin.storage.data;

import java.util.UUID;

public class PlayerData {

    private String name;
    private UUID uuid;
    private String prefix;
    private String suffix;

    public PlayerData(String name, UUID uuid, String prefix, String suffix) {
    	this.name = name;
    	this.uuid = uuid;
    	this.prefix = prefix;
    	this.suffix = suffix;
    }
    
    public String getName() {
    	return name;
    }
    
    public UUID getUUID() {
    	return uuid;
    }
    
    public String getPrefix() {
    	return prefix;
    }
    
    public String getSuffix() {
    	return suffix;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setUUID(UUID uuid) {
    	this.uuid = uuid;
    }
    
    public void setPrefix(String prefix) {
    	this.prefix = prefix;
    }
    
    public void setSuffix(String suffix) {
    	this.suffix = suffix;
    }
}