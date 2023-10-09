package com.frostedmc.frostedgames.listener;

import com.frostedmc.frostedgames.game.CustomDamage;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class Damage implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().hasPotionEffect(PotionEffectType.JUMP)) {
            for(PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                if(potionEffect.getType() == PotionEffectType.JUMP) {
                    if(potionEffect.getAmplifier() >= 200) {
                        if(event.getFrom().getX() != event.getTo().getX()
                                || event.getFrom().getZ() != event.getTo().getZ()) {
                            Location newLocation = event.getFrom();
                            newLocation.setY(event.getTo().getY());
                            newLocation.setYaw(event.getTo().getYaw());
                            newLocation.setPitch(event.getTo().getPitch());
                            event.setTo(newLocation);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if(event.getEntity() instanceof Silverfish || event.getEntity() instanceof Snowman) {
            if(event.getTarget().getType() != EntityType.PLAYER)
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(InternalGameSettings.status != Status.INGAME) {
            event.setCancelled(true);
        }
        if(InternalGameSettings.status == Status.INGAME) {
            if(InternalGameSettings.enablePeace) {
                if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                        || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    event.setCancelled(true);
                }
            }
        }
        if(!event.isCancelled()) {
            event.getEntity().getWorld().playEffect(event.getEntity().getLocation().add(0, new Random().nextDouble() + 0.5D, 0), Effect.STEP_SOUND, 152);
            event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.SHOOT_ARROW, 1, 2);
            if(event.getEntity() instanceof Player) {
                Player entity = (Player) event.getEntity();
                if((entity.getHealth() - event.getFinalDamage()) <= 0) {
                    event.setCancelled(true);
                    if(((Player) event.getEntity()).getKiller() != null) {
                        CustomDamage.handleDeath(entity, ((Player) event.getEntity()).getKiller(), CustomDamage.formatCause(event.getCause()));
                    } else {
                        CustomDamage.handleDeath(entity, null, CustomDamage.formatCause(event.getCause()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if(event.getDamager().getType() != EntityType.PLAYER)
            return;
        if(InternalGameSettings.status == Status.INGAME) {
            if(InternalGameSettings.gameMaker != null) {
                if(event.getDamager().getName().equalsIgnoreCase(InternalGameSettings.gameMaker.getName())) {
                    event.setCancelled(true);
                }
            }
        }
        if(!event.isCancelled()) {
            if(event.getEntity() instanceof Player) {
                Player entity = (Player) event.getEntity();
                if((entity.getHealth() - event.getFinalDamage()) <= 0) {
                    event.setCancelled(true);
                    CustomDamage.handleDeath(entity, event.getDamager(), CustomDamage.formatCause(event.getCause()));
                }
            }
        }
    }
}