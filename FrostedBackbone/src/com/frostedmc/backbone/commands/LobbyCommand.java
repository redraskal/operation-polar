package com.frostedmc.backbone.commands;

import com.frostedmc.backbone.modules.KickModule;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/17/2017.
 */
public class LobbyCommand extends Command {

    private Map<UUID, Long> last = new HashMap<UUID, Long>();

    public LobbyCommand() {
        super("lobby", null, new String[]{"leave"});
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer)) return;
        if(last.containsKey(((ProxiedPlayer) commandSender).getUniqueId())) {
            if((System.currentTimeMillis() - last.get(((ProxiedPlayer) commandSender).getUniqueId()))
                    <= 5000L) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&6Network> &7Please wait a few seconds before executing this command again."));
                return;
            }
        }
        String find = "Hub-";
        if(((ProxiedPlayer) commandSender).getServer().getInfo().getName().startsWith("FG")) find = "FGL-";
        if(((ProxiedPlayer) commandSender).getServer().getInfo().getName().startsWith("ArenaPVP")) find = "ArenaPVP-";
        net.md_5.bungee.api.config.ServerInfo serverInfo =
                KickModule.findServer(((ProxiedPlayer) commandSender).getServer().getInfo().getName(), find);
        if(serverInfo == null) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7We could not locate a Lobby server at the current time."));
        } else {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7You are now being sent to &e" + serverInfo.getName() + "&7."));
            BungeeCord.getInstance().getPlayer(commandSender.getName())
                    .connect(serverInfo);
        }
        last.put(((ProxiedPlayer) commandSender).getUniqueId(), System.currentTimeMillis());
    }
}
