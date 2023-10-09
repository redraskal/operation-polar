package com.frostedmc.fabrication.game;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public enum VoteType {

    CONCEPT("Concept"),
    ORIGINALITY("Originality"),
    TECHNIQUE("Technique"),
    EXECUTION("Execution"),
    PRESENTATION("Presentation");

    private String name;

    private VoteType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
