package com.frostedmc.backbone.automate;

import com.frostedmc.backbone.Backbone;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Redraskal_2 on 11/6/2016.
 */
public class TemplateManager {

    private static TemplateManager instance = null;

    public static TemplateManager getInstance() {
        if(instance == null) {
            instance = new TemplateManager();
        }
        return instance;
    }

    private File templateFolder;
    private List<TemplateInfo> templateInfoList = new LinkedList<TemplateInfo>();

    private TemplateManager() {
        this.templateFolder = new File(Backbone.getInstance().getDataFolder().getAbsoluteFile()
                .getParentFile().getParentFile().getParentFile().getPath() + "/templates/");
        this.refreshTemplates();
    }

    public File getTemplateFolder() {
        return this.templateFolder;
    }

    public List<TemplateInfo> getTemplates() {
        return this.templateInfoList;
    }

    public TemplateInfo getTemplate(String name) {
        for(TemplateInfo templateInfo : this.templateInfoList) {
            if(templateInfo.getName().equalsIgnoreCase(name))
                return templateInfo;
        }
        return null;
    }

    public void refreshTemplates() {
        templateInfoList.clear();
        for(File folder : templateFolder.listFiles()) {
            if(!folder.isDirectory()) continue;
            File templateInfoJson = new File(folder.getPath() + "/templateInfo.json");
            TemplateInfo templateInfo = new TemplateInfo(folder, 512, 512);
            if(templateInfoJson.exists()) {
                try {
                    JsonElement parsedData = new JsonParser().parse(new FileReader(templateInfoJson));
                    templateInfo.setXms(parsedData.getAsJsonObject().get("xms").getAsInt());
                    templateInfo.setXmx(parsedData.getAsJsonObject().get("xmx").getAsInt());
                } catch (FileNotFoundException e) {}
            }
            templateInfoList.add(templateInfo);
        }
    }
}
