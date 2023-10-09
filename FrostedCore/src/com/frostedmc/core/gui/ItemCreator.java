package com.frostedmc.core.gui;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 9/1/2016.
 */
public class ItemCreator {

    private static ItemCreator instance;

    public static ItemCreator getInstance() {
        return instance;
    }

    public static boolean initialize(JavaPlugin javaPlugin) {
        if(instance != null) {
            return false;
        }

        instance = new ItemCreator(javaPlugin);
        return true;
    }

    private JavaPlugin javaPlugin;

    private ItemCreator(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public ItemStack BLANK_ITEMSTACK = new ItemStack(Material.AIR);

    public List<String> colorizeList(List<String> list) {
        List<String> result = new ArrayList<String>();

        for(String line : list) {
            result.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return result;
    }

    public ItemStack createItem(Material type, int amount, int data, String name, List<String> lore) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        ItemMeta meta = item.getItemMeta();

        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(colorizeList(lore));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createItemPotion(Material type, int amount, int data, String name, List<String> lore) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        PotionMeta meta = (PotionMeta) item.getItemMeta();

        meta.clearCustomEffects();
        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(colorizeList(lore));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createItem(Material type, int amount, int data, String name) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        ItemMeta meta = item.getItemMeta();

        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createBanner(Material type, DyeColor dyeColor, int amount, int data, String name) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        BannerMeta meta = (BannerMeta) item.getItemMeta();

        item.setAmount(amount);
        meta.setBaseColor(dyeColor);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createBanner(Material type, DyeColor dyeColor, int amount, int data, String name, List<String> lore) {
        ItemStack item = new ItemStack(type, 1, (short) data);
        BannerMeta meta = (BannerMeta) item.getItemMeta();

        item.setAmount(amount);
        meta.setBaseColor(dyeColor);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(colorizeList(lore));
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createArmour(Material type, int amount, Color color, String name) {
        ItemStack item = new ItemStack(type, 1, (short) 0);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setColor(color);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createArmour(Material type, int amount, Color color, String name, List<String> lore) {
        ItemStack item = new ItemStack(type, 1, (short) 0);
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();

        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(colorizeList(lore));
        meta.setColor(color);
        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createSkull(int amount, String owner, String name) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        if(Bukkit.getPlayer(owner) != null) {
            Player o = Bukkit.getPlayer(owner);
            CraftPlayer cp = ((CraftPlayer) o);
            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, cp.getProfile());
            } catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e1) {
                e1.printStackTrace();
            }
        } else {
            meta.setOwner(owner);
        }

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createSkull(int amount, String owner, String name, List<String> lore) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, amount, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        item.setAmount(amount);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        if(Bukkit.getPlayer(owner) != null) {
            Player o = Bukkit.getPlayer(owner);
            CraftPlayer cp = ((CraftPlayer) o);
            try {
                Field profileField = meta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(meta, cp.getProfile());
            } catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e1) {
                e1.printStackTrace();
            }
        } else {
            meta.setOwner(owner);
        }

        meta.setLore(colorizeList(lore));
        item.setItemMeta(meta);

        return item;
    }
}