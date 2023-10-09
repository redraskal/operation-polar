package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class ProfileGUI {

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public ProfileGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "Profile", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    AccountDetails accountDetails = Core.getInstance().getAccountManager()
                            .parseDetails(player.getUniqueId());
                    String rank = "&7&lPLAYER";
                    if(accountDetails.getRank() != Rank.PLAYER) {
                        rank = accountDetails.getRank().getPrefix(false);
                    }
                    double formattedTime = accountDetails.getMinutesOnline();
                    String time = "";
                    if(accountDetails.getMinutesOnline() < 60) {
                        time = decimalFormat.format(formattedTime) + " minutes";
                    } else {
                        formattedTime = (formattedTime / 60);
                        if(formattedTime >= 24) {
                            formattedTime = (formattedTime / 24);
                            if(formattedTime >= 7) {
                                formattedTime = (formattedTime / 7);
                                if(formattedTime >= 3.5) {
                                    formattedTime = (formattedTime / 3.5);
                                    time = decimalFormat.format(formattedTime) + " months";
                                } else {
                                    time = decimalFormat.format(formattedTime) + " weeks";
                                }
                            } else {
                                time = decimalFormat.format(formattedTime) + " days";
                            }
                        } else {
                            time = decimalFormat.format(formattedTime) + " hours";
                        }
                    }
                    gui.i.setItem(13, ItemCreator.getInstance().createSkull(1, player.getName(), "&e&lGLOBAL STATISTICS", Arrays.asList(new String[]{
                            "&fRank » " + rank,
                            "&fIcicles » &7" + accountDetails.getIcicles(),
                            "&fTime Online » &7" + time
                    })));
                    gui.i.setItem(29, ItemCreator.getInstance().createItem(Material.ENCHANTED_BOOK, 1, 0, "&b&lACHIEVEMENTS"));
                    gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.WATCH, 1, 0, "&b&lGAME STATISTICS"));
                    gui.i.setItem(33, ItemCreator.getInstance().createItem(Material.REDSTONE_COMPARATOR, 1, 0, "&b&lOPTIONS"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null && item.getType() != Material.AIR
                            && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if(d.equalsIgnoreCase("game statistics")) {
                            player.closeInventory();
                            new StatisticsGUI(player, javaPlugin);
                        }
                        if(d.equalsIgnoreCase("options")) {
                            player.closeInventory();
                            new OptionsGUI(player, javaPlugin);
                        }
                    }
                }
            }
            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }
}