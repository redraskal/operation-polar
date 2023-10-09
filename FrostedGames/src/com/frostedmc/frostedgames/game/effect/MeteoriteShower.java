package com.frostedmc.frostedgames.game.effect;

import com.frostedmc.frostedgames.FrostedGames;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class MeteoriteShower {

    public MeteoriteShower(Player target) {
        for(int i=0; i<5; i++) {
            Fireball fireball = target.getWorld().spawn(target.getLocation().clone().add(i*3, 30, i*3), Fireball.class);
            fireball.setDirection(fireball.getLocation().getDirection().subtract(target.getLocation().getDirection()));
            fireball.setIsIncendiary(true);
            new BukkitRunnable() {
                public void run() {
                    if(fireball.isDead()) {
                        this.cancel();
                    } else {
                        fireball.setVelocity(new Vector(0, -.5, 0));
                    }
                }
            }.runTaskTimer(FrostedGames.getInstance(), 0, 1L);
        }
    }
}