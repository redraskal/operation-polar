package com.frostedmc.backbone.automate;

import java.io.File;

/**
 * Created by Redraskal_2 on 11/6/2016.
 */
public class TemplateInfo {

    private File directory;
    private int xmx;
    private int xms;

    TemplateInfo(File directory) {
        this.directory = directory;
    }

    TemplateInfo(File directory, int xmx, int xms) {
        this.directory = directory;
        this.xmx = xms;
        this.xms = xms;
    }

    public File getDirectory() {
        return this.directory;
    }

    public String getName() {
        return this.directory.getName();
    }

    public int getXmx() {
        return this.xmx;
    }

    public int getXms() {
        return this.xms;
    }

    public void setXmx(int xmx) {
        this.xmx = xmx;
    }

    public void setXms(int xms) {
        this.xms = xms;
    }
}