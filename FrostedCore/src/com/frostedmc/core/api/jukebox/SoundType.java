package com.frostedmc.core.api.jukebox;

/**
 * Created by Redraskal_2 on 9/28/2016.
 */
public enum SoundType {

    MUSIC("music"),
    SOUND_EFFECT("soundEffect");

    private String value;

    private SoundType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}