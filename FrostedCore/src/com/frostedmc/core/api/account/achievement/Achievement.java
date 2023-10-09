package com.frostedmc.core.api.account.achievement;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class Achievement {

    private String title;
    private String description;

    public Achievement(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }
}