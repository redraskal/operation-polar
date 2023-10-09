package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.report.ReportCategory;
import com.frostedmc.core.api.account.report.ReportDetails;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Redraskal_2 on 3/2/2017.
 */
public class PlayerReportGUI {

    private Map<ReportCategory, String> reasons = new HashMap<ReportCategory, String>();

    public PlayerReportGUI(Player player, String username, JavaPlugin javaPlugin) {
        UUID reporting = Core.getInstance().getUUIDFetcher().parseDetails(username);
        if(reporting == null) {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bReport> &7We could not locate &e" + username + "&7."));
            return;
        }
        if(Core.getInstance().getPlayerReportManager().parseDetails(reporting, player.getUniqueId()) != null) {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bReport> &7You already have an open report against &e" + username + "&7."));
            return;
        }
        openMainMenu(player, username, javaPlugin);
    }

    private void openMainMenu(Player player, String username, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "New Report | " + username, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(11, Utils.removeDefaultLores(
                            ItemCreator.getInstance()
                                    .createItem(Material.DIAMOND_SWORD, 1, 0, "&b&lHacking Category")));
                    gui.i.setItem(13, ItemCreator.getInstance()
                            .createItem(Material.PAPER, 1, 0, "&b&lChat Abuse Category"));
                    gui.i.setItem(15, ItemCreator.getInstance()
                            .createItem(Material.FISHING_ROD, 1, 0, "&b&lExploiting Category"));
                    gui.i.setItem(31, ItemCreator.getInstance()
                            .createItem(Material.BOOK_AND_QUILL, 1, 0, "&a&lSend Report"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() == Material.DIAMOND_SWORD) {
                            player.closeInventory();
                            openHackingCategory(player, username, javaPlugin);
                        }
                        if(item.getType() == Material.PAPER) {
                            player.closeInventory();
                            openChatAbuseCategory(player, username, javaPlugin);
                        }
                        if(item.getType() == Material.FISHING_ROD) {
                            player.closeInventory();
                            openExploitingCategory(player, username, javaPlugin);
                        }
                        if(item.getType() == Material.BOOK_AND_QUILL) {
                            if(reasons.isEmpty()) {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                            } else {
                                sendReport(player, username);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    private void openHackingCategory(Player player, String username, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "New Report | " + username, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    // 11 -> 15
                    // 20 -> 24
                    gui.i.setItem(11, createItem(ItemCreator.getInstance()
                            .createItem(Material.DIAMOND_SWORD, 1, 0, "&b&lKillAura"), ReportCategory.HACKING));
                    gui.i.setItem(12, createItem(ItemCreator.getInstance()
                            .createItem(Material.CARROT_STICK, 1, 0, "&b&lCriticals"), ReportCategory.HACKING));
                    gui.i.setItem(13, createItem(ItemCreator.getInstance()
                            .createItem(Material.BOW, 1, 0, "&b&lAimAssist"), ReportCategory.HACKING));
                    gui.i.setItem(14, createItem(ItemCreator.getInstance()
                            .createItem(Material.STONE, 1, 0, "&b&lFastPlace(Break)/Scaffold"), ReportCategory.HACKING));
                    gui.i.setItem(15, createItem(ItemCreator.getInstance()
                            .createItem(Material.LEASH, 1, 0, "&b&lHeadSnaps"), ReportCategory.HACKING));

                    gui.i.setItem(20, createItem(ItemCreator.getInstance()
                            .createItem(Material.DEAD_BUSH, 1, 0, "&b&lReach"), ReportCategory.HACKING));
                    gui.i.setItem(21, createItem(ItemCreator.getInstance()
                            .createItem(Material.MONSTER_EGG, 1, 59, "&b&lSpider"), ReportCategory.HACKING));
                    gui.i.setItem(22, createItem(ItemCreator.getInstance()
                            .createItem(Material.ENDER_PEARL, 1, 0, "&b&lSpeed"), ReportCategory.HACKING));
                    gui.i.setItem(23, createItem(ItemCreator.getInstance()
                            .createItem(Material.STRING, 1, 0, "&b&lFlight"), ReportCategory.HACKING));
                    gui.i.setItem(24, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lOther"), ReportCategory.HACKING));

                    gui.i.setItem(30, ItemCreator.getInstance()
                            .createItem(Material.BARRIER, 1, 0, "&e&lBack to categories"));
                    gui.i.setItem(32, ItemCreator.getInstance()
                            .createItem(Material.BOOK_AND_QUILL, 1, 0, "&a&lSend Report"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.hasItemMeta()) {
                            if(item.getItemMeta().hasDisplayName()) {
                                String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                if(item.getItemMeta().getDisplayName().startsWith("" + ChatColor.AQUA)) {
                                    if(contains(ReportCategory.HACKING, d)) {
                                        remove(ReportCategory.HACKING, d);
                                    } else {
                                        append(ReportCategory.HACKING, d);
                                    }
                                    player.closeInventory();
                                    openHackingCategory(player, username, javaPlugin);
                                }
                            }
                        }
                        if(item.getType() == Material.BARRIER) {
                            player.closeInventory();
                            openMainMenu(player, username, javaPlugin);
                        }
                        if(item.getType() == Material.BOOK_AND_QUILL) {
                            if(reasons.isEmpty()) {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                            } else {
                                sendReport(player, username);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    private void openChatAbuseCategory(Player player, String username, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "New Report | " + username, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    // 11 -> 15
                    // 20 -> 24
                    gui.i.setItem(11, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lSpam"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(12, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lCaps"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(13, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lRudeness"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(14, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lHackusations"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(15, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lArguing"), ReportCategory.CHAT_ABUSE));

                    gui.i.setItem(20, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lChat Trolling"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(21, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lImpersonation"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(22, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lMini-Modding"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(23, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lAdvertising"), ReportCategory.CHAT_ABUSE));
                    gui.i.setItem(24, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lOther"), ReportCategory.CHAT_ABUSE));

                    gui.i.setItem(30, ItemCreator.getInstance()
                            .createItem(Material.BARRIER, 1, 0, "&e&lBack to categories"));
                    gui.i.setItem(32, ItemCreator.getInstance()
                            .createItem(Material.BOOK_AND_QUILL, 1, 0, "&a&lSend Report"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.hasItemMeta()) {
                            if(item.getItemMeta().hasDisplayName()) {
                                String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                if(item.getItemMeta().getDisplayName().startsWith("" + ChatColor.AQUA)) {
                                    if(contains(ReportCategory.CHAT_ABUSE, d)) {
                                        remove(ReportCategory.CHAT_ABUSE, d);
                                    } else {
                                        append(ReportCategory.CHAT_ABUSE, d);
                                    }
                                    player.closeInventory();
                                    openChatAbuseCategory(player, username, javaPlugin);
                                }
                            }
                        }
                        if(item.getType() == Material.BARRIER) {
                            player.closeInventory();
                            openMainMenu(player, username, javaPlugin);
                        }
                        if(item.getType() == Material.BOOK_AND_QUILL) {
                            if(reasons.isEmpty()) {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                            } else {
                                sendReport(player, username);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    private void openExploitingCategory(Player player, String username, JavaPlugin javaPlugin) {
        new ChestGUI(player, 36, "New Report | " + username, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    // 11 -> 15
                    // 20 -> 24
                    gui.i.setItem(10, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lBug Exploiting"), ReportCategory.EXPLOITING));
                    gui.i.setItem(12, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lCompromised Account"), ReportCategory.EXPLOITING));
                    gui.i.setItem(14, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lBungeeCord Exploit"), ReportCategory.EXPLOITING));
                    gui.i.setItem(16, createItem(ItemCreator.getInstance()
                            .createItem(Material.EMPTY_MAP, 1, 0, "&b&lOther"), ReportCategory.EXPLOITING));

                    gui.i.setItem(21, ItemCreator.getInstance()
                            .createItem(Material.BARRIER, 1, 0, "&e&lBack to categories"));
                    gui.i.setItem(23, ItemCreator.getInstance()
                            .createItem(Material.BOOK_AND_QUILL, 1, 0, "&a&lSend Report"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.hasItemMeta()) {
                            if(item.getItemMeta().hasDisplayName()) {
                                String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                if(item.getItemMeta().getDisplayName().startsWith("" + ChatColor.AQUA)) {
                                    if(contains(ReportCategory.EXPLOITING, d)) {
                                        remove(ReportCategory.EXPLOITING, d);
                                    } else {
                                        append(ReportCategory.EXPLOITING, d);
                                    }
                                    player.closeInventory();
                                    openExploitingCategory(player, username, javaPlugin);
                                }
                            }
                        }
                        if(item.getType() == Material.BARRIER) {
                            player.closeInventory();
                            openMainMenu(player, username, javaPlugin);
                        }
                        if(item.getType() == Material.BOOK_AND_QUILL) {
                            if(reasons.isEmpty()) {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 5, 1);
                            } else {
                                sendReport(player, username);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    public ItemStack createItem(ItemStack current, ReportCategory reportCategory) {
        if(contains(reportCategory, ChatColor.stripColor(current.getItemMeta().getDisplayName()))) {
            current.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        current = Utils.removeDefaultLores(current);
        return current;
    }

    private boolean contains(ReportCategory reportCategory, String reason) {
        if(reasons.containsKey(reportCategory)) {
            for(String temp_reason : reasons.get(reportCategory).split(", ")) {
                if(temp_reason.equalsIgnoreCase(reason)) return true;
            }
        }
        return false;
    }

    private void append(ReportCategory reportCategory, String reason) {
        if(reasons.containsKey(reportCategory)) {
            reasons.put(reportCategory, (reasons.get(reportCategory)+", "+reason));
        } else {
            reasons.put(reportCategory, reason);
        }
    }

    private void remove(ReportCategory reportCategory, String reason) {
        if(reasons.containsKey(reportCategory)) {
            String temp = "";
            for(String temp_reason : reasons.get(reportCategory).split(", ")) {
                if(!temp_reason.equalsIgnoreCase(reason)) {
                    if(!temp.isEmpty()) temp+=", ";
                    temp+=temp_reason;
                }
            }
            reasons.put(reportCategory, temp);
            if(reasons.get(reportCategory).isEmpty()
                    || reasons.get(reportCategory).equalsIgnoreCase(", ")) {
                reasons.remove(reportCategory);
            }
        }
    }

    private void sendReport(Player player, String username) {
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&bReport> &7Your report against &e" + username + " &7has been filed!"));
        UUID uuid = Core.getInstance().getUUIDFetcher().parseDetails(username);
        Core.getInstance().getPlayerReportManager().register(
                new ReportDetails(uuid,
                        player.getUniqueId(), reasons));
        if(MessageChannel.getInstance() != null) {
            MessageChannel.getInstance().ReportCreate(player, uuid);
        }
        Utils.pushSlackMessage("xoxb-153245903298-pfYka3OsJTyYqFy2SJLw8Mji",
                "C4H760HNW", "*" + player.getName() + "* created a report against _" + username
                        + "_. View it with `/viewreport " + player.getName() + " " + username + "`");
    }
}