package com.frostedmc.nightfall.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public enum CustomSkullHash {

    ILLUMINATI_HEAD("eyJ0aW1lc3RhbXAiOjE1MTEwNDU1NjM4OTcsInByb2ZpbGVJZCI6IjdkYTJhYjNhOTNjYTQ4ZWU4MzA0OGFmYzNiODBlNjhlIiwicHJvZmlsZU5hbWUiOiJHb2xkYXBmZWwiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzVkNWVlZTQ4NjBlNjMxMTc1MzJlNWE2MjdhYWYxZjhhZmNlOTExM2I4NDMzNDUzYzE0M2E3ZWUwZmMxIn19fQ"),
    ILLUMINATI_ORB("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0MGI4N2Q1MjI3MWQyYTc1NWRlZGM4Mjg3N2UwZWQzZGY2N2RjYzQyZWE0NzllYzE0NjE3NmIwMjc3OWE1In19fQ==");

    @Getter private final String hash;

    CustomSkullHash(String hash) {
        this.hash = hash;
    }

    public ItemStack construct() {
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        new SkullProfile(this.getHash()).applyTextures(itemStack);
        return itemStack;
    }
}