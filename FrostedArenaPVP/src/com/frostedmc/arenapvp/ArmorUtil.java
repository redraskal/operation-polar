package com.frostedmc.arenapvp;

import org.bukkit.Color;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class ArmorUtil {

    public static ItemStack addEnchantment(ItemStack itemStack, Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return itemStack;
    }

    public static ItemStack setColor(ItemStack itemStack, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
        meta.setColor(color);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}