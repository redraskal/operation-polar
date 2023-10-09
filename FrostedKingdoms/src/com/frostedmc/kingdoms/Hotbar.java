package com.frostedmc.kingdoms;

import com.frostedmc.core.gui.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public enum Hotbar {

    LOBBY_NO_DATA(
            new HotbarBuilder()
            .add(ItemCreator.getInstance().createItem(Material.ENCHANTED_BOOK, 1, 0, "&2&lCreate a Kingdom &7(Right-Click)"), 3)
            .add(ItemCreator.getInstance().createItem(Material.EYE_OF_ENDER, 1, 0, "&e&lJoin a Kingdom &7(Right-Click)"), 5)
    ),
    LOBBY_DATA(
            new HotbarBuilder()
                    .add(ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&2&lTeleport to Kingdom &7(Right-Click)"), 3)
                    .add(ItemCreator.getInstance().createItem(Material.REDSTONE, 1, 0, "&c&lDisband your Kingdom &7(Right-Click)"), 5)
    );

    private HotbarBuilder hotbarBuilder;

    private Hotbar(HotbarBuilder hotbarBuilder) {
        this.hotbarBuilder = hotbarBuilder;
    }

    public Hotbar apply(Player player) {
        hotbarBuilder.apply(player);
        return this;
    }
}