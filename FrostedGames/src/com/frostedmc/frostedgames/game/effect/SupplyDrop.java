package com.frostedmc.frostedgames.game.effect;

import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.chest.ChestFiller;
import com.frostedmc.frostedgames.game.chest.ChestType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftChest;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class SupplyDrop {

    private SupplyDrop in;
    private Block groundBlock;
    private HashMap<Block, Object[]> replace = new HashMap<Block, Object[]>();

    public SupplyDrop(Block groundBlock) {
        this.in = this;
        this.groundBlock = groundBlock;
        if(this.groundBlock.getLocation().getBlockX() >= 0) {
            this.groundBlock.getLocation().setX(this.groundBlock.getLocation().getBlockX()+.5);
        } else {
            this.groundBlock.getLocation().setX(this.groundBlock.getLocation().getBlockX()-.5);
        }
        if(this.groundBlock.getLocation().getBlockZ() >= 0) {
            this.groundBlock.getLocation().setZ(this.groundBlock.getLocation().getBlockZ() + .5);
        } else {
            this.groundBlock.getLocation().setZ(this.groundBlock.getLocation().getBlockZ() - .5);
        }
        replace.put(groundBlock, new Object[]{groundBlock.getType(), groundBlock.getData()});
        groundBlock.setType(Material.BEACON);
        for(int x=-1; x<=1; x++) {
            for(int z=-1; z<=1; z++) {
                Block temp = groundBlock.getRelative(x, -1, z);
                replace.put(temp, new Object[]{temp.getType(), temp.getData()});
                temp.setType(Material.IRON_BLOCK);
            }
        }
        groundBlock.getWorld().strikeLightningEffect(groundBlock.getLocation());
        start();
    }

    private void start() {
        new BukkitRunnable() {
            int ticks = 0;
            double i = 0;
            Location location = groundBlock.getLocation().clone().add(0, 40, 0);
            public void run() {
                Location spiral_1 = location.clone();
                Location spiral_2 = location.clone();
                double radius_1 = 2.3D;
                double radius_2 = 2.3D;
                for(int step = 0; step < 70; step += 4) {
                    double difference = (2 * Math.PI) / 30;
                    double angle = (step * difference) + i;
                    org.bukkit.util.Vector vector = new org.bukkit.util.Vector(Math.cos(angle) * radius_1, 0,
                            Math.sin(angle) * radius_1);
                    Location tempLocation = spiral_1.add(vector);
                    ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 1, tempLocation, 60);
                    spiral_1.subtract(vector);
                    spiral_1.add(0, 0.12d, 0);
                    radius_1 -= 0.044f;
                }
                for(int step = 0; step < 70; step += 4) {
                    double difference = (2 * Math.PI) / 30;
                    double angle = (step * difference) + i + 3.5;
                    org.bukkit.util.Vector vector = new org.bukkit.util.Vector(Math.cos(angle) * radius_2, 0,
                            Math.sin(angle) * radius_2);
                    Location tempLocation = spiral_2.add(vector);
                    ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 1, tempLocation, 60);
                    spiral_2.subtract(vector);
                    spiral_2.add(0, 0.12d, 0);
                    radius_2 -= 0.044f;
                }
                ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.ICE, (byte) 0), 1, 1, 1, 0, 7, location.clone(), 60);
                i += 0.05;
                if(location.getY() > groundBlock.getLocation().getY()) {
                    location.getWorld().playSound(location, Sound.FIZZ, 1, 1);
                    location = location.subtract(0, 0.3, 0);
                } else {
                    this.cancel();
                    location.getWorld().playSound(groundBlock.getRelative(BlockFace.UP).getLocation(), Sound.EXPLODE, 5, 1);
                    location.getWorld().playSound(groundBlock.getRelative(BlockFace.UP).getLocation(), Sound.ANVIL_LAND, 5, 1);
                    ParticleEffect.EXPLOSION_LARGE.display(1, 1, 1, 0, 5, groundBlock.getRelative(BlockFace.UP).getLocation(), 60);
                    ParticleEffect.CLOUD.display(1, 1, 1, 0, 5, groundBlock.getRelative(BlockFace.UP).getLocation(), 60);
                    groundBlock.getRelative(BlockFace.UP).setType(Material.CHEST);
                    ((CraftChest) groundBlock.getRelative(BlockFace.UP).getState()).getTileEntity().a("Supply Drop");
                    ChestFiller.fillByType(((Chest) groundBlock.getRelative(BlockFace.UP).getState()).getInventory(),
                            ChestType.TIER_2);
                    reset();
                }
                ticks++;
            }
        }.runTaskTimer(FrostedGames.getInstance(), 0, 1L);
    }

    private void reset() {
        for(Map.Entry<Block, Object[]> entry : replace.entrySet()) {
            entry.getKey().setType((Material) entry.getValue()[0]);
            entry.getKey().setData((Byte) entry.getValue()[1]);
        }
        replace.clear();
    }
}