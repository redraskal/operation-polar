package com.frostedmc.frostedgames;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class FireworkUtil {

    public static void shootRandomFirework(Location startLocation) {
        Firework firework = startLocation.getWorld().spawn(startLocation, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)))
                .withFade(Color.WHITE)
                .flicker(true)
                .trail(true)
                .build());
        firework.setFireworkMeta(meta);
        new BukkitRunnable() {
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(FrostedGames.getInstance(), 3L);
    }
}