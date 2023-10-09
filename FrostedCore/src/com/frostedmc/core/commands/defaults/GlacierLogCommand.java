package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.glacier.Check;
import com.frostedmc.core.glacier.GlacierModule;
import com.frostedmc.core.messages.PredefinedMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;

/**
 * Created by Redraskal_2 on 11/21/2016.
 */
public class GlacierLogCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.ADMIN;
    }

    @Override
    public String commandLabel() {
        return "glog";
    }

    @Override
    public String commandDescription() {
        return "Exports a player's debug log for a certain check.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length < 3) {
            player.sendMessage(PredefinedMessage.NOT_ENOUGH_ARGUMENTS
                    .registerPlaceholder("%usage%", "/glog <username> <check> <file>")
                    .build());
        } else {
            try {
                Check check = GlacierModule.getInstance().getCheck(args[1]);
                new File(GlacierModule.getInstance().getPlugin().getDataFolder().getPath() + "/glacier_log/").mkdirs();
                check.exportLogs(Bukkit.getPlayer(args[0]).getUniqueId(),
                        new File(GlacierModule.getInstance().getPlugin().getDataFolder().getPath() + "/glacier_log/" + args[2]));
                player.sendMessage(PredefinedMessage.GLACIER_LOG_EXPORTED.build());
            } catch (Exception e) {
                player.sendMessage(PredefinedMessage.COMMAND_ERROR.build());
            }
        }
    }
}