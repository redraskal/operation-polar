package com.frostedmc.hub.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.hub.Hub;
import com.frostedmc.hub.manager.GamePubSub;
import com.frostedmc.hub.manager.GameStatus;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class GameServerViewGUI {

    public GameServerViewGUI(Player player, String title, String prefix) {
        new ChestGUI(player, 45, title, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    try {
                        updateGUI(gui, prefix);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null && item.getType() != Material.AIR
                            && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if(item.getType() == Material.BANNER) {
                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Network> &7You are now being sent to &e"
                                    + d + "&7."));
                            MessageChannel.getInstance().send(player, d);
                        }
                        if(d.equalsIgnoreCase("JOIN A GAME")) {
                            player.closeInventory();
                            join(player, prefix);
                        }
                        if(d.equalsIgnoreCase("SPECTATE A GAME")) {
                            player.closeInventory();
                            spectate(player, prefix);
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {
                updateGUI(gui, prefix);
            }
        }, Hub.getInstance());
    }

    public void join(Player player, String prefix) {
        GameStatus[] gameStatuses = GamePubSub.getArray(prefix);
        if(gameStatuses.length == 0) {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7We could not locate a game server at the current time."));
            return;
        }
        GameStatus current = null;
        for(GameStatus gameStatus : gameStatuses) {
            if(gameStatus.getGameStatus() <= 1) {
                if(current == null) {
                    if(gameStatus.getOnlinePlayers() != gameStatus.getMaxPlayers()) {
                        current = gameStatus;
                    }
                } else {
                    if(gameStatus.getOnlinePlayers() > current.getOnlinePlayers()) {
                        if(gameStatus.getOnlinePlayers() != gameStatus.getMaxPlayers()) {
                            current = gameStatus;
                        }
                    }
                }
            }
        }
        if(current != null) {
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7You are now being sent to &e"
                    + current.getServer() + "&7."));
            MessageChannel.getInstance().send(player, current.getServer());
        } else {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7We could not locate a game server at the current time."));
        }
    }

    public void spectate(Player player, String prefix) {
        GameStatus[] gameStatuses = GamePubSub.getArray(prefix);
        if(gameStatuses.length == 0) {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7We could not locate a game server at the current time."));
            return;
        }
        GameStatus current = null;
        for(GameStatus gameStatus : gameStatuses) {
            if(gameStatus.getGameStatus() >= 2) {
                if(current == null) {
                    if(gameStatus.getOnlinePlayers() != gameStatus.getMaxPlayers()) {
                        current = gameStatus;
                    }
                } else {
                    if(gameStatus.getOnlinePlayers() > current.getOnlinePlayers()) {
                        if(gameStatus.getOnlinePlayers() != gameStatus.getMaxPlayers()) {
                            current = gameStatus;
                        }
                    }
                }
            }
        }
        if(current != null) {
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7You are now being sent to &e"
                            + current.getServer() + "&7."));
            MessageChannel.getInstance().send(player, current.getServer());
        } else {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&6Network> &7We could not locate a game server at the current time."));
        }
    }

    public void updateGUI(ChestGUI gui, String prefix) {
        gui.i.clear();
        if(GamePubSub.getArray(prefix).length == 0) {
            gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 14,
                    "&c&lNO GAMES ARE AVAILABLE"));
            return;
        }
        gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.ENDER_PEARL, 1, 0, "&e&lJOIN A GAME"));
        gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&5&lSPECTATE A GAME"));
        for(GameStatus gameStatus : GamePubSub.getArray(prefix)) {
            if(gameStatus.getGameStatus() == 3) continue;
            DyeColor dyeColor = DyeColor.LIME;
            if(gameStatus.getGameStatus() == 1) {
                dyeColor = DyeColor.ORANGE;
            }
            if(gameStatus.getGameStatus() == 2) {
                dyeColor = DyeColor.PURPLE;
            }
            String status = "Waiting for players.";
            if(gameStatus.getGameStatus() == 1) {
                status = "Game is starting shortly.";
            }
            if(gameStatus.getGameStatus() == 2) {
                status = "In-game.";
            }
            gui.i.setItem(getSlot(gui), ItemCreator.getInstance().createBanner(Material.BANNER, dyeColor, 1, 0,
               "&b&l" + gameStatus.getServer(), Arrays.asList(new String[]{
                            "&ePlayers » &f" + gameStatus.getOnlinePlayers() + "&7/&f" + gameStatus.getMaxPlayers(),
                            "&eStatus » &f" + status,
                            " &a&lJoin the game",
                    })));
        }
    }

    public int getSlot(ChestGUI gui) {
        int current = 10;
        for(int i=current; i<gui.i.getSize(); i++) {
            if(gui.i.getItem(i) == null
                    || gui.i.getItem(i).getType() == Material.AIR) {
                return current;
            } else {
                if(current >= 16) {
                    current = 18;
                }
                if(current >= 25) {
                    return 25;
                }
                current++;
            }
        }
        return current;
    }
}