package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/7/2016.
 */
public class GameSelectorGUI {

    public GameSelectorGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "Game Selector", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(11, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.IRON_SWORD, 1, 0, "&b&lArenaPVP", Utils.convert(new String[]{
                            "&eBattle to the death in a haven",
                            "&eof arenas.",
                            "&b",
                            " &a&lJoin a lobby",
                            "&7&o0 players online",
                    }))));

                    gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.SNOW_BALL, 1, 0, "&b&lFrosted Games", Utils.convert(new String[]{
                            "&eA frostified version of",
                            "&ethe Hunger Games.",
                            "&b",
                            " &a&lJoin a lobby",
                            "&7&o0 players online",
                    })));

                    gui.i.setItem(13, ItemCreator.getInstance().createItem(Material.SMOOTH_BRICK, 1, 3, "&c&lComing Soon", Utils.convert(new String[]{
                            "&eThis gamemode is coming soon.",
                    })));
                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.SMOOTH_BRICK, 1, 3, "&c&lComing Soon", Utils.convert(new String[]{
                            "&eThis gamemode is coming soon.",
                    })));

                    gui.i.setItem(15, ItemCreator.getInstance().createItem(Material.SMOOTH_BRICK, 1, 3, "&c&lComing Soon", Utils.convert(new String[]{
                            "&eThis gamemode is coming soon.",
                    })));

                    gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.WATCH, 1, 0, "&b&lHub Servers", Utils.convert(new String[]{
                            "&eView the hub server listing.",
                            "&b",
                            " &a&lJoin a lobby",
                            "&7&o0 players online",
                    })));

                    gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.BEACON, 1, 0, "&b&lKingdoms", Utils.convert(new String[]{
                            "&eThis gamemode is coming soon.",
                            "&b",
                            " &a&lJoin the game",
                            "&7&o0 players online",
                    })));

                    gui.i.setItem(32, ItemCreator.getInstance().createSkull(1, "CV_", "&b&lCV's Thinking House™", Utils.convert(new String[]{
                            "&eThis server is coming soon.",
                            "&b",
                            " &a&lJoin the server",
                            "&7&o0 players online",
                    })));
                }

                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                        if(!d.isEmpty()) {
                            if(d.equalsIgnoreCase("Hub Servers")) {
                                player.closeInventory();
                                new LoadingGUI(player, javaPlugin);
                            }
                            if(d.equalsIgnoreCase("ArenaPVP")) {
                                player.closeInventory();
                                MessageChannel.getInstance().Switch(player, player.getName(), "ArenaPVP-");
                            }
                            if(d.equalsIgnoreCase("Frosted Games")) {
                                player.closeInventory();
                                MessageChannel.getInstance().Switch(player, player.getName(), "FGL-");
                            }
                            if(d.equalsIgnoreCase("CV's Thinking House™")) {
                                player.closeInventory();
                                MessageChannel.getInstance().Switch(player, player.getName(), "Thinking-");
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }
}
