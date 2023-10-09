package com.frostedmc.fabrication.gui;

import com.frostedmc.core.Core;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Title;
import com.frostedmc.fabrication.Fabrication;
import com.frostedmc.fabrication.game.Arena;
import com.frostedmc.fabrication.game.GameStatus;
import com.frostedmc.fabrication.game.Theme;
import com.frostedmc.fabrication.manager.ScoreboardManager;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public class VoteGUI implements Listener {

    private VoteGUI instance;
    private Inventory inventory;
    private boolean allowClose;

    private Theme[] themes;
    private Map<Player, Theme> players = new HashMap<Player, Theme>();
    private Map<Theme, Integer> votes = new HashMap<Theme, Integer>();

    private int[] theme1 = new int[]{
            18,
            27,
            36,
            45,
    };

    public VoteGUI() {
        this.instance = this;
        this.inventory = Bukkit.createInventory(null, 54, "Choose a Theme");
        this.allowClose = false;
        this.themes = Theme.random();

        for(int i=0; i<54; i++) {
            this.inventory.setItem(i, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
        }

        for(int i=9; i<18; i++) {
            this.inventory.setItem(i, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 0, "&b"));
        }

        for(int i=0; i<themes.length; i++) {
            this.inventory.setItem(i, ItemCreator.getInstance().createItem(Material.BOOK, 1, 0, "&b&l" + themes[i].getName(),
                    Arrays.asList(new String[]{
                            "&e0 votes",
                    })));
        }

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.openInventory(this.inventory);
        }

        new BukkitRunnable() {
            public void run() {
                if(GameStatus.SECONDS <= 1) {
                    this.cancel();
                    HandlerList.unregisterAll(instance);
                    allowClose = true;
                    Theme theme = pick();
                    for(Player player : Bukkit.getOnlinePlayers()) { player.closeInventory();
                        player.removePotionEffect(PotionEffectType.BLINDNESS);
                        ScoreboardManager.getInstance().update(player, ScoreboardManager.UpdateType.STATUS, "&3Theme » &7" + theme.getName());
                        Title title = new Title("Theme »", theme.getName(), 1, 1, 1);
                        title.setTitleColor(ChatColor.DARK_AQUA);
                        title.setSubtitleColor(ChatColor.GRAY);
                        title.send(player);
                        new Toolbox(player, Arena.getArena(player));
                    }
                    //TODO
                } else {
                    for(int i=0; i<themes.length; i++) {
                        inventory.setItem(i, ItemCreator.getInstance().createItem(Material.BOOK, 1, 0, "&b&l" + themes[i].getName(),
                                Arrays.asList(new String[]{
                                        "&e" + votes(themes[i]) + " votes",
                                })));
                    }
                }
            }
        }.runTaskTimer(Fabrication.getInstance(), 0, 20L);

        Fabrication.getInstance().getServer().getPluginManager().registerEvents(this, Fabrication.getInstance());
    }

    public int getGraphAmount(Theme theme) {
        double themeVotes = (double) votes(theme);
        double totalVotes = (double) players.size();
        double percent = ((themeVotes / totalVotes) * 100);
        Core.getInstance().getLogger().info("[Debug] " + theme.getName() + " " + themeVotes + " Theme Votes | " + totalVotes + " Total Votes | " + percent + "%");

        if(percent < 25) {
            return 0;
        }

        if(percent < 50) {
            return 1;
        }

        if(percent < 75) {
            return 2;
        }

        if(percent < 100) {
            return 3;
        }

        return 4;
    }

    public int[] getSlots(int theme) {
        int start = (18+theme);
        List<Integer> slots = new ArrayList<Integer>();

        slots.add(45+theme);
        slots.add(36+theme);
        slots.add(27+theme);
        slots.add(18+theme);

        return Ints.toArray(slots);
    }

    private int votes(Theme theme) {
        if(votes.containsKey(theme)) {
            return votes.get(theme);
        } else {
            return 0;
        }
    }

    private boolean vote(Player player, Theme theme) {
        if(players.containsKey(player)) {
            if(players.get(player) != theme) {
                removeVote(players.get(player));
                addVote(theme);
                players.put(player, theme);
            } else {
                return false;
            }
        } else {
            addVote(theme);
            players.put(player, theme);
        }
        return true;
    }

    private void addVote(Theme theme) {
        if(votes.containsKey(theme)) {
            votes.put(theme, (votes.get(theme)+1));
        } else {
            votes.put(theme, 1);
        }
    }

    private void removeVote(Theme theme) {
        if(votes.containsKey(theme)) {
            int n = (votes.get(theme)-1);
            if(n > 0) { votes.put(theme, n); } else { votes.remove(theme); }
        }
    }

    private Theme pick() {
        if(votes.isEmpty()) {
            return Theme.values()[new Random().nextInt(Theme.values().length)];
        } else {
            Theme top = null;
            for(Map.Entry<Theme, Integer> entry : votes.entrySet()) {
                if(top == null) {
                    top = entry.getKey();
                } else {
                    if(entry.getValue() >= votes.get(top)) {
                        if(entry.getValue() == votes.get(top)) {
                            if(new Random().nextBoolean()) {
                                top = entry.getKey();
                            }
                        } else {
                            top = entry.getKey();
                        }
                    }
                }
            }
            if(top == null) { top = Theme.values()[new Random().nextInt(Theme.values().length)]; }
            return top;
        }
    }

    private void updateGraph() {
        int currentID = 0;
        for(Theme theme : themes) {
            int cur = 0;
            int max = getGraphAmount(theme);
            for(int slot : getSlots(currentID)) {
                if(cur < max) {
                    this.inventory.setItem(slot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 10, "&b"));
                } else {
                    this.inventory.setItem(slot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                }
                cur++;
            }
            currentID++;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.setCancelled(true);
        if(inventoryClickEvent.getCurrentItem() != null) {
            if(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName() != null) {
                String d = inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName();
                d = ChatColor.stripColor(d);

                if(inventoryClickEvent.getCurrentItem().getType() == Material.BOOK) {
                    Theme theme = Theme.from(d);
                    if(vote((Player) inventoryClickEvent.getWhoClicked(), theme)) {
                        inventory.setItem(inventoryClickEvent.getSlot(), ItemCreator.getInstance().createItem(Material.BOOK, 1, 0, "&b&l" + theme.getName(),
                                Arrays.asList(new String[]{
                                        "&e" + votes(theme) + " votes",
                                })));
                        this.updateGraph();
                        ((Player) inventoryClickEvent.getWhoClicked()).playSound(inventoryClickEvent.getWhoClicked().getLocation(),
                                Sound.NOTE_PLING, 10, 1);
                    } else {
                        ((Player) inventoryClickEvent.getWhoClicked()).playSound(inventoryClickEvent.getWhoClicked().getLocation(),
                                Sound.NOTE_BASS, 10, 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        if(!allowClose) {
            new BukkitRunnable() {
                public void run() {
                    if(((Player) inventoryCloseEvent.getPlayer()).isOnline()) {
                        inventoryCloseEvent.getPlayer().openInventory(inventory);
                    }
                }
            }.runTaskLater(Fabrication.getInstance(), 3L);
        }
    }
}