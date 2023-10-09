package com.frostedmc.fabrication.gui;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.gui.anvil.AnvilEvent;
import com.frostedmc.core.gui.anvil.AnvilGUI;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.fabrication.Fabrication;
import com.frostedmc.fabrication.api.FreshCoalAPI;
import com.frostedmc.fabrication.api.SkullResult;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class HeadQueryGUI {

    public HeadQueryGUI(Player player, Toolbox instance) {
        player.closeInventory();
        new AnvilGUI(player, ItemCreator.getInstance().createItem(Material.PAPER, 1, 0, "Search"), new AnvilEvent() {
            @Override
            public void onItemClick(Player player, String input) {
                if(!input.isEmpty() && !input.equalsIgnoreCase(" ")) {
                    new BukkitRunnable() {
                        public void run() {
                            try {
                                player.sendMessage(PredefinedMessage.QUERY_PROGRESS.build());
                                SkullResult[] skullResults = FreshCoalAPI.getInstance().query(ChatColor.stripColor(input));
                                new HeadGUI(player, skullResults, instance);
                            } catch (Exception e) {
                                e.printStackTrace();
                                player.sendMessage(PredefinedMessage.ERROR_IN_QUERY.build());
                            }
                        }
                    }.runTaskAsynchronously(Fabrication.getInstance());
                }
            }
        }, Fabrication.getInstance());
    }
}