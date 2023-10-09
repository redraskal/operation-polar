package com.frostedmc.hub.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.core.utils.Utils;
import com.frostedmc.hub.manager.CustomPubSub;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GameSelectorGUI {

    public GameSelectorGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "Game Selector", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, GUICallback.CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    update(gui);
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                        if(!d.isEmpty()) {
                            if(d.equalsIgnoreCase("Hub Servers")) {
                                player.closeInventory();
                                new ServerViewGUI(player, "Hub Servers", "Hub-");
                            }
                            if(d.equalsIgnoreCase("ArenaPVP")) {
                                player.closeInventory();
                                MessageChannel.getInstance().Switch(player, player.getName(), "ArenaPVP-");
                            }
                            if(d.equalsIgnoreCase("Frosted Games MAINTENANCE")) {
                                player.closeInventory();
                                MessageChannel.getInstance().Switch(player, player.getName(), "FGL-");
                            }
                            if(d.equalsIgnoreCase("OITC FEATURED")) {
                                player.closeInventory();
                                new GameServerViewGUI(player, "OITC", "OITC-");
                            }
                            if(d.equalsIgnoreCase("Arcade")) {
                                player.closeInventory();
                                new GameServerViewGUI(player, "Arcade", "Arcade-");
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
            public void onSecond(ChestGUI gui) { update(gui); }
        }, javaPlugin);
    }

    private void update(ChestGUI gui) {
        gui.i.setItem(11, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.IRON_SWORD, 1, 0, "&b&lArenaPVP", Utils.convert(new String[]{
                "&eBattle to the death in a haven",
                "&eof arenas.",
                "&b",
                " &a&lJoin a lobby",
                "&7&o" + CustomPubSub.getPlayers("ArenaPVP") + " players online",
        }))));

        gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.SNOW_BALL, 1, 0, "&b&lFrosted Games &c&lMAINTENANCE", Utils.convert(new String[]{
                "&eA frostified version of",
                "&ethe Hunger Games.",
                "&b",
                " &a&lJoin a lobby",
                "&7&o" + (CustomPubSub.getPlayers("FGL")+CustomPubSub.getPlayers("FG")) + " players online",
        })));

        gui.i.setItem(13, ItemCreator.getInstance().createItem(Material.SKULL_ITEM, 1, 1, "&b&lArcade", Utils.convert(new String[]{
                "&eAn endless loop of",
                "&emadness.",
                "&b",
                " &a&lJoin a game",
                "&7&o" + (CustomPubSub.getPlayers("Arcade")) + " players online",
        })));

        gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.BOW, 1, 0, "&b&lOITC &6&lFEATURED", Utils.convert(new String[]{
                "&eYour mission is to eliminate",
                "&eenemies in one shot.",
                "&b",
                " &a&lJoin a game",
                "&7&o" + (CustomPubSub.getPlayers("OITC")) + " players online",
        })));

        gui.i.setItem(15, ItemCreator.getInstance().createItem(Material.SMOOTH_BRICK, 1, 3, "&c&lComing Soon", Utils.convert(new String[]{
                "&eThis gamemode is coming soon.",
        })));

        gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.WATCH, 1, 0, "&b&lHub Servers", Utils.convert(new String[]{
                "&eView the hub server listing.",
                "&b",
                " &a&lJoin a lobby",
                "&7&o" + CustomPubSub.getPlayers("Hub") + " players online",
        })));

        gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.BEACON, 1, 0, "&b&lKingdoms", Utils.convert(new String[]{
                "&eThis gamemode is coming soon.",
                "&b",
                " &a&lJoin the game",
                "&7&o0 players online",
        })));

        gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.TRAP_DOOR, 1, 0, "&b&lCV's Thinking House™", Utils.convert(new String[]{
                "&eThis server is coming soon.",
                "&b",
                " &a&lJoin the server",
                "&7&o" + CustomPubSub.getPlayers("Thinking") + " players online",
        })));
    }
}
