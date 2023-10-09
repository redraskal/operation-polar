package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.Timestamp;
import com.frostedmc.core.api.account.punish.PunishmentRecord;
import com.frostedmc.core.api.account.punish.PunishmentRecordGUI;
import com.frostedmc.core.api.account.punish.PunishmentSeverity;
import com.frostedmc.core.api.account.punish.PunishmentType;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class PunishGUI {

    private Map<Integer, PunishmentType> punishmentTypeMap = new HashMap<Integer, PunishmentType>();
    private Map<Integer, PunishmentSeverity> punishmentSeverityMap = new HashMap<Integer, PunishmentSeverity>();
    private Map<Integer, Integer> punishmentLengthMap = new HashMap<Integer, Integer>();

    public PunishGUI(Player player, String punishing, String reason, JavaPlugin javaPlugin) {
        punishmentTypeMap.put(20, PunishmentType.TEMP_MUTE);
        punishmentTypeMap.put(21, PunishmentType.TEMP_MUTE);
        punishmentTypeMap.put(22, PunishmentType.TEMP_MUTE);
        punishmentTypeMap.put(23, PunishmentType.TEMP_MUTE);
        punishmentTypeMap.put(24, PunishmentType.PERM_MUTE);

        punishmentTypeMap.put(29, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(30, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(31, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(32, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(33, PunishmentType.PERM_BAN);

        punishmentTypeMap.put(38, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(39, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(40, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(41, PunishmentType.TEMP_BAN);
        punishmentTypeMap.put(42, PunishmentType.PERM_BAN);

        punishmentSeverityMap.put(20, PunishmentSeverity.SEVERITY_1);
        punishmentSeverityMap.put(21, PunishmentSeverity.SEVERITY_2);
        punishmentSeverityMap.put(22, PunishmentSeverity.SEVERITY_3);
        punishmentSeverityMap.put(23, PunishmentSeverity.SEVERITY_4);
        punishmentSeverityMap.put(24, PunishmentSeverity.SEVERITY_5);

        punishmentSeverityMap.put(29, PunishmentSeverity.SEVERITY_1);
        punishmentSeverityMap.put(30, PunishmentSeverity.SEVERITY_2);
        punishmentSeverityMap.put(31, PunishmentSeverity.SEVERITY_3);
        punishmentSeverityMap.put(32, PunishmentSeverity.SEVERITY_4);
        punishmentSeverityMap.put(33, PunishmentSeverity.SEVERITY_5);

        punishmentSeverityMap.put(38, PunishmentSeverity.SEVERITY_1);
        punishmentSeverityMap.put(39, PunishmentSeverity.SEVERITY_2);
        punishmentSeverityMap.put(40, PunishmentSeverity.SEVERITY_3);
        punishmentSeverityMap.put(41, PunishmentSeverity.SEVERITY_4);
        punishmentSeverityMap.put(42, PunishmentSeverity.SEVERITY_5);

        punishmentLengthMap.put(20, 2);
        punishmentLengthMap.put(21, 24);
        punishmentLengthMap.put(22, (24*3));
        punishmentLengthMap.put(23, (24*7));
        punishmentLengthMap.put(24, 1000);

        punishmentLengthMap.put(29, 2);
        punishmentLengthMap.put(30, 24);
        punishmentLengthMap.put(31, ((24*7)*3));
        punishmentLengthMap.put(32, (24*31));
        punishmentLengthMap.put(33, 1000);

        punishmentLengthMap.put(38, 24);
        punishmentLengthMap.put(39, 24*7);
        punishmentLengthMap.put(40, (24*31));
        punishmentLengthMap.put(41, ((24*31)*3));
        punishmentLengthMap.put(42, 1000);

        new ChestGUI(player, 54, "Punish | " + punishing, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(12, ItemCreator.getInstance().createSkull(1, punishing, "&e" + punishing, Arrays.asList(new String[]{
                            "&7Reason: &f" + reason,
                            "&7Timestamp: &f" + Timestamp.getCurrentTimestamp().toString()
                    })));
                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.EYE_OF_ENDER, 1, 0, "&ePast Record"));

                    gui.i.setItem(19, ItemCreator.getInstance().createItem(Material.BOOK_AND_QUILL, 1, 0, "&eChat Offences"));
                    gui.i.setItem(20, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 3, "&eSeverity 1",
                            Arrays.asList(new String[]{
                                "&72 Hour Mute"
                    })));
                    gui.i.setItem(21, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 4, "&eSeverity 2",
                            Arrays.asList(new String[]{
                                    "&71 Day Mute"
                            })));
                    gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 11, "&eSeverity 3",
                            Arrays.asList(new String[]{
                                    "&73 Day Mute"
                            })));
                    gui.i.setItem(23, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 14, "&eSeverity 4",
                            Arrays.asList(new String[]{
                                    "&71 Week Mute"
                            })));
                    gui.i.setItem(24, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 1, "&eSeverity 5",
                            Arrays.asList(new String[]{
                                    "&7Permanent Mute"
                            })));

                    gui.i.setItem(28, ItemCreator.getInstance().createItem(Material.BOOK, 1, 0, "&eGeneral Offences"));
                    gui.i.setItem(29, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 3, "&eSeverity 1",
                            Arrays.asList(new String[]{
                                    "&72 Hour Ban"
                            })));
                    gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 4, "&eSeverity 2",
                            Arrays.asList(new String[]{
                                    "&71 Day Ban"
                            })));
                    gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 11, "&eSeverity 3",
                            Arrays.asList(new String[]{
                                    "&73 Week Ban"
                            })));
                    gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 14, "&eSeverity 4",
                            Arrays.asList(new String[]{
                                    "&71 Month Ban"
                            })));
                    gui.i.setItem(33, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 1, "&eSeverity 5",
                            Arrays.asList(new String[]{
                                    "&7Permanent Ban"
                            })));

                    gui.i.setItem(34, ItemCreator.getInstance().createItem(Material.PAPER, 1, 0, "&eIssue a warning"));

                    gui.i.setItem(37, Utils.removeDefaultLores(ItemCreator.getInstance().createItem(Material.IRON_SWORD, 1, 0, "&eHacking Offences")));
                    gui.i.setItem(38, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 3, "&eSeverity 1",
                            Arrays.asList(new String[]{
                                    "&71 Day Ban"
                            })));
                    gui.i.setItem(39, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 4, "&eSeverity 2",
                            Arrays.asList(new String[]{
                                    "&71 Week Ban"
                            })));
                    gui.i.setItem(40, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 11, "&eSeverity 3",
                            Arrays.asList(new String[]{
                                    "&71 Month Ban"
                            })));
                    gui.i.setItem(41, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 14, "&eSeverity 4",
                            Arrays.asList(new String[]{
                                    "&73 Month Ban"
                            })));
                    gui.i.setItem(42, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 1, "&eSeverity 5",
                            Arrays.asList(new String[]{
                                    "&7Permanent Ban"
                            })));
                }
                if(callback == CallbackType.CLICK) {
                    if(item != null && item.getType() != Material.AIR && item.hasItemMeta()) {
                        if(item.getItemMeta().hasDisplayName()) {
                            String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                            if(d.equalsIgnoreCase("Past Record")) {
                                player.closeInventory();
                                new PunishmentRecordGUI(player, punishing,
                                        Core.getInstance().getAccountManager().parseDetails(punishing).getUUID(), 1, true, javaPlugin).rE = reason;
                            }
                            if(d.equalsIgnoreCase("Issue a warning")) {
                                player.closeInventory();
                                Core.getInstance().getPunishmentManager().register(new PunishmentRecord(Core.getInstance().getAccountManager().parseDetails(punishing).getUUID(),
                                        PunishmentType.WARN, PunishmentSeverity.SEVERITY_1,
                                        Timestamp.getCurrentTimestamp(), Timestamp.getCurrentTimestamp(), player.getUniqueId(), reason));
                                MessageChannel.getInstance().sendMessage(player, punishing, "&4Punish> &7You have been warned by &e" + player.getName() + " &7for &f" + reason);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7A warning has been sent to &e" + punishing + "&7."));
                            }
                            if(item.getType() == Material.INK_SACK) {
                                PunishmentType punishmentType = punishmentTypeMap.get(gui.la);
                                PunishmentSeverity punishmentSeverity = punishmentSeverityMap.get(gui.la);
                                int hours = punishmentLengthMap.get(gui.la);
                                Calendar tempCal = new GregorianCalendar();
                                tempCal.setTime(Timestamp.getCurrentTimestamp().toDate());
                                tempCal.add(Calendar.HOUR, hours);
                                Timestamp end = new Timestamp(tempCal.getTime());
                                if(Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getRank() == Rank.HELPER
                                        && punishmentSeverity.getID() > 3) {
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &7You cannot execute this action."));
                                } else {
                                    Core.getInstance().getPunishmentManager().register(new PunishmentRecord(
                                            Core.getInstance().getAccountManager().parseDetails(punishing).getUUID(),
                                            punishmentType,
                                            punishmentSeverity,
                                            Timestamp.getCurrentTimestamp(),
                                            end,
                                            player.getUniqueId(),
                                            reason
                                    ));
                                    String banMessage = Core.getInstance().getPunishmentManager().hasOngoingBan(
                                            Core.getInstance().getAccountManager().parseDetails(punishing).getUUID());
                                    if(!banMessage.isEmpty()) {
                                        MessageChannel.getInstance().kickPlayerGlobally(player, punishing, banMessage);
                                    }
                                    if(punishmentType == PunishmentType.TEMP_MUTE || punishmentType == PunishmentType.PERM_MUTE) {
                                        MessageChannel.getInstance().sendMessage(player, punishing, "&4Punish> &7You have been muted by &e" + player.getName() + " &7for &f" + reason);
                                    }
                                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4Punish> &e" + punishing + " &7has been punished."));
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {
                gui.i.setItem(12, ItemCreator.getInstance().createSkull(1, punishing, "&e" + punishing, Arrays.asList(new String[]{
                        "&7Reason: &f" + reason,
                        "&7Timestamp: &f" + Timestamp.getCurrentTimestamp().toString()
                })));
            }
        }, javaPlugin);
    }
}