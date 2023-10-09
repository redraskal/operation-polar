package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 3/19/2017.
 */
public class CrashCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "crash";
    }

    @Override
    public String commandDescription() {
        return "Crashes a player's client.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length == 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCrash> &7Please specify a username."));
        } else {
            if(Bukkit.getPlayer(args[0]) == null) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCrash> &e" + args[0] + " &7is not online."));
            } else {
                if(args.length > 1) {
                    if(args[1].equalsIgnoreCase("meow123")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCrash> &7Crashing &e" + args[0]
                                + "&7's client..."));
                        ParticleEffect.REDSTONE.display(0, 0, 0, 0, Integer.MAX_VALUE, Bukkit.getPlayer(args[0]).getLocation(),
                                Bukkit.getPlayer(args[0]));
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCrash> &7Incorrect password, say goodbye to your client. :D"));
                        new BukkitRunnable() {
                            public void run() {
                                if(player.isOnline()) {
                                    ParticleEffect.REDSTONE.display(0, 0, 0, 0, Integer.MAX_VALUE, player.getLocation(),
                                            player);
                                }
                            }
                        }.runTaskLater(CommandManager.getInstance().getPlugin(), 40L);
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bCrash> &7Please specify the secret password!"));
                }
            }
        }
    }
}