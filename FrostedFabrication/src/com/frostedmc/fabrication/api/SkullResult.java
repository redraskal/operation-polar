package com.frostedmc.fabrication.api;

import com.frostedmc.core.gui.CustomSkull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class SkullResult {

    private String name;
    private UUID skullOwner;
    private String textures;

    public SkullResult(String name, UUID skullOwner, String textures) {
        this.name = name;
        this.skullOwner = skullOwner;
        this.textures = textures;
    }

    public String getName() {
        return this.name;
    }

    public UUID getSkullOwner() {
        return this.skullOwner;
    }

    public String getTextures() {
        return this.textures;
    }

    public ItemStack create(String displayName) {
        return CustomSkull.getInstance().create(displayName, this.textures);
    }

    public ItemStack create(String displayName, String... lore) {
        return CustomSkull.getInstance().create(displayName, this.textures, lore);
    }
}