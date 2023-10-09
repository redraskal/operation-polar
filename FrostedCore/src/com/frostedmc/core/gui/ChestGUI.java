package com.frostedmc.core.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 9/1/2016.
 */
public class ChestGUI implements Listener {

    public Player p;
    public Inventory i;
    public GUICallback c;
    public boolean aC;
    public boolean iO;
    public int la = 0;

    public JavaPlugin j;
    public ChestGUI iN;

    public ChestGUI(Player player, int size, String title, boolean allowClick, GUICallback callback, JavaPlugin javaPlugin) {
        this.p = player;
        this.i = Bukkit.createInventory(null, size, title);
        this.c = callback;
        this.aC = allowClick;
        this.iO = true;
        this.iN = this;
        this.j = javaPlugin;

        c.callback(this, GUICallback.CallbackType.INIT, null);

        p.openInventory(i);

        this.j.getServer().getPluginManager().registerEvents(this, this.j);

        new BukkitRunnable() {
            public void run() {
                new BukkitRunnable() {
                    public void run() {
                        if(iO) {
                            c.onSecond(iN);
                        } else {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(j, 0, 5L);
            }
        }.runTaskLater(j, 10L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getWhoClicked().getName() == p.getName()) {
            try {
                if(!aC) {
                    event.setCancelled(true);
                }

                if(event.getCurrentItem() != null
                        && event.getCurrentItem().getType() != Material.AIR
                        && event.getCurrentItem().hasItemMeta()) {
                    la = event.getSlot();
                    c.callback(this, GUICallback.CallbackType.CLICK, event.getCurrentItem());
                }
            } catch (Exception e) {}
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(event.getPlayer().getName() == p.getName()) {
            HandlerList.unregisterAll(this);
            iO = false;
            c.callback(this, GUICallback.CallbackType.CLOSE, null);
        }
    }
}