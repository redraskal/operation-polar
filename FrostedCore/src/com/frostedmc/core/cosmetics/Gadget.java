package com.frostedmc.core.cosmetics;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 9/8/2016.
 */
public abstract class Gadget implements Listener {

    private Player player;
    public Random random = new Random();
    public JavaPlugin javaPlugin;
    private boolean enabled = false;
    private boolean enabledGadget = false;

    public abstract String name();

    public abstract ItemStack item();

    public abstract long delay();

    public abstract void onUpdate(Player player);

    public abstract void onGadgetUse(Player player);

    public abstract void onGadgetUseWhileEnabled(Player player);

    public abstract void onEnable(Player player);

    public abstract void onDisable(Player player);

    public Gadget(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public void enable(Player player) {
        enabled = true;
        this.player = player;
        player.sendMessage(PredefinedMessage.COSMETICS_GADGET_ENABLE.registerPlaceholder("%gadget%", name()).build());

        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
        player.getInventory().setItem(7, item());
        onEnable(player);
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        try {
            if(event.getPlayer().getUniqueId() != this.player.getUniqueId()) {
                return;
            }

            if(event.getAction() == Action.LEFT_CLICK_AIR
                    || event.getAction() == Action.LEFT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_AIR
                    || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if(event.getItem() != null && event.getItem().getType() != Material.AIR) {
                    String d = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

                    if(d.equalsIgnoreCase(ChatColor.stripColor(item().getItemMeta().getDisplayName()))) {
                        if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                            CosmeticsManager.getInstance().disableGadget(event.getPlayer());
                        } else {
                            if(enabledGadget) {
                                onGadgetUseWhileEnabled(event.getPlayer());
                            } else {
                                onGadgetUse(event.getPlayer());

                                enableUpdater(event.getPlayer());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {}
    }

    public void enableUpdater(Player player) {
        enabledGadget = true;

        new BukkitRunnable() {
            public void run() {
                if(enabled) {
                    if(enabledGadget) {
                        if(player.isOnline()) {
                            onUpdate(player);
                        } else {
                            this.cancel();
                            disableUpdater();
                        }
                    } else {
                        this.cancel();
                        disableUpdater();
                    }
                } else {
                    this.cancel();
                    disableUpdater();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, delay());
    }

    public void disableUpdater() {
        enabledGadget = false;
    }

    public void disable(Player player) {
        enabled = false;
        disableUpdater();
        resetInventory(player);
        player.sendMessage(PredefinedMessage.COSMETICS_GADGET_DISABLE.registerPlaceholder("%gadget%", name()).build());

        HandlerList.unregisterAll(this);

        onDisable(player);
    }

    public static void resetInventory(Player player) {
        if(player.isOnline()) {
            player.getInventory().setItem(7, ItemCreator.getInstance().createItem(Material.SLIME_BALL, 1, 0, "&e&lCOSMETICS &7(Right-Click)"));
        }
    }
}