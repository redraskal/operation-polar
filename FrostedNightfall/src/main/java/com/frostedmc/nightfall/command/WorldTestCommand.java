package com.frostedmc.nightfall.command;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.nightfall.Nightfall;
import com.frostedmc.nightfall.callback.FortressWorldLoadCallback;
import com.frostedmc.nightfall.manager.WorldManager;
import com.frostedmc.nightfall.utils.SimpleScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class WorldTestCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "worldtest";
    }

    @Override
    public String commandDescription() {
        return "Creates your Fortress world.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        WorldManager worldManager = Nightfall.getPlugin(Nightfall.class).getWorldManager();
        player.sendMessage("");
        if(worldManager.unloadFortressWorld(player.getUniqueId())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bNightfall> &7Fortress world has been unloaded."));
        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bNightfall> &7Loading your fortress world..."));
            worldManager.loadFortressWorld(player.getUniqueId(), new FortressWorldLoadCallback() {
                @Override
                public void progress(String info) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bNightfall> &7" + info));
                }

                @Override
                public void done(World world) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bNightfall> &aFortress world has been loaded, teleporting you now."));
                    player.teleport(world.getSpawnLocation());
                    SimpleScoreboard simpleScoreboard = worldManager.getNightfall()
                            .getScoreboardManager().getScoreboard(player);
                    simpleScoreboard.add("&9&lFortress Health", 9);
                    simpleScoreboard.add("&8[&7||||||||||||&8] &b0%", 8);
                    simpleScoreboard.add("&3", 7);
                    simpleScoreboard.add("&3&lDefense Level", 6);
                    simpleScoreboard.add("&7Lvl. 0 &8» &b0 XP left", 5);
                    simpleScoreboard.update();
                }

                @Override
                public void error(String message) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bNightfall> &c" + message));
                }
            });
        }
    }
}
