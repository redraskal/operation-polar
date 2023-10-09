package com.frostedmc.nightfall.structure;

import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Soundinizer {

    /**
     * Returns the sound best fit for the material.
     * @param material
     * @return
     */
    public static Sound get(Material material) {
        if(material.toString().contains("STONE")) {
            return Sound.STEP_STONE;
        } if(material.toString().contains("GRAVEL")) {
            return Sound.STEP_GRAVEL;
        } if(material.toString().contains("LADDER")) {
            return Sound.STEP_LADDER;
        } if(material.toString().contains("SAND")) {
            return Sound.STEP_SAND;
        } if(material.toString().contains("SNOW")) {
            return Sound.STEP_SNOW;
        } if(material.toString().contains("WOOD")) {
            return Sound.STEP_WOOD;
        } if(material.toString().contains("WOOL")) {
            return Sound.STEP_WOOL;
        }
        return Sound.STEP_GRASS;
    }
}