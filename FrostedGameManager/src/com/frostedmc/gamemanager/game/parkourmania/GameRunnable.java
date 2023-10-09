package com.frostedmc.gamemanager.game.parkourmania;

import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.Title;
import com.frostedmc.core.utils.Utils;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Lighting;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.commands.GodModeCommand;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by Redraskal_2 on 3/18/2017.
 */
public class GameRunnable extends BukkitRunnable {

    public GameRunnable() {
        this.runTaskTimer(GameManager.getInstance(), 0, 3L);
        Title title = new Title("Generating map", "One moment...", 1, 1, 1);
        title.setTitleColor(ChatColor.DARK_AQUA);
        title.setSubtitleColor(ChatColor.AQUA);
        title.broadcast();
        new BukkitRunnable() {
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(SpectatorMode.getInstance().contains(player)) continue;
                    Location start = ((ParkourMania) GameManager.getInstance().getCurrentGame())
                            .startLocations.get(player.getUniqueId()).clone();
                    if(GodModeCommand.usernames.contains(player.getName())) {
                        int current = 0;
                        int[] data = new int[]{14, 1, 4, 5, 9, 10};
                        for(int z=0; z>-100; z--) {
                            Block temp = start.clone().getBlock().getRelative(0, 0, z);
                            if(temp.getType() == Material.AIR) {
                                if(new Random().nextBoolean()) {
                                    int result = new Random().nextInt(3);
                                    if(result == 0) {
                                        temp.setType(Material.GOLD_BLOCK);
                                    }
                                    if(result == 1) {
                                        temp.setType(Material.EMERALD_BLOCK);
                                    }
                                    if(result == 2) {
                                        temp.setType(Material.GOLD_BLOCK);
                                    }
                                    if(result == 3) {
                                        temp.setType(Material.IRON_BLOCK);
                                    }
                                } else {
                                    if(current >= data.length) {
                                        current = 0;
                                        temp.setType(Material.WOOL);
                                        temp.setData((byte) data[current]);
                                    } else {
                                        temp.setType(Material.WOOL);
                                        temp.setData((byte) data[current]);
                                        current++;
                                    }
                                }
                            }
                        }
                    } else {
                        for(int i=3; i<100; i+=3) {
                            generate(player, start.clone().subtract(0, 0, i).add(0, 2, 0));
                        }
                    }
                }
                Title title = new Title("", "❄ Parkour Mania ❄", 0, 4, 0);
                title.setSubtitleColor(ChatColor.DARK_AQUA);
                title.broadcast();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                    ChatUtils.sendBlockMessage("Parkour Mania", new String[]{
                            "&bMake your way over to the diamond",
                            "&bplatform, dodging any barriers in",
                            "&byour way!"
                    }, player);
                }
                new BukkitRunnable() {
                    public void run() {
                        new BukkitRunnable() {
                            int countdown = 3;
                            public void run() {
                                if(countdown <= 0) {
                                    this.cancel();
                                    Move.dontAllow.clear();
                                    for(Player player : Bukkit.getOnlinePlayers()) {
                                        player.setLevel(0);
                                        player.setExp(0);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
                                    }
                                    Title title = new Title("", "Go!", 0, 1, 0);
                                    title.setSubtitleColor(ChatColor.GREEN);
                                    title.broadcast();
                                } else {
                                    if(countdown == 3) {
                                        for(Player player : Bukkit.getOnlinePlayers()) {
                                            player.setLevel(3);
                                            player.setExp(0.9f);
                                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        }
                                        Title title = new Title("", "3...", 0, 1, 0);
                                        title.setSubtitleColor(ChatColor.RED);
                                        title.broadcast();
                                    }
                                    if(countdown == 2) {
                                        for(Player player : Bukkit.getOnlinePlayers()) {
                                            player.setLevel(2);
                                            player.setExp(0.6f);
                                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        }
                                        Title title = new Title("", "2...", 0, 1, 0);
                                        title.setSubtitleColor(ChatColor.YELLOW);
                                        title.broadcast();
                                    }
                                    if(countdown == 1) {
                                        for(Player player : Bukkit.getOnlinePlayers()) {
                                            player.setLevel(1);
                                            player.setExp(0.3f);
                                            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        }
                                        Title title = new Title("", "1...", 0, 1, 0);
                                        title.setSubtitleColor(ChatColor.DARK_GREEN);
                                        title.broadcast();
                                    }
                                    countdown--;
                                }
                            }
                        }.runTaskTimer(GameManager.getInstance(), 0, 20L);
                    }
                }.runTaskLater(GameManager.getInstance(), 40L);
            }
        }.runTaskLater(GameManager.getInstance(), 60L);
        for(int x=-10; x<(10*Bukkit.getOnlinePlayers().size()); x++) {
            new Location(((ParkourMania) GameManager.getInstance().getCurrentGame()).map,
                    x, 33, -100).getBlock().setType(Material.DIAMOND_BLOCK);
            new Location(((ParkourMania) GameManager.getInstance().getCurrentGame()).map,
                    x, 33, -101).getBlock().setType(Material.DIAMOND_BLOCK);
        }
    }

    @Override
    public void run() {
        if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!SpectatorMode.getInstance().contains(player)) {
                    double distance = player.getLocation()
                            .distance(((ParkourMania) GameManager.getInstance().getCurrentGame())
                                    .startLocations.get(player.getUniqueId()));
                    if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                        ScoreboardManager.getInstance().playerBoards.get(player)
                                .add("&fDistance: &a"
                                        + new DecimalFormat("#.#").format(distance) + " blocks", 5);
                        ScoreboardManager.getInstance().playerBoards.get(player).update();
                    }
                } else {
                    if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                        ScoreboardManager.getInstance().playerBoards.get(player)
                                .add("&fDistance: &a--- blocks", 5);
                        ScoreboardManager.getInstance().playerBoards.get(player).update();
                    }
                }
            }
        } else {
            this.cancel();
        }
    }

    private Block getClosestBlock(Location location) {
        Block temp = null;
        for(Block near : Utils.getBlocksInRadius(location, 5, false)) {
            if(near.getType() != Material.AIR) {
                if(temp == null) {
                    near = temp;
                } else {
                    if(near.getLocation().distance(location) < temp.getLocation().distance(location)) {
                        temp = near;
                    }
                }
            }
        }
        if(temp == null) {
            return location.getBlock();
        } else {
            return temp;
        }
    }

    private void generate(Player player, Location startLocation) {
        Block otherBlock = getClosestBlock(startLocation);
        int dis = (2+new Random().nextInt(2));
        boolean ladder = (new Random().nextInt(5) >= 4);
        boolean slab = (!ladder && new Random().nextInt(5) == 0);
        startLocation.setZ(startLocation.getZ()-dis);
        int dif = (-2+new Random().nextInt(2));
        startLocation.setY(startLocation.getY()+dif);
        if(dis > 3 && !ladder) {
            startLocation.setY(startLocation.getY()-1);
        }
        if(ladder) {
            startLocation.setY(startLocation.getY()+new Random().nextInt(2));
        }
        if(otherBlock != null && otherBlock != startLocation.getBlock()) {
            if(otherBlock.getLocation().getY() - startLocation.getY() <= 1) {
                if(ladder) {
                    startLocation.setY(startLocation.getY()-1);
                    if(Math.abs(otherBlock.getZ() - startLocation.getZ()) >= 4) {
                        startLocation.setZ(startLocation.getZ()-1);
                    }
                } else {
                    startLocation.setY(startLocation.getY()-2);
                }
            }
            if(slab) {
                if(otherBlock.getZ() - startLocation.getZ() < 0) {
                    startLocation.setZ(startLocation.getZ()-1);
                }
            }
            if(otherBlock.getLocation().getY() == startLocation.getY()) {
                if(new Random().nextBoolean()) {
                    if(new Random().nextInt(3) == 2) {
                        startLocation.setY(startLocation.getY()+new Random().nextInt(1));
                    } else {
                        startLocation.setY(startLocation.getY()-new Random().nextInt(1));
                    }
                }
            }
        }
        if(ladder) {
            if(dis >= 4) {
                startLocation.setZ(startLocation.getZ()+2);
            }
        }
        if(ladder) {
            startLocation.getBlock().getRelative(BlockFace.NORTH).setType(Material.STAINED_CLAY);
            setAsLadder(startLocation.getBlock());
        } else {
            if(!slab) {
                startLocation.getBlock().setType(Material.STAINED_CLAY);
            } else {
                startLocation.getBlock().setTypeIdAndData(Material.STONE_SLAB2.getId(), (byte) 10, true);
            }
        }
        Lighting.createLight(startLocation, 6);
    }

    private void setAsLadder(Block block) {
        block.setTypeIdAndData(Material.LADDER.getId(), (byte) 3, false);
    }
}
