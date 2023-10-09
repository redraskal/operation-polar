package com.frostedmc.core.cosmetics;

import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.core.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public abstract class Particle {

    public abstract String name();

    public Random random = new Random();

    public Player player;
    public long delay;
    public Location location;
    public Location lastLocation;

    public Particle(Player player, long delay, JavaPlugin javaPlugin) {
        this.player = player;
        this.delay = delay;
        this.location = player.getLocation();
        this.lastLocation = player.getLocation();
        player.sendMessage(PredefinedMessage.COSMETICS_PARTICLE_ENABLE.registerPlaceholder("%particle%", name()).build());

        new BukkitRunnable() {
            public void run() {
                if(CosmeticsManager.getInstance().hasParticle(player) && CosmeticsManager.getInstance().getParticle(player) == name()
                        && player.isOnline()) {
                    location = player.getLocation();
                    update(!Utils.compareLocations(location, lastLocation));
                    lastLocation = player.getLocation();
                } else {
                    this.cancel();
                    player.sendMessage(PredefinedMessage.COSMETICS_PARTICLE_DISABLE.registerPlaceholder("%particle%", name()).build());
                }
            }
        }.runTaskTimerAsynchronously(javaPlugin, 0, delay);
    }

    public abstract void update(boolean moving);
}