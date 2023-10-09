package com.frostedmc.hub.gui;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.Timestamp;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.api.account.statistics.StatisticProfile;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.CustomSkull;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.hub.Hub;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class FrostyGUI {

    public FrostyGUI(Player player) {
        new ChestGUI(player, 45, "Frosty", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    Object[] dailyBonus = fetchDailyBonus(player.getUniqueId());
                    Object[] monthlyBonus = fetchMonthlyBonus(player.getUniqueId());
                    Object[] survey = fetchSurvey(player.getUniqueId());
                    gui.i.setItem(10, ItemCreator.getInstance().createItem((Material) dailyBonus[1], 1, 0,
                            "&b&lDaily Bonus", Arrays.asList((String[]) dailyBonus[2])));
                    gui.i.setItem(13, ItemCreator.getInstance().createItem((Material) monthlyBonus[1], 1, 0,
                            "&b&lMonthly Bonus", Arrays.asList((String[]) monthlyBonus[2])));
                    gui.i.setItem(16, ItemCreator.getInstance().createItem((Material) survey[1], 1, 0,
                            "&b&lVote on Poll", Arrays.asList((String[]) survey[2])));
                    gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.PAPER, 1, 0,
                            "&b&lVote for Icicles", Arrays.asList(new String[]{
                                    "&7",
                                    "&7&oReceive 100 icicles per vote!",
                                    "&7&oClick to see the links in chat.",
                                    "&7",
                                    "&6Rewards »",
                                    "  &7100 icicles/vote"
                            })));
                    gui.i.setItem(32, CustomSkull.getInstance().create("&b&lUse the Treasure Trove",
                            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODg4YzkwNmIzOWQ0ZmJlNDk0NjY4OThlNjdhYWRmMWUxODIxZWZiNDg1MTA5NDc3ZjM5YzU2NDkyZWM5ZTE1ZSJ9fX0=",
                            Arrays.asList(new String[]{
                                    "&7",
                                    "&7&oThis feature is coming soon."
                            })
                    ));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null && item.getType() != Material.AIR
                            && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if(d.equalsIgnoreCase("Daily Bonus")) {
                            if(item.getType() != Material.REDSTONE) {
                                player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                                gui.i.setItem(10, ItemCreator.getInstance().createItem(Material.LAPIS_BLOCK, 1, 0,
                                        "&e&lProcessing..."));
                                Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(),
                                        "frosty_daily_bonus", 1);
                                AccountDetails accountDetails = Core.getInstance().getAccountManager()
                                        .parseDetails(player.getUniqueId());
                                Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                                new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                                        (accountDetails.getIcicles()
                                                                +dailyBonusRewards(accountDetails.getRank())[0])));
                                Core.getInstance().getStatisticsManager().add(player.getUniqueId(), "frosty_treasure_tokens", 1);
                                new BukkitRunnable() {
                                    public void run() {
                                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                        player.closeInventory();
                                        new FrostyGUI(player);
                                    }
                                }.runTaskLater(Hub.getInstance(), 10L);
                            } else {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                            }
                        }
                        if(d.equalsIgnoreCase("Monthly Bonus")) {
                            if(item.getType() != Material.REDSTONE) {
                                player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                                gui.i.setItem(13, ItemCreator.getInstance().createItem(Material.LAPIS_BLOCK, 1, 0,
                                        "&e&lProcessing..."));
                                Core.getInstance().getStatisticsManager().updateStat(player.getUniqueId(),
                                        "frosty_monthly_bonus", 1);
                                AccountDetails accountDetails = Core.getInstance().getAccountManager()
                                        .parseDetails(player.getUniqueId());
                                Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                        new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                                (accountDetails.getIcicles()
                                                        +monthlyBonusReward(accountDetails.getRank()))));
                                Core.getInstance().getStatisticsManager().add(player.getUniqueId(), "frosty_treasure_tokens", 1);
                                new BukkitRunnable() {
                                    public void run() {
                                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                        player.closeInventory();
                                        new FrostyGUI(player);
                                    }
                                }.runTaskLater(Hub.getInstance(), 10L);
                            } else {
                                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                            }
                        }
                        if(d.equalsIgnoreCase("Vote on Poll")) {
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                        }
                        if(d.equalsIgnoreCase("Vote for Icicles")) {
                            player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
                            player.closeInventory();
                            ChatUtils.sendBlockMessage("Voting for Icicles", new String[]{
                                    "&aVotes may take a while to register.",
                                    "&bhttps://vote.frostedmc.com/1",
                                    "&bhttps://vote.frostedmc.com/2",
                                    "&bhttps://vote.frostedmc.com/3",
                                    "&bhttps://vote.frostedmc.com/4",
                                    "&bhttps://vote.frostedmc.com/5"
                            }, player);
                        }
                    }
                }
            }
            @Override
            public void onSecond(ChestGUI gui) {}
        }, Hub.getInstance());
    }

    private int[] dailyBonusRewards(Rank rank) {
        if(rank == Rank.PLAYER) {
            return new int[]{200, 350};
        } else {
            int icicles = 200;
            if(Rank.compare(rank, Rank.VIP)) {
                icicles+=100;
            }
            if(Rank.compare(rank, Rank.ELITE)) {
                icicles+=50;
            }
            if(Rank.compare(rank, Rank.LEGEND)) {
                icicles+=50;
            }
            return new int[]{icicles, 350};
        }
    }

    private int monthlyBonusReward(Rank rank) {
        if(rank == Rank.PLAYER) {
            return 6200;
        } else {
            int icicles = 6200;
            if(Rank.compare(rank, Rank.VIP)) {
                icicles+=3200;
            }
            if(Rank.compare(rank, Rank.ELITE)) {
                icicles+=3200;
            }
            if(Rank.compare(rank, Rank.LEGEND)) {
                icicles+=3200;
            }
            return icicles;
        }
    }

    private Object[] fetchDailyBonus(UUID uuid) {
        boolean allow = false;
        Material material = Material.REDSTONE;
        List<String> lore = new ArrayList<String>();
        StatisticProfile statisticProfile = Core.getInstance().getStatisticsManager().fetchProfile(uuid);
        Rank rank = Core.getInstance().getAccountManager().parseDetails(uuid).getRank();
        if(statisticProfile.getStatistic("frosty_daily_bonus") == 1) {
            Date currentUpdate = Timestamp.getCurrentTimestamp().toDate();
            Date lastUpdate = statisticProfile.getLastUpdate("frosty_daily_bonus").toDate();
            Calendar tempCal = new GregorianCalendar();
            tempCal.setTime(lastUpdate);
            tempCal.add(Calendar.HOUR, 12);
            Timestamp end = new Timestamp(tempCal.getTime());
            if(currentUpdate.after(end.toDate())) {
                Core.getInstance().getStatisticsManager().updateStat(uuid, "frosty_daily_bonus", 0);
                allow = true;
            }
        } else {
            allow = true;
        }
        lore.add("&b");
        if(allow) {
            material = Material.GOLD_NUGGET;
            lore.add("&7&oClick to claim reward!");
        } else {
            lore.add("&7&oYou cannot claim this reward right now.");
        }
        int[] dailyBonusRewards = dailyBonusRewards(rank);
        lore.add("&b");
        lore.add("&6Rewards »");
        lore.add("  &7" + dailyBonusRewards[0] + " icicles");
        lore.add("  &71 Treasure Token");
        lore.add("  &7" + dailyBonusRewards[1] + " exp");
        return new Object[]{allow, material, lore.toArray(new String[lore.size()])};
    }

    private Object[] fetchMonthlyBonus(UUID uuid) {
        boolean allow = false;
        Material material = Material.REDSTONE;
        List<String> lore = new ArrayList<String>();
        StatisticProfile statisticProfile = Core.getInstance().getStatisticsManager().fetchProfile(uuid);
        Rank rank = Core.getInstance().getAccountManager().parseDetails(uuid).getRank();
        if(statisticProfile.getStatistic("frosty_monthly_bonus") == 1) {
            Date currentUpdate = Timestamp.getCurrentTimestamp().toDate();
            Date lastUpdate = statisticProfile.getLastUpdate("frosty_monthly_bonus").toDate();
            Calendar tempCal = new GregorianCalendar();
            tempCal.setTime(lastUpdate);
            tempCal.add(Calendar.MONTH, 1);
            Timestamp end = new Timestamp(tempCal.getTime());
            if(currentUpdate.after(end.toDate())) {
                Core.getInstance().getStatisticsManager().updateStat(uuid, "frosty_monthly_bonus", 0);
                allow = true;
            }
        } else {
            allow = true;
        }
        lore.add("&b");
        if(allow) {
            material = Material.NETHER_STAR;
            lore.add("&7&oClick to claim reward!");
        } else {
            lore.add("&7&oYou cannot claim this reward right now.");
        }
        int dailyBonusReward = monthlyBonusReward(rank);
        lore.add("&b");
        lore.add("&6Rewards »");
        lore.add("  &7" + dailyBonusReward + " icicles");
        lore.add("  &71 Treasure Token");
        return new Object[]{allow, material, lore.toArray(new String[lore.size()])};
    }

    private Object[] fetchSurvey(UUID uuid) {
        boolean allow = false;
        Material material = Material.BOOK;
        List<String> lore = new ArrayList<String>();
        lore.add("&b");
        if(allow) {
            material = Material.ENCHANTED_BOOK;
            lore.add("&7&oClick to enter poll!");
        } else {
            lore.add("&7&oNo polls are currently available.");
        }
        return new Object[]{allow, material, lore.toArray(new String[lore.size()])};
    }
}