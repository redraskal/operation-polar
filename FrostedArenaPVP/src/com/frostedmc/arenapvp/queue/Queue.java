package com.frostedmc.arenapvp.queue;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.Game;
import com.frostedmc.arenapvp.arena.GameType;
import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.core.utils.ActionBar;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Queue {

    private static List<Queue> queues = new ArrayList<Queue>();

    public Player player;
    public Kit kit;
    public boolean finding = true;
    public Player rival;
    public boolean end = false;
    public long start;
    public boolean suggestPracticeMode = false;
    public GameType gameType;

    private Queue(Player player, Player rival, Kit kit) {
        this.player = player;
        this.kit = kit;
        this.gameType = GameType.UNRANKED;
        this.start = System.currentTimeMillis();
        this.finding = false;
        this.rival = rival;
        Game.queueInv(player);
        for(Queue queue : queues) {
            if(queue.player.getName().equalsIgnoreCase(rival.getName())) {
                this.player.playSound(this.player.getLocation(), Sound.NOTE_PIANO, 10, 1);
                this.rival.playSound(this.rival.getLocation(), Sound.NOTE_PIANO, 10, 1);
                ArenaManager.getInstance().findArena(this);
            }
        }
        new BukkitRunnable() {
            boolean dir = true;
            int currentDot = 0;
            public void run() {
                if(rival != null) {
                    if(!rival.isOnline()) {
                        this.cancel();
                        if(inQueue(player)) {
                            queues.remove(getQueue(player));
                        }
                        if(rival != null) {
                            Queue.getQueue(rival).rival = null;
                            Queue.getQueue(rival).finding = true;
                            Queue.getQueue(rival).end = false;
                        }
                        return;
                    }
                }
                if(!player.isOnline() || !inQueue(player)) {
                    this.cancel();
                    if(inQueue(player)) {
                        queues.remove(getQueue(player));
                    }
                    if(rival != null) {
                        Queue.getQueue(rival).rival = null;
                        Queue.getQueue(rival).finding = true;
                        Queue.getQueue(rival).end = false;
                    }
                    return;
                }
                String dots = "";
                for(int i=0; i<3; i++) {
                    if(i == currentDot) {
                        dots+="0";
                    } else {
                        dots+="o";
                    }
                }
                if(dir) {
                    currentDot++;
                } else {
                    currentDot--;
                }
                if(currentDot >= 2 || currentDot <= 0) {
                    dir = !dir;
                }
                if(finding) {
                    ActionBar.send(player, "&bSearching for players... " + dots);
                    if((System.currentTimeMillis() - start) >= 10000L && !suggestPracticeMode) {
                        if(Bukkit.getOnlinePlayers().size() == 1) {
                            suggestPracticeMode = true;
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lIt looks like no one else is online at the moment."));
                            new FancyMessage("Click here")
                                    .color(ChatColor.GOLD)
                                    .style(ChatColor.BOLD)
                                    .tooltip("Enter Practice Mode (/practice)")
                                    .command("/practice")
                                    .then(" to practice with a bot. You will receive no rewards in Practice Mode.")
                                    .color(ChatColor.YELLOW)
                                    .send(player);
                        }
                    }
                } else {
                    if(end) {
                        ActionBar.send(player, "&bPlayer found, initializing game...");
                        this.cancel();
                        queues.remove(Queue.getQueue(player));
                    } else {
                        ActionBar.send(player, "&bPlayer found, initializing game... " + dots);
                    }
                }
            }
        }.runTaskTimer(ArenaPVP.getInstance(), 0, 5L);
    }

    private Queue(Player player, Kit kit, GameType gameType) {
        this.player = player;
        this.kit = kit;
        this.gameType = gameType;
        this.start = System.currentTimeMillis();
        Game.queueInv(player);
        for(Queue queue : queues) {
            if(queue.kit == this.kit && queue.gameType == this.gameType) {
                this.finding = false;
                this.rival = queue.player;
                queue.finding = false;
                queue.rival = this.player;
                this.player.playSound(this.player.getLocation(), Sound.NOTE_PIANO, 10, 1);
                this.rival.playSound(this.rival.getLocation(), Sound.NOTE_PIANO, 10, 1);
                ArenaManager.getInstance().findArena(this);
            }
        }
        new BukkitRunnable() {
            boolean dir = true;
            int currentDot = 0;
            public void run() {
                if(!player.isOnline() || !inQueue(player)) {
                    this.cancel();
                    if(inQueue(player)) {
                        queues.remove(getQueue(player));
                    }
                    if(rival != null) {
                        Queue.getQueue(rival).rival = null;
                        Queue.getQueue(rival).finding = true;
                        Queue.getQueue(rival).end = false;
                    }
                    return;
                }
                String dots = "";
                for(int i=0; i<3; i++) {
                    if(i == currentDot) {
                        dots+="0";
                    } else {
                        dots+="o";
                    }
                }
                if(dir) {
                    currentDot++;
                } else {
                    currentDot--;
                }
                if(currentDot >= 2 || currentDot <= 0) {
                    dir = !dir;
                }
                if(finding) {
                    ActionBar.send(player, "&bSearching for players... " + dots);
                    if((System.currentTimeMillis() - start) >= 10000L && !suggestPracticeMode) {
                        if(Bukkit.getOnlinePlayers().size() == 1) {
                            suggestPracticeMode = true;
                            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lIt looks like no one else is online at the moment."));
                            new FancyMessage("Click here")
                                    .color(ChatColor.GOLD)
                                    .style(ChatColor.BOLD)
                                    .tooltip("Enter Practice Mode (/practice)")
                                    .command("/practice")
                                    .then(" to practice with a bot. You will receive no rewards in Practice Mode.")
                                    .color(ChatColor.YELLOW)
                                    .send(player);
                        }
                    }
                } else {
                    if(end) {
                        ActionBar.send(player, "&bPlayer found, initializing game...");
                        this.cancel();
                        queues.remove(Queue.getQueue(player));
                    } else {
                        ActionBar.send(player, "&bPlayer found, initializing game... " + dots);
                    }
                }
            }
        }.runTaskTimer(ArenaPVP.getInstance(), 0, 5L);
    }

    public void remove() {
        queues.remove(Queue.getQueue(player));
    }

    public static int count(Kit kit, GameType gameType) {
        int count = 0;
        for(Queue queue : queues) {
            if(queue.kit == kit && queue.gameType == gameType) {
                count++;
            }
        }
        return count;
    }

    public static boolean addTo(Player player, Kit kit, GameType gameType) {
        if(inQueue(player))
            return false;
        queues.add(new Queue(player, kit, gameType));
        return true;
    }

    public static boolean addTo(Player player, Player rival, Kit kit) {
        if(inQueue(player))
            return false;
        queues.add(new Queue(player, rival, kit));
        return true;
    }

    public static Queue getQueue(Player player) {
        for(Queue queue : queues) {
            if(queue.player == player) {
                return queue;
            }
        }
        return null;
    }

    public static boolean inQueue(Player player) {
        for(Queue queue : queues) {
            if(queue.player == player) {
                return true;
            }
        }
        return false;
    }
}