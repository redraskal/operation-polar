package com.nametag.plugin.storage.data;

public class FakeTeam {

    private String name;
    private String prefix = "";
    private String suffix = "";

    public FakeTeam(String name, String prefix, String suffix) {
    	this.name = name;
    	this.prefix = prefix;
    	this.suffix = suffix;
    }
    
    public boolean isSimilar(FakeTeam fakeTeam) {
        return fakeTeam != null && fakeTeam.getPrefix().equals(prefix) && fakeTeam.getSuffix().equals(suffix);
    }

    public String getPrefix() {
    	return prefix;
    }
    
    public String getSuffix() {
    	return suffix;
    }
    
    public String getName() {
    	return name;
    }
}