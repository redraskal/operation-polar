package com.frostedmc.arenapvp;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class PotionUtil {

    public static ItemStack create(PotionType potionType, boolean splash) {
        ItemStack itemStack = new ItemStack(Material.POTION, 1);
        Potion potion = new Potion(1);
        potion.setType(potionType);
        potion.setSplash(splash);
        potion.apply(itemStack);
        return itemStack;
    }

    public static ItemStack create(PotionType potionType, boolean splash, int level) {
        ItemStack itemStack = new ItemStack(Material.POTION, 1);
        Potion potion = new Potion(1);
        potion.setType(potionType);
        potion.setSplash(splash);
        potion.setLevel(level);
        potion.apply(itemStack);
        return itemStack;
    }
}