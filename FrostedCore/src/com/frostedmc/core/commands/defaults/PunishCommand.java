package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.gui.defaults.PunishGUI;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.ResultSet;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class PunishCommand extends Command {

    private JavaPlugin javaPlugin;

    public PunishCommand(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public Rank requiredRank() {
        return Rank.HELPER;
    }

    @Override
    public String commandLabel() {
        return "punish";
    }

    @Override
    public String commandDescription() {
        return "An advanced gui to punish people with!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length >= 2) {
            String reason = "";
            for(int i=1; i<args.length; i++) {
                if(reason.isEmpty()) {
                    reason+=args[i];
                } else {
                    reason+=" "+args[i];
                }
            }
            ResultSet resultSet = Core.getInstance().getAccountManager().queryAccountDetails(args[0]);
            try {
                if(resultSet.next()) {
                    new PunishGUI(player, args[0], reason, this.javaPlugin);
                    player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &e" + args[0] + " &7has not played on FrostedMC yet."));
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7Something went wrong."));
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
            }
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7Please specify a username and reason."));
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
        }
    }
}