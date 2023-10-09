package com.frostedmc.kingdoms.commands;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.kingdoms.FrostedKingdoms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * Created by Redraskal_2 on 1/27/2017.
 */
public class IcicleTransferCommand extends Command {

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "icicletransfer";
    }

    @Override
    public String commandDescription() {
        return "Transfer your icicles into kingdoms balance.";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        new ChestGUI(player, 27, "Kingdoms Icicles Transfer", false, new GUICallback() {
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
                    gui.i.setItem(4, ItemCreator.getInstance().createItem(Material.NETHER_STAR, 1, 0, "&e" + player.getName(), Collections.singletonList("&bIcicles > " + accountDetails.getIcicles())));
                    gui.i.setItem(19, ItemCreator.getInstance().createItem(Material.GOLD_NUGGET, 1, 0, "&b500 Icicles &7-> &6$1250", Collections.singletonList("&7Transfer 500 icicles into $1250 Kingdoms money")));
                    gui.i.setItem(21, ItemCreator.getInstance().createItem(Material.IRON_INGOT, 1, 0, "&b1000 Icicles &7-> &6$2500", Collections.singletonList("&7Transfer 1000 icicles into $2500 Kingdoms money")));
                    gui.i.setItem(23, ItemCreator.getInstance().createItem(Material.DIAMOND, 1, 0, "&b2500 Icicles &7-> &6$5650", Collections.singletonList("&7Transfer 2500 icicles into $5650 Kingdoms money")));
                    gui.i.setItem(25, ItemCreator.getInstance().createItem(Material.EMERALD, 1, 0, "&b5000 Icicles &7-> &6$12500", Collections.singletonList("&7Transfer 5000 icicles into $12500 Kingdoms money")));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    AccountDetails accountDetails = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId());
                                    if(d.equalsIgnoreCase("500 Icicles -> $1250")) {
                                        if(accountDetails.getIcicles() >= 500) {
                                            Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                                    new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                                            (accountDetails.getIcicles()-500)));
                                            FrostedKingdoms.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                                    "eco give " + player.getName() + " 1250");
                                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                        } else {
                                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                                        }
                                    }
                                    if(d.equalsIgnoreCase("1000 Icicles -> $2500")) {
                                        if(accountDetails.getIcicles() >= 1000) {
                                            Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                                    new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                                            (accountDetails.getIcicles()-1000)));
                                            FrostedKingdoms.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                                    "eco give " + player.getName() + " 2500");
                                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                        } else {
                                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                                        }
                                    }
                                    if(d.equalsIgnoreCase("2500 Icicles -> $5650")) {
                                        if(accountDetails.getIcicles() >= 2500) {
                                            Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                                    new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                                            (accountDetails.getIcicles()-2500)));
                                            FrostedKingdoms.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                                    "eco give " + player.getName() + " 5650");
                                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                        } else {
                                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                                        }
                                    }
                                    if(d.equalsIgnoreCase("5000 Icicles -> $12500")) {
                                        if(accountDetails.getIcicles() >= 5000) {
                                            Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                                    new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                                            (accountDetails.getIcicles()-5000)));
                                            FrostedKingdoms.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(),
                                                    "eco give " + player.getName() + " 12500");
                                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                        } else {
                                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            public void onSecond(ChestGUI gui) {}
        }, FrostedKingdoms.getInstance());
    }
}