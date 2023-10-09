package com.frostedmc.core.api.account.report;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public enum ReportCategory {

    HACKING("Hacking", 0),
    CHAT_ABUSE("Chat Abuse", 1),
    EXPLOITING("Exploiting", 2);

    private String name;
    private int id;

    private ReportCategory(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public static ReportCategory byName(String name) {
        for(ReportCategory reportCategory : ReportCategory.values()) {
            if(reportCategory.getName().equalsIgnoreCase(name)) return reportCategory;
        }
        return null;
    }

    public static ReportCategory byId(int id) {
        for(ReportCategory reportCategory : ReportCategory.values()) {
            if(reportCategory.getId() == id) return reportCategory;
        }
        return null;
    }
}