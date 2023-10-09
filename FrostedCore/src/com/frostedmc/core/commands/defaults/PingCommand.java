package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class PingCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "ping";
    }

    @Override
    public String commandDescription() {
        return "Shows your current ping to the server.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        player.sendMessage(PredefinedMessage.PING_COMMAND_PLAYER.registerPlaceholder("%ping%", getPing(player)).build());
    }

    private String getPing(Player player) {
        return "" + ((CraftPlayer) player).getHandle().ping;
    }
}