package com.frostedmc.hub.module;

import com.frostedmc.core.module.Module;
import com.frostedmc.core.utils.NBTUtils;
import com.frostedmc.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Created by Redraskal_2 on 2/17/2017.
 */
public class CasualGamesModule extends Module implements Listener {

    @Override
    public String name() {
        return "Casual Games";
    }

    private Location location;
    private Villager villager;
    private ArmorStand base;
    private ArmorStand nameTag;

    @Override
    public void onEnable() {
        this.location = new Location(Bukkit.getWorld("world"), 103.5, 79.0, 48.5, 2.1f, 0.3f);
        this.villager = location.getWorld().spawn(this.location, Villager.class);
        villager.setRemoveWhenFarAway(false);
        villager.setCanPickupItems(false);
        villager.setMetadata("casual-games", new FixedMetadataValue(Hub.getInstance(), true));
        this.base = location.getWorld().spawn(location.clone().subtract(0, 1.5, 0), ArmorStand.class);
        base.setVisible(false);
        base.setBasePlate(false);
        base.setSmall(true);
        base.setGravity(false);
        base.setPassenger(this.villager);
        nameTag = Bukkit.getWorld("world").spawn(location.clone().add(0, 1.2, 0), ArmorStand.class);
        nameTag.setSmall(true);
        nameTag.setBasePlate(false);
        nameTag.setVisible(false);
        nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e&lCasual Games"));
        nameTag.setCustomNameVisible(true);
        nameTag.setGravity(false);
        NBTUtils.defaultNPCTags(nameTag);
        NBTUtils.defaultNPCTags(base);
        NBTUtils.defaultNPCTags(villager);
        Hub.getInstance().getServer().getPluginManager().registerEvents(this, Hub.getInstance());
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata("casual-games")) {
            event.setCancelled(true);
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bCasual Games> &7This feature is coming soon."));
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.villager.remove();
        this.base.remove();
        this.nameTag.remove();
    }
}