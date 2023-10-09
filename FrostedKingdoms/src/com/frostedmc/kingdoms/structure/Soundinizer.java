package com.frostedmc.kingdoms.structure;

import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Created by Redraskal_2 on 11/13/2016.
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