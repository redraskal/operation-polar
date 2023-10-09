package com.frostedmc.gamemanager.listener;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.manager.DamageManager;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class Damage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
            event.setCancelled(true);
            return;
        }
        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                && event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                && event.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.enablePVE) {
                event.setCancelled(true);
                return;
            }
        }
        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            event.getEntity().getWorld().playEffect(event.getEntity().getLocation().add(0, new Random().nextDouble() + 0.5D, 0), Effect.STEP_SOUND, 152);
            event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.SHOOT_ARROW, 1, 2);
        }
        if(event.getEntity() instanceof Player) {
            Player entity = (Player) event.getEntity();
            if((entity.getHealth() - event.getFinalDamage()) <= 0) {
                event.setCancelled(true);
                if(((Player) event.getEntity()).getKiller() != null) {
                    DamageManager.handleDeath(entity, ((Player) event.getEntity()).getKiller(), DamageManager.formatCause(event.getCause()));
                } else {
                    DamageManager.handleDeath(entity, null, DamageManager.formatCause(event.getCause()));
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if(event.getEntity().getType() != EntityType.PLAYER) return;
        if(event.getDamager() instanceof Player) {
            if(SpectatorMode.getInstance().contains((Player) event.getDamager())) {
                event.setCancelled(true);
                return;
            }
        }
        if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
            event.setCancelled(true);
            return;
        }
        if(!(event.getDamager() instanceof Player)) {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.enablePVM) {
                event.setCancelled(true);
                return;
            }
        } else {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.enablePVP) {
                event.setCancelled(true);
                return;
            } else {
                Player entity = (Player) event.getEntity();
                event.getEntity().getWorld().playEffect(event.getEntity().getLocation().add(0, new Random().nextDouble() + 0.5D, 0), Effect.STEP_SOUND, 152);
                event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.SHOOT_ARROW, 1, 2);
                if((entity.getHealth() - event.getFinalDamage()) <= 0) {
                    event.setCancelled(true);
                    DamageManager.handleDeath(entity, event.getDamager(), DamageManager.formatCause(event.getCause()));
                }
            }
        }
    }
}