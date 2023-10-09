package com.frostedmc.frostedgames.game.effect;

import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.CustomDamage;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Snowman;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class SnowmenHoard {

    public SnowmenHoard(Player target) {
        for(int i=0; i<5; i++) {
            Snowman snowman = target.getWorld().spawn(target.getLocation().clone().add(3+new Random().nextInt(3), .5, 3+new Random().nextInt(3)), Snowman.class);
            Silverfish silverfish = target.getWorld().spawn(snowman.getLocation(), Silverfish.class);
            silverfish.setPassenger(snowman);
            silverfish.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 2), true);
            silverfish.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2), true);
            new BukkitRunnable() {
                public void run() {
                    if(silverfish.isDead() || snowman.isDead() || target.isDead()) {
                        this.cancel();
                        if(!silverfish.isDead()) {
                            silverfish.remove();
                        }
                    } else {
                        for(Entity near : silverfish.getNearbyEntities(2, 2, 2)) {
                            if(near instanceof Player) {
                                Player n = (Player) near;
                                if(n.getGameMode() != GameMode.CREATIVE
                                        && InternalGameSettings.districtMap.containsKey(n)) {
                                    if((n.getHealth() - 0.5) <= 0) {
                                        CustomDamage.handleDeath(n, null, "Snowmen Hoard");
                                    } else {
                                        n.damage(0.5);
                                    }
                                }
                            }
                        }
                        if(silverfish.getLocation().distance(target.getLocation()) >= 5) {
                            silverfish.teleport(target.getLocation().clone().add(3 + new Random().nextInt(3), .5, 3 + new Random().nextInt(3)));
                        }
                        silverfish.setTarget(target);
                        snowman.getLocation().setDirection(target.getLocation().getDirection()
                                .subtract(snowman.getLocation().getDirection()));
                    }
                }
            }.runTaskTimer(FrostedGames.getInstance(), 0, 1L);
        }
    }
}
