package com.frostedmc.kingdoms.structure;

import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 11/13/2016.
 */
public class SchematicBuilder extends BukkitRunnable {

    private JavaPlugin javaPlugin;
    private Schematic schematic;
    private Location startLocation;

    private int currentLayer;
    private int delay;
    private int blocksLeft;
    private int largeCounter;

    /**
     * Builds a schematic with magical animation powers.
     * @param javaPlugin
     * @param schematic
     * @param startLocation
     * @param startLayer
     */
    public SchematicBuilder(JavaPlugin javaPlugin, Schematic schematic, Location startLocation, int startLayer) {
        this.javaPlugin = javaPlugin;
        this.schematic = schematic;
        this.startLocation = startLocation;
        this.currentLayer = startLayer;
        this.blocksLeft = schematic.getBlocks().length;
        this.largeCounter = 0;
    }

    @Override
    public void run() {
        if(this.currentLayer >= schematic.getHeight()) {
            this.cancel();
        } else {
            this.pasteLayer();
        }
    }

    private void pasteLayer() {
        this.delay = 0;
        for(int z=0; z<schematic.getLength(); z++) {
            for(int x=0; x<schematic.getWidth(); x++) {
                Location pasteLocation = this.startLocation.clone().add(x, this.currentLayer, z);
                int index = x + (this.currentLayer * schematic.getLength() + z) * schematic.getWidth();
                if(Material.getMaterial(new Byte(schematic.getBlocks()[index]).intValue()) != Material.AIR) {
                    if(this.currentLayer == 0) {
                        try {
                            build(pasteLocation, index);
                        } catch (Exception e) {}
                    } else {
                        if(this.largeCounter >= 8) {
                            this.largeCounter = 0;
                            this.delay+=1;
                            try {
                                build(pasteLocation, index);
                            } catch (Exception e) {}
                        } else {
                            new BukkitRunnable() {
                                public void run() {
                                    try {
                                        build(pasteLocation, index);
                                    } catch (Exception e) {}
                                }
                            }.runTaskLater(this.javaPlugin, delay);
                            this.largeCounter++;
                        }
                    }
                }

            }
        }
        this.currentLayer++;

        new BukkitRunnable() {
            public void run() {
                if(currentLayer >= schematic.getHeight()) {
                    cancelBuild();
                } else {
                    pasteLayer();
                }
            }
        }.runTaskLater(this.javaPlugin, delay);
    }

    private void build(Location pasteLocation, int index) {
        // Used DarkBladee12's amazing particle api class here
        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.getMaterial(new Byte(schematic.getBlocks()[index]).intValue()),
                schematic.getData()[index]), 1, 1, 1, 0, 15, pasteLocation, 30);
        pasteLocation.getWorld().playSound(pasteLocation, Soundinizer.get(Material.getMaterial(new Byte(schematic.getBlocks()[index]).intValue())), 10f, 1f);
        pasteLocation.getBlock().setTypeIdAndData(schematic.getBlocks()[index], schematic.getData()[index], false);
        blocksLeft--;
    }

    public void cancelBuild() {
        this.cancel();
    }
}