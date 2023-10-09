package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.core.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 9/10/2016.
 */
public class BlizzardCommand extends Command {

    private boolean enabled = false;

    @Override
    public Rank requiredRank() {
        return Rank.DEV;
    }

    @Override
    public String commandLabel() {
        return "blizzard";
    }

    @Override
    public String commandDescription() {
        return "Change the world into a blizzard of fun!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(enabled) {
            player.sendMessage(PredefinedMessage.BLIZZARD_ENABLED.build());
            return;
        }

        enabled = true;
        player.sendMessage(PredefinedMessage.BLIZZARD_ENABLE.build());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&l* A blizzard appears on the horizon. *"));

        Location mainLocation = player.getLocation().clone();
        mainLocation.getWorld().playSound(mainLocation, Sound.AMBIENCE_THUNDER, 10f, 10f);
        mainLocation.getWorld().setStorm(true);

        new BukkitRunnable() {
            public void run() {
                mainLocation.getWorld().playSound(mainLocation, Sound.AMBIENCE_THUNDER, 10f, 8f);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b&l* Snow starts to fall *"));

                start(mainLocation);
            }
        }.runTaskLater(CommandManager.getInstance().getPlugin(), 80L);
    }

    private void start(Location mainLocation) {
        new BukkitRunnable() {
            int radius = 0;
            public void run() {
               for(Block block : Utils.getBlocksInRadius(mainLocation, radius, true)) {
                   if(block.getType() != Material.AIR) {
                       for(Player player : Bukkit.getOnlinePlayers()) {
                           player.sendBlockChange(block.getLocation(), Material.SNOW_BLOCK, (byte) 0);
                       }
                   }
               }

                radius++;
            }
        }.runTaskTimer(CommandManager.getInstance().getPlugin(), 0, 10L);

        new BukkitRunnable() {
            public void run() {
                new BukkitRunnable() {
                    int radius = 0;
                    public void run() {
                        for(Block block : Utils.getBlocksInRadius(mainLocation, radius, true)) {
                            if(block.getType() != Material.AIR) {
                                for(Player player : Bukkit.getOnlinePlayers()) {
                                    player.sendBlockChange(block.getLocation(), Material.PACKED_ICE, (byte) 0);
                                }
                            }
                        }

                        radius++;
                    }
                }.runTaskTimer(CommandManager.getInstance().getPlugin(), 0, 20L);
            }
        }.runTaskLater(CommandManager.getInstance().getPlugin(), 80L);
    }
}