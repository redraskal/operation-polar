package com.frostedmc.fabrication.listener;

import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Created by Redraskal_2 on 10/30/2016.
 */
public class Falling implements Listener {

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent entityChangeBlockEvent) {
        if(entityChangeBlockEvent.getEntity() instanceof FallingBlock) {
            entityChangeBlockEvent.setCancelled(true);
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(entityChangeBlockEvent.getTo(), entityChangeBlockEvent.getData()),
                    1, 1, 1, 0, 7, entityChangeBlockEvent.getEntity().getLocation(), 15);
            ParticleEffect.FIREWORKS_SPARK.display(1, 1, 1, 0, 6, entityChangeBlockEvent.getEntity().getLocation(), 15);
            ParticleEffect.PORTAL.display(1, 1, 1, 0, 6, entityChangeBlockEvent.getEntity().getLocation(), 15);
            ParticleEffect.EXPLOSION_NORMAL.display(1, 1, 1, 0, 3, entityChangeBlockEvent.getEntity().getLocation(), 30);
            ParticleEffect.EXPLOSION_NORMAL.display(1, 1, 1, 0, 2, entityChangeBlockEvent.getEntity().getLocation(), 30);
            entityChangeBlockEvent.getEntity().getWorld().playSound(entityChangeBlockEvent.getEntity().getLocation(), Sound.DIG_GRASS, 10, 1);
        }
    }
}