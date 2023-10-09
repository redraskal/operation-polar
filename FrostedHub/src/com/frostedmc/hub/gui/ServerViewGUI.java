package com.frostedmc.hub.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.hub.Hub;
import com.frostedmc.hub.manager.GamePubSub;
import com.frostedmc.hub.manager.GameStatus;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class ServerViewGUI {

    public ServerViewGUI(Player player, String title, String prefix) {
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
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {
                updateGUI(gui, prefix);
            }
        }, Hub.getInstance());
    }

    public void updateGUI(ChestGUI gui, String prefix) {
        gui.i.clear();
        if(GamePubSub.getArray(prefix).length == 0) {
            gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 14,
                    "&c&lNO SERVERS ARE AVAILABLE"));
            return;
        }
        for(GameStatus gameStatus : GamePubSub.getArray(prefix)) {
            if(gameStatus.getGameStatus() == 3) continue;
            DyeColor dyeColor = DyeColor.LIME;
            if((gameStatus.getMaxPlayers() - gameStatus.getOnlinePlayers()) <= 10) {
                dyeColor = DyeColor.RED;
            }
            if((gameStatus.getMaxPlayers() - gameStatus.getOnlinePlayers()) <= 30) {
                dyeColor = DyeColor.ORANGE;
            }
            if((gameStatus.getMaxPlayers() - gameStatus.getOnlinePlayers()) <= 50) {
                dyeColor = DyeColor.YELLOW;
            }
            gui.i.setItem(getSlot(gui), ItemCreator.getInstance().createBanner(Material.BANNER, dyeColor, 1, 0,
                    "&b&l" + gameStatus.getServer(), Arrays.asList(new String[]{
                            "&ePlayers » &f" + gameStatus.getOnlinePlayers() + "&7/&f" + gameStatus.getMaxPlayers(),
                            " &a&lJoin the server",
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
                    return 27;
                }
                if(current >= 34) {
                    return 34;
                }
                current++;
            }
        }
        return current;
    }
}