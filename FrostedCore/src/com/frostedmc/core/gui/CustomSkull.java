package com.frostedmc.core.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 9/8/2016.
 */
public class CustomSkull {

    private static CustomSkull instance;

    public static CustomSkull getInstance() {
        if(instance == null) {
            instance = new CustomSkull();
        }

        return instance;
    }

    public String COIN_TEXTURES = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDViYmFhMmIyN2UwZTJkOGJlYjc4ZDRlNmNlYTNhNmM5MjdhMmMxNDMyNTlhOWMzY2M4N2JlZGRmNzhlNDY2In19fQ==";
    public String SLIME_TEXTURES = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTZhZDIwZmMyZDU3OWJlMjUwZDNkYjY1OWM4MzJkYTJiNDc4YTczYTY5OGI3ZWExMGQxOGM5MTYyZTRkOWI1In19fQ==";

    private CustomSkull() {}

    @SuppressWarnings("deprecation")
    public ItemStack create(String displayName, String url, String... lore) {
        ItemStack itemStack = this.createSkull(url);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        if (lore != null) {
            List<String> finalLore = new ArrayList<String>();

            for (String s : lore) {
                finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            itemMeta.setLore(finalLore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @SuppressWarnings("deprecation")
    public ItemStack create(String displayName, String url, List<String> lore) {
        ItemStack itemStack = this.createSkull(url);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        if (lore != null) {
            List<String> finalLore = new ArrayList<String>();

            for (String s : lore) {
                finalLore.add(ChatColor.translateAlternateColorCodes('&', s));
            }

            itemMeta.setLore(finalLore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack create(String displayName, String url) {
        return create(displayName, url, new String[]{});
    }

    public ItemStack createSkull(String url) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

        if (url.isEmpty()) {
            return head;
        }

        SkullMeta headMeta = (SkullMeta)head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException|IllegalArgumentException|IllegalAccessException e1) {
            e1.printStackTrace();
        }

        head.setItemMeta(headMeta);
        return head;
    }
}