package com.frostedmc.arenapvp.gui;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.GameType;
import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.arenapvp.queue.Queue;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class UnrankedQueue {

    public UnrankedQueue(Player player) {
        new ChestGUI(player, 18, "Unranked Queue | Kit Selection", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    updateQueue(gui.i);
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.hasItemMeta()) {
                            if(item.getType() != Material.AIR) {
                                Kit kit = Kit.fromName(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                                if(Queue.addTo(player, kit, GameType.UNRANKED)) {
                                    player.closeInventory();
                                    player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 10, 1);
                                } else {
                                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onSecond(ChestGUI gui) {
                updateQueue(gui.i);
            }
        }, ArenaPVP.getInstance());
    }

    private void updateQueue(Inventory inventory) {
        int i = 0;
        for(Kit kit : Kit.values()) {
            List<String> lore = new ArrayList<String>();
            lore.add("&7Waiting: &f" + Queue.count(kit, GameType.UNRANKED));
            lore.add("&7Games: &f" + ArenaManager.getInstance().count(kit, GameType.UNRANKED));
            inventory.setItem(i, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(kit.getDisplayItem(), 1, 0, "&e" + kit.getName(), lore)));
            i++;
        }
    }
}