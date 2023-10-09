package com.frostedmc.kingdoms.listener;

import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Redraskal_2 on 11/14/2016.
 */
public class CustomTNT implements Listener {

    @EventHandler
    public void onBlockExplode(EntityExplodeEvent entityExplodeEvent) {
        entityExplodeEvent.setCancelled(true);
        ParticleEffect.EXPLOSION_NORMAL.display(1, 1, 1, 0, 6, entityExplodeEvent.getLocation(), 15);
        for(Block radius : getBlocksInRadius(entityExplodeEvent.getLocation(), 5, false)) {
            if(radius.getType() != null
                    && radius.getType() != Material.AIR) {
                spawn(radius.getType(), radius.getData(), radius.getLocation());
            }
        }
        entityExplodeEvent.getLocation().getWorld().createExplosion(entityExplodeEvent.getLocation(), 5, true);
    }

    private void spawn(Material material, byte data, Location location) {
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(
                location, material, data);
        fallingBlock.setDropItem(false);
        fallingBlock.setVelocity(new Vector(0, 2.5, 0).add(randomVector().multiply(0.3)));
    }

    private Vector randomVector() {
        double rnd = new Random().nextDouble() * 2.0D * 3.14D;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new Vector(x, 0.0D, z);
    }

    private List<org.bukkit.block.Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
        List<Block> blocks = new ArrayList<Block>();

        int bX = location.getBlockX();
        int bY = location.getBlockY();
        int bZ = location.getBlockZ();
        for (int x = bX - radius; x <= bX + radius; x++) {
            for (int y = bY - radius; y <= bY + radius; y++) {
                for (int z = bZ - radius; z <= bZ + radius; z++)
                {
                    double distance = (bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z);
                    if ((distance < radius * radius) && ((!hollow) || (distance >= (radius - 1) * (radius - 1))))
                    {
                        Location l = new Location(location.getWorld(), x, y, z);
                        if (l.getBlock().getType() != org.bukkit.Material.BARRIER) {
                            blocks.add(l.getBlock());
                        }
                    }
                }
            }
        }
        return blocks;
    }
}