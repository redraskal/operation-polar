package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 3/1/2017.
 */
public class OptionsGUI {

    public OptionsGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 54, "Options", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(10, ItemCreator.getInstance().createItem(Material.PAPER, 1, 0, "&b&lTOGGLE CHAT MESSAGES"));
                    gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.EMPTY_MAP, 1, 0, "&b&lTOGGLE PRIVATE MESSAGES"));
                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.SKULL_ITEM, 1, 4, "&b&lTOGGLE FRIEND REQUESTS"));
                    gui.i.setItem(16, ItemCreator.getInstance().createItem(Material.BOOK_AND_QUILL, 1, 0, "&b&lTOGGLE PARTY REQUESTS"));

                    setToggleItem(gui, player, "options_chat", "CHAT MESSAGES", 19);
                    setToggleItem(gui, player, "options_pm", "PRIVATE MESSAGES", 21);
                    setToggleItem(gui, player, "options_friend_requests", "FRIEND REQUESTS", 23);
                    setToggleItem(gui, player, "options_party_requests", "PARTY REQUESTS", 25);

                    gui.i.setItem(38, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 15, "&b"));
                    gui.i.setItem(40, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lPROFILE"));
                    if(Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getRank() != Rank.PLAYER) {
                        gui.i.setItem(42, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&lNEXT PAGE >"));
                    } else {
                        gui.i.setItem(42, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 15, "&b"));
                    }
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    if(d.equalsIgnoreCase("enable chat messages")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_chat", 0);
                                        StatisticsCache.getInstance().update(player, "options_chat");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable chat messages")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_chat", 1);
                                        StatisticsCache.getInstance().update(player, "options_chat");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("enable private messages")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_pm", 0);
                                        StatisticsCache.getInstance().update(player, "options_pm");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable private messages")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_pm", 1);
                                        StatisticsCache.getInstance().update(player, "options_pm");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("enable friend requests")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_friend_requests", 0);
                                        StatisticsCache.getInstance().update(player, "options_friend_requests");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable friend requests")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_friend_requests", 1);
                                        StatisticsCache.getInstance().update(player, "options_friend_requests");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("enable party requests")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_party_requests", 0);
                                        StatisticsCache.getInstance().update(player, "options_party_requests");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable party requests")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_party_requests", 1);
                                        StatisticsCache.getInstance().update(player, "options_party_requests");
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("NEXT PAGE >")) {
                                        player.closeInventory();
                                        openPage2(player, javaPlugin);
                                    }
                                    if(d.equalsIgnoreCase("PROFILE")) {
                                        player.closeInventory();
                                        new ProfileGUI(player, javaPlugin);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    private void setToggleItem(ChestGUI gui, Player player, String option, String title, int slot) {
        boolean enabled = (StatisticsCache.getInstance().get(player, option) == 0);
        if(enabled) {
            gui.i.setItem(slot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 10, "&c&lDISABLE " + title));
        } else {
            gui.i.setItem(slot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&a&lENABLE " + title));
        }
    }

    private void setToggleItem(ChestGUI gui, Player player, String option, String title, int slot, boolean invert) {
        boolean enabled = (StatisticsCache.getInstance().get(player, option) == 0);
        if(invert) enabled = !enabled;
        if(enabled) {
            gui.i.setItem(slot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 10, "&c&lDISABLE " + title));
        } else {
            gui.i.setItem(slot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&a&lENABLE " + title));
        }
    }

    private void openPage2(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 54, "Options", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.FIREWORK, 1, 0, "&b&lTOGGLE DOUBLE JUMP"));
                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.GOLD_NUGGET, 1, 0, "&b&lTOGGLE CHAT MENTIONS"));

                    setToggleItem(gui, player, "options_double_jump", "DOUBLE JUMP", 21);
                    setToggleItem(gui, player, "options_mentions", "CHAT MENTIONS", 23, true);

                    gui.i.setItem(38, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&l< PREVIOUS PAGE"));
                    gui.i.setItem(40, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lPROFILE"));
                    if(Rank.compare(Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getRank(), Rank.HELPER)) {
                        gui.i.setItem(42, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&lNEXT PAGE >"));
                    } else {
                        gui.i.setItem(42, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 15, "&b"));
                    }
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    if(d.equalsIgnoreCase("enable double jump")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_double_jump", 0);
                                        StatisticsCache.getInstance().update(player, "options_double_jump");
                                        player.closeInventory();
                                        openPage2(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                        if(javaPlugin.getServer().getPluginManager().isPluginEnabled("Hub")) {
                                            player.setFlying(false);
                                        }
                                    }
                                    if(d.equalsIgnoreCase("disable double jump")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_double_jump", 1);
                                        StatisticsCache.getInstance().update(player, "options_double_jump");
                                        player.closeInventory();
                                        openPage2(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                        if(javaPlugin.getServer().getPluginManager().isPluginEnabled("Hub")) {
                                            player.teleport(player.getLocation().clone().add(0, 1, 0));
                                            player.setFlying(true);
                                        }
                                    }
                                    if(d.equalsIgnoreCase("enable chat mentions")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_mentions", 1);
                                        StatisticsCache.getInstance().update(player, "options_mentions");
                                        player.closeInventory();
                                        openPage2(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable chat mentions")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_mentions", 0);
                                        StatisticsCache.getInstance().update(player, "options_mentions");
                                        player.closeInventory();
                                        openPage2(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("< PREVIOUS PAGE")) {
                                        player.closeInventory();
                                        new OptionsGUI(player, javaPlugin);
                                    }
                                    if(d.equalsIgnoreCase("NEXT PAGE >")) {
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                    }
                                    if(d.equalsIgnoreCase("PROFILE")) {
                                        player.closeInventory();
                                        new ProfileGUI(player, javaPlugin);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    private void openPage3(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 54, "Options", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(11, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.POTION, 1, 8206, "&b&lTOGGLE GLOBAL VANISH")));
                    gui.i.setItem(13, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.DIAMOND_SWORD, 1, 0, "&b&lTOGGLE GLACIER REPORTS")));
                    gui.i.setItem(15, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.IRON_CHESTPLATE, 1, 0, "&b&lTOGGLE PLAYER REPORTS")));

                    setToggleItem(gui, player, "options_global_vanish", "GLOBAL VANISH", 20, true);
                    setToggleItem(gui, player, "options_glacier_reports", "GLACIER REPORTS", 22, true);
                    setToggleItem(gui, player, "options_player_reports", "PLAYER REPORTS", 24, true);

                    gui.i.setItem(38, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&l< PREVIOUS PAGE"));
                    gui.i.setItem(40, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lPROFILE"));
                    gui.i.setItem(42, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 15, "&b"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    if(d.equalsIgnoreCase("enable global vanish")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_global_vanish", 1);
                                        StatisticsCache.getInstance().update(player, "options_global_vanish");
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable global vanish")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_global_vanish", 0);
                                        StatisticsCache.getInstance().update(player, "options_global_vanish");
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("enable glacier reports")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_glacier_reports", 1);
                                        StatisticsCache.getInstance().update(player, "options_glacier_reports");
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable glacier reports")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_glacier_reports", 0);
                                        StatisticsCache.getInstance().update(player, "options_glacier_reports");
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("enable player reports")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_player_reports", 1);
                                        StatisticsCache.getInstance().update(player, "options_player_reports");
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("disable player reports")) {
                                        Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(), "options_player_reports", 0);
                                        StatisticsCache.getInstance().update(player, "options_player_reports");
                                        player.closeInventory();
                                        openPage3(player, javaPlugin);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 5, 1);
                                    }
                                    if(d.equalsIgnoreCase("< PREVIOUS PAGE")) {
                                        player.closeInventory();
                                        openPage2(player, javaPlugin);
                                    }
                                    if(d.equalsIgnoreCase("PROFILE")) {
                                        player.closeInventory();
                                        new ProfileGUI(player, javaPlugin);
                                    }
                                }
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