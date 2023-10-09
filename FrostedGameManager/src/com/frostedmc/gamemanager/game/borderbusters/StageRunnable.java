package com.frostedmc.gamemanager.game.borderbusters;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.WorldBorderUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class StageRunnable extends BukkitRunnable {

    private int totalTicks = 160;
    private int ticks = 0;
    private int size = 80;
    private Block center;
    private HashMap<Block, Object[]> replace = new HashMap<Block, Object[]>();

    public StageRunnable() {
        this.totalTicks-=(10*((BorderBusters) GameManager.getInstance().getCurrentGame()).stage);
        this.center = ((BorderBusters) GameManager.getInstance().getCurrentGame())
                .possibleLocations.get(new Random().nextInt(((BorderBusters) GameManager.getInstance().getCurrentGame()).possibleLocations.size()));
        center = center.getRelative(BlockFace.DOWN);
        replace.put(center, new Object[]{center.getType(), center.getData()});
        center.setType(Material.BEACON);
        for(int x=-1; x<=1; x++) {
            for(int z=-1; z<=1; z++) {
                Block temp = center.getRelative(x, -1, z);
                replace.put(temp, new Object[]{temp.getType(), temp.getData()});
                temp.setType(Material.IRON_BLOCK);
            }
        }
        new WorldBorderUtils(((CraftWorld) center.getWorld()).getHandle().getWorldBorder());
        ((CraftWorld) center.getWorld()).getHandle().getWorldBorder().setCenter(Math.floor(center.getX()), Math.floor(center.getZ()));
        ((CraftWorld) center.getWorld()).getHandle().getWorldBorder().setSize(size);
        ((CraftWorld) center.getWorld()).getHandle().getWorldBorder().setDamageAmount(1);
        ((CraftWorld) center.getWorld()).getHandle().getWorldBorder().setDamageBuffer(0.5);
        // Long | 1 second = 1000
        // Ticks | 1 second = 20
        // 20 ticks => 1000 long
        ((CraftWorld) center.getWorld()).getHandle().getWorldBorder().transitionSizeBetween(size, 1, totalTicks*40);
        this.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    @Override
    public void run() {
        if(ticks >= totalTicks) {
            this.cancel();
            for(Map.Entry<Block, Object[]> entry : replace.entrySet()) {
                entry.getKey().setType((Material) entry.getValue()[0]);
                entry.getKey().setData((Byte) entry.getValue()[1]);
            }
            replace.clear();
            ((CraftWorld) center.getWorld()).getHandle().getWorldBorder().setSize(size);
            ((BorderBusters) GameManager.getInstance().getCurrentGame()).stage++;
            GameRunnable.instance.countdown = 8;
        } else {
            ticks++;
        }
        double percent = (100D-(((double) ticks/(double) totalTicks)*100D));
        setXP(Double.valueOf(percent).floatValue());
    }

    private void setXP(float percent) {
        float xp = ((percent % 100) / 100);
        int level = ((BorderBusters) GameManager.getInstance().getCurrentGame()).stage;
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.setLevel(level);
            player.setExp(xp);
        }
    }
}