package com.frostedmc.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/2/2016.
 */
public class JoinItem implements Listener {

    private ItemStack itemStack;
    private int slot;
    private boolean onReload;

    public JoinItem(JavaPlugin javaPlugin, ItemStack itemStack, int slot, boolean onReload) {
        this.itemStack = itemStack;
        this.slot = slot;
        this.onReload = onReload;

        if(this.onReload) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                ItemStack customItem = itemStack.clone();

                if(customItem.getType() == Material.SKULL_ITEM
                        && customItem.getData().getData() == 3) {
                    SkullMeta customMeta = (SkullMeta) customItem.getItemMeta();
                    customMeta.setOwner(player.getName());
                    customItem.setItemMeta(customMeta);
                }

                player.getInventory().setItem(this.slot, customItem);
            }
        }

        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        ItemStack customItem = itemStack.clone();

        if(customItem.getType() == Material.SKULL_ITEM
                && customItem.getData().getData() == 3) {
            SkullMeta customMeta = (SkullMeta) customItem.getItemMeta();
            customMeta.setOwner(playerJoinEvent.getPlayer().getName());
            customItem.setItemMeta(customMeta);
        }

        playerJoinEvent.getPlayer().getInventory().setItem(this.slot, customItem);
    }

    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}