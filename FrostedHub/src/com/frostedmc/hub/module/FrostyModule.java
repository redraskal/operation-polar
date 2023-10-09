package com.frostedmc.hub.module;

import com.frostedmc.core.module.Module;
import com.frostedmc.core.utils.BlinkEffect;
import com.frostedmc.core.utils.NBTUtils;
import com.frostedmc.hub.Hub;
import com.frostedmc.hub.gui.FrostyGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class FrostyModule extends Module implements Listener {

    private Location location;
    private Snowman snowman;
    private ArmorStand base;
    private ArmorStand top;
    private JavaPlugin javaPlugin;

    public FrostyModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.location = new Location(Bukkit.getWorld("world"), 38.5, 91.0, 68.5, 46.2f, 0.7f);
    }

    @Override
    public String name() {
        return "Frosty";
    }

    @Override
    public void onEnable() {
        snowman = location.getWorld().spawn(location.clone(), Snowman.class);
        snowman.setCanPickupItems(false);
        snowman.setRemoveWhenFarAway(false);
        top = location.getWorld().spawn(location.clone().add(0, 1.0, 0), ArmorStand.class);
        top.setVisible(false);
        top.setBasePlate(false);
        top.setSmall(true);
        top.setGravity(false);
        base = location.getWorld().spawn(location.clone().subtract(0, 1.5, 0), ArmorStand.class);
        base.setVisible(false);
        base.setBasePlate(false);
        base.setSmall(true);
        base.setGravity(false);
        base.setPassenger(snowman);
        NBTUtils.defaultNPCTags(snowman);
        NBTUtils.defaultNPCTags(base);
        NBTUtils.defaultNPCTags(top);
        this.nametagRunnable();
        Hub.getInstance().getServer().getPluginManager().registerEvents(this, Hub.getInstance());
    }

    private void nametagRunnable() {
        BlinkEffect eff = new BlinkEffect("Frosty", ChatColor.WHITE, ChatColor.AQUA, ChatColor.DARK_AQUA);
        eff.next();
        new BukkitRunnable() {
            public void run() {
                if(snowman != null && !snowman.isDead()) {
                    top.setCustomName(eff.next());
                    top.setCustomNameVisible(true);
                    snowman.setVelocity(new Vector(0, 0, 0));
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(javaPlugin, 0, 3L);
    }

    @EventHandler
    public void onEntityInteract(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        if(event.getEntity() instanceof Snowman) {
            ((Player) event.getDamager()).closeInventory();
            new FrostyGUI((Player) event.getDamager());
        }
    }

    @EventHandler
    public void onSnowmanClick(PlayerInteractEntityEvent event) {
        if(event.getRightClicked() instanceof Snowman) {
            event.getPlayer().closeInventory();
            new FrostyGUI(event.getPlayer());
        }
    }

    @EventHandler
    public void onSnowmanClick(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked() instanceof Snowman) {
            event.getPlayer().closeInventory();
            new FrostyGUI(event.getPlayer());
        }
    }

    @Override
    public void onDisable() {
        snowman.remove();
        base.remove();
        top.remove();
        HandlerList.unregisterAll(this);
    }
}