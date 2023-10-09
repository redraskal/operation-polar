package com.frostedmc.kingdoms.backend;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class Configuration {

    private File file;
    private JSONParser jsonParser;
    private JSONObject jsonObject;

    public Configuration(File file) {
        this.file = file;
        this.jsonParser = new JSONParser();
    }

    public boolean create() {
        try {
            this.file.createNewFile();
            FileWriter fileWriter = new FileWriter(this.file);
            fileWriter.write("{}"); fileWriter.flush(); fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean reload() {
        try {
            Object fileObject = this.jsonParser.parse(new FileReader(this.file));
            if(fileObject == null)
                return false;
            this.jsonObject = (JSONObject) fileObject;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean save() {
        try {
            FileWriter fileWriter = new FileWriter(this.file);
            fileWriter.write(this.jsonObject.toJSONString()); fileWriter.flush(); fileWriter.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject get() {
        if(!this.file.exists())
            this.create();
        if(this.jsonObject == null)
            this.reload();
        return this.jsonObject;
    }

    public boolean exists() {
        return this.file.exists();
    }
}