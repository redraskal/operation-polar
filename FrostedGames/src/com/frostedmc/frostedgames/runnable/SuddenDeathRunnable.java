package com.frostedmc.frostedgames.runnable;

import com.frostedmc.core.utils.Utils;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.CustomDamage;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 1/22/2017.
 */
public class SuddenDeathRunnable extends BukkitRunnable {

    public SuddenDeathRunnable() {
        InternalGameSettings.deathmatch = true;
        Bukkit.getWorld("deathmatch").setTime(16000);
        int currentspawn = 0;
        Location[] spawnpoints = new Location[]{
                new Location(Bukkit.getWorld("deathmatch"), 32.5, 71.0, 22.5, 86.6f, 2.2f),
                new Location(Bukkit.getWorld("deathmatch"), 7.5, 71.0, 23.5, -92f, 4.5f),
                new Location(Bukkit.getWorld("deathmatch"), 17.5, 71.0, 10.5, .2f, 1.8f),
                new Location(Bukkit.getWorld("deathmatch"), 20.5, 71.0, 35.5, 178f, 1.6f)
        };
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(InternalGameSettings.districtMap.containsKey(player)) {
                Location spawnpoint = null;
                if(spawnpoints.length >= currentspawn) {
                    spawnpoint = spawnpoints[currentspawn];
                } else {
                    spawnpoint = spawnpoints
                            [new Random().nextInt(spawnpoints.length)];
                }
                player.teleport(spawnpoint);
                currentspawn++;
            } else {
                player.teleport(new Location(Bukkit.getWorld("deathmatch"), 25.5, 76.0, 32.5, 142.8f, 18.3f));
                player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 5, 1);
            }
        }
        this.runTaskTimer(FrostedGames.getInstance(), 0, 20L);
    }

    private int countdown = 59;
    private int radius = 23;

    @Override
    public void run() {
        if(InternalGameSettings.status == Status.ENDED) {
            this.cancel();
            return;
        }
        if(countdown <= 0) {
            this.cancel();
            InternalGameSettings.status = Status.ENDED;
            for(Player player : InternalGameSettings.districtMap.keySet()) {
                CustomDamage.handleDeath(player, null, "Sudden Death");
            }
        } else {
            String co = "";
            if(countdown < 10) {
                co = "0" + countdown;
            } else {
                co = "" + countdown;
            }
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                    ScoreboardManager.getInstance().playerBoards.get(player)
                            .add("&fSudden Death: &a00&f:&a" + co, 5);
                    ScoreboardManager.getInstance().playerBoards.get(player).update();
                }
            }
            if(countdown % 4 == 0) {
                if(radius >= 6) {
                    for(Block block :
                            Utils.getBlocksInRadius(new Location(Bukkit.getWorld("deathmatch"), 17, 69, 21), radius, true)) {
                        if(block.getType().isSolid()) {
                            if(block.getType() != Material.AIR) {
                                FallingBlock fallingBlock = block.getWorld()
                                        .spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                                fallingBlock.setDropItem(false);
                                block.setType(Material.AIR);
                            }
                        }
                    }
                    radius--;
                }
            }
            countdown--;
        }
    }
}