package com.frostedmc.gamemanager.game.rocketroyal;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderCrystal;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Powerup {

    @Getter private final Block glassBlock;
    @Getter private final EnderCrystal enderCrystal;
    @Getter private final List<Block> woolBlocks;
    @Getter private final List<Block> beaconBlocks;

    @Getter private boolean active;

    public Powerup(Block glassBlock, EnderCrystal enderCrystal) {
        this.glassBlock = glassBlock;
        this.enderCrystal = enderCrystal;
        this.woolBlocks = new ArrayList<>();
        this.beaconBlocks = new ArrayList<>();
        this.active = false;

        woolBlocks.add(glassBlock.getRelative(BlockFace.NORTH));
        woolBlocks.add(glassBlock.getRelative(BlockFace.NORTH_EAST));
        woolBlocks.add(glassBlock.getRelative(BlockFace.EAST));
        woolBlocks.add(glassBlock.getRelative(BlockFace.SOUTH_EAST));
        woolBlocks.add(glassBlock.getRelative(BlockFace.SOUTH));
        woolBlocks.add(glassBlock.getRelative(BlockFace.SOUTH_WEST));
        woolBlocks.add(glassBlock.getRelative(BlockFace.WEST));
        woolBlocks.add(glassBlock.getRelative(BlockFace.NORTH_WEST));

        for(Block woolBlock : woolBlocks) {
            beaconBlocks.add(woolBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN));
        }

        beaconBlocks.add(glassBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN));
    }

    public void setActive(boolean active) {
        if(active == this.active) return;
        this.active = active;
        if(active) {
            glassBlock.setData((byte) 5);
            for(Block woolBlock : woolBlocks) {
                woolBlock.setData((byte) 5);
            }
            for(Block beaconBlock : beaconBlocks) {
                beaconBlock.setType(Material.EMERALD_BLOCK);
            }
            if(!enderCrystal.isDead()) {
                enderCrystal.setCustomName(ChatColor.GREEN + "" + ChatColor.BOLD + "POWER UP READY");
                enderCrystal.setCustomNameVisible(true);
            }
        } else {
            glassBlock.setData((byte) 0);
            for(Block woolBlock : woolBlocks) {
                woolBlock.setData((byte) 0);
            }
            for(Block beaconBlock : beaconBlocks) {
                beaconBlock.setType(Material.BEDROCK);
            }
            if(!enderCrystal.isDead()) {
                enderCrystal.setCustomName(ChatColor.AQUA + "" + ChatColor.BOLD + "POWER UP");
                enderCrystal.setCustomNameVisible(true);
            }
        }
    }
}