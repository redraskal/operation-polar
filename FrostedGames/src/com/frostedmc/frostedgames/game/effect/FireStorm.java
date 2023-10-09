package com.frostedmc.frostedgames.game.effect;

import com.frostedmc.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class FireStorm {

    public FireStorm(Player target) {
        for(Block around : Utils.getBlocksInRadius(target.getLocation(), 5, true)) {
            if(around.getLocation().getBlockY() == target.getLocation().getBlockY()) {
                FallingBlock fallingblock = around.getWorld()
                        .spawnFallingBlock(around.getLocation().add(0, 10, 0), Material.FIRE, (byte) 0);
            }
        }
        for(Block around : Utils.getBlocksInRadius(target.getLocation(), 5, false)) {
            if(around.getLocation().getBlockY() == target.getLocation().getBlockY()) {
                Arrow arrow = around.getWorld()
                        .spawn(around.getLocation().add(0, 10, 0), Arrow.class);
                arrow.setCritical(true);
                arrow.setFireTicks(200000);
            }
        }
    }
}