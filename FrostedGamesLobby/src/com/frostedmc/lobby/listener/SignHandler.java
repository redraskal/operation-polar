package com.frostedmc.lobby.listener;

import com.frostedmc.core.utils.MessageChannel;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Redraskal_2 on 1/23/2017.
 */
public class SignHandler implements Listener {

    @EventHandler
    public void onSignClick(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK
                || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock() != null) {
                if(event.getClickedBlock().getType() == Material.WALL_SIGN) {
                   Sign sign = (Sign) event.getClickedBlock().getState();
                   String[] lines = sign.getLines();
                    if(!lines[0].isEmpty()) {
                        String server = lines[0].replace("[", "");
                        server = server.replace("]", "");
                        if(!server.isEmpty()) {
                            if(!server.equalsIgnoreCase("---")) {
                                if(!lines[1].isEmpty()) {
                                    if(!lines[1].equalsIgnoreCase("---")
                                            && !lines[1].equalsIgnoreCase("Restarting")) {
                                        MessageChannel.getInstance().send(event.getPlayer(), server);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
