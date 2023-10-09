package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.statistics.StatisticProfile;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 1/27/2017.
 */
public class StatisticsGUI {

    private DecimalFormat df = new DecimalFormat("#.##");

    public StatisticsGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 36, "Player Statistics", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.IRON_SWORD, 1, 0, "&b&lARENA PVP"));
                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.SNOW_BALL, 1, 0, "&b&lFROSTED GAMES"));
                    gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lPROFILE"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    if(d.equalsIgnoreCase("ARENA PVP")) {
                                        player.closeInventory();
                                        openArenaPVP(player, javaPlugin);
                                    }
                                    if(d.equalsIgnoreCase("FROSTED GAMES")) {
                                        player.closeInventory();
                                        openFrostedGames(player, javaPlugin);
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

    private void openFrostedGames(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 36, "Player Statistics", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    List<String> combatLore = new ArrayList<String>();
                    StatisticProfile statisticProfile = Core.getInstance().getStatisticsManager()
                            .fetchProfile(player.getUniqueId());

                    double kills = statisticProfile.getStatistic("frostedgames_kills");
                    double deaths = statisticProfile.getStatistic("frostedgames_deaths");
                    double wins = statisticProfile.getStatistic("frostedgames_wins");
                    if(kills == 0 || deaths == 0) {
                        combatLore.add("&fK/D Ratio » &7---");
                    } else {
                        combatLore.add("&fK/D Ratio » &7" + df.format((kills / deaths)));
                    }
                    if(wins == 0 || deaths == 0) {
                        combatLore.add("&fW/L Ratio » &7---");
                    } else {
                        combatLore.add("&fW/L Ratio » &7" + df.format((wins / deaths)));
                    }
                    combatLore.add("&fTotal Kills » &7" + ((int) kills));
                    combatLore.add("&fTotal Deaths » &7" + ((int) deaths));
                    combatLore.add("&fTotal Wins » &7" + ((int) wins));

                    gui.i.setItem(13, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.DIAMOND_SWORD, 1, 0, "&6&lCOMBAT", combatLore)));
                    gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lMAIN MENU"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    if(d.equalsIgnoreCase("MAIN MENU")) {
                                        player.closeInventory();
                                        new StatisticsGUI(player, javaPlugin);
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

    private void openArenaPVP(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 36, "Player Statistics", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    List<String> rankedLore = new ArrayList<String>();
                    List<String> unrankedLore = new ArrayList<String>();
                    StatisticProfile statisticProfile = Core.getInstance().getStatisticsManager()
                            .fetchProfile(player.getUniqueId());

                    double rankedWins = statisticProfile.getStatistic("arenapvp_wins_ranked");
                    double rankedDeaths = statisticProfile.getStatistic("arenapvp_deaths_ranked");
                    if(rankedWins == 0 || rankedDeaths == 0) {
                        rankedLore.add("&fK/D Ratio » &7---");
                    } else {
                        rankedLore.add("&fK/D Ratio » &7" + df.format((rankedWins / rankedDeaths)));
                    }
                    rankedLore.add("&fTotal Kills » &7" + ((int) rankedWins));
                    rankedLore.add("&fTotal Deaths » &7" + ((int) rankedDeaths));

                    double unrankedWins = statisticProfile.getStatistic("arenapvp_wins_unranked");
                    double unrankedDeaths = statisticProfile.getStatistic("arenapvp_deaths_unranked");
                    if(unrankedWins == 0 || unrankedDeaths == 0) {
                        unrankedLore.add("&fK/D Ratio » &7---");
                    } else {
                        unrankedLore.add("&fK/D Ratio » &7" + df.format((unrankedWins / unrankedDeaths)));
                    }
                    unrankedLore.add("&fTotal Kills » &7" + ((int) unrankedWins));
                    unrankedLore.add("&fTotal Deaths » &7" + ((int) unrankedDeaths));

                    gui.i.setItem(12, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.DIAMOND_SWORD, 1, 0, "&6&lRANKED COMBAT", rankedLore)));
                    gui.i.setItem(14, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.STONE_SWORD, 1, 0, "&e&lUNRANKED COMBAT", unrankedLore)));
                    gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lMAIN MENU"));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        if(item.getType() != Material.AIR) {
                            if(item.hasItemMeta()) {
                                if(item.getItemMeta().hasDisplayName()) {
                                    String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                                    if(d.equalsIgnoreCase("MAIN MENU")) {
                                        player.closeInventory();
                                        new StatisticsGUI(player, javaPlugin);
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