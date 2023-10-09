package com.frostedmc.arenapvp.commands;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.arenapvp.queue.Queue;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/16/2017.
 */
public class DuelCommand extends Command {

    private Map<Player, Player> duelRequests = new HashMap<Player, Player>();
    private Map<Player, UUID> duelRequestIDS = new HashMap<Player, UUID>();
    private Map<Player, Kit> duelKits = new HashMap<Player, Kit>();

    @Override
    public Rank requiredRank() {
        return Rank.PLAYER;
    }

    @Override
    public String commandLabel() {
        return "duel";
    }

    @Override
    public String commandDescription() {
        return "Fight against your mortal enemy!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(args.length <= 0) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bDuel> &7Invalid arguments, try /duel <username> <kit>"));
        } else {
            if(args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                if(args.length >= 2) {
                    if(Bukkit.getPlayer(args[1]) != null) {
                        Player rival = Bukkit.getPlayer(args[1]);
                        if(duelRequests.containsKey(rival)) {
                            if(duelRequests.get(rival).getName().equalsIgnoreCase(player.getName())) {
                                duelRequests.remove(rival);
                                duelRequestIDS.remove(rival);
                                final Kit kit = duelKits.get(rival);
                                duelKits.remove(rival);
                                if(ArenaManager.getInstance().inArena(rival)) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &e" + rival.getName() + " &7is currently unavailable."));
                                    return;
                                }
                                if(args[0].equalsIgnoreCase("accept")) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &7You have accepted &e" + rival.getName() + "&7's duel request."));
                                    rival.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &e" + player.getName() + " &7has accepted your duel request."));
                                    Queue.addTo(rival, player, kit);
                                    Queue.addTo(player, rival, kit);
                                } else {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &7You have denied &e" + rival.getName() + "&7's duel request."));
                                    rival.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &e" + player.getName() + " &7has denied your duel request."));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&bDuel> &7Invalid duel request."));
                            }
                        } else {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&bDuel> &7Invalid duel request."));
                        }
                    } else {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&bDuel> &e" + args[1] + " &7is not online."));
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bDuel> &7Invalid arguments, try /duel <accept/deny> <username>"));
                }
            } else {
                if(Bukkit.getPlayer(args[0]) != null) {
                    if(args[0].equalsIgnoreCase(player.getName())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                "&bDuel> &7You cannot duel yourself."));
                    } else {
                        Player rival = Bukkit.getPlayer(args[0]);
                        if(ArenaManager.getInstance().inArena(rival)) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    "&bDuel> &e" + rival.getName() + " &7is currently unavailable."));
                        } else {
                            if(args.length >= 2) {
                                Kit kit = Kit.fromName(args[1]);
                                if(kit == null) {
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &7Invalid kit."));
                                } else {
                                    if(duelRequests.containsKey(player)) {
                                        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                "&bDuel> &7You have already have a pending duel request."));
                                        return;
                                    }
                                    duelRequests.put(player, rival);
                                    duelKits.put(player, kit);
                                    final UUID uuid = UUID.randomUUID();
                                    duelRequestIDS.put(player, uuid);
                                    new BukkitRunnable() {
                                        public void run() {
                                            if(duelRequests.containsKey(player)) {
                                                if(duelRequestIDS.get(player) == uuid) {
                                                    duelRequests.remove(player);
                                                    duelRequestIDS.remove(player);
                                                    duelKits.remove(player);
                                                }
                                            }
                                        }
                                    }.runTaskLater(ArenaPVP.getInstance(), (20*30));
                                    rival.playSound(rival.getLocation(), Sound.NOTE_PIANO, 5, 1);
                                    rival.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &e" + player.getName() + " &7has requested a &3"
                                                    + kit.getName()
                                                    + " &7duel with you. This request will expire in &e30 seconds&7."));
                                    new FancyMessage("Duel> ")
                                       .color(ChatColor.AQUA)
                                       .then("[Accept] ")
                                       .color(ChatColor.GREEN)
                                       .style(ChatColor.BOLD)
                                       .tooltip("Left-Click to accept.")
                                       .command("/duel accept " + player.getName())
                                       .then("[Deny]")
                                       .color(ChatColor.RED)
                                       .style(ChatColor.BOLD)
                                       .tooltip("Left-Click to deny.")
                                       .command("/duel deny " + player.getName())
                                       .send(rival);
                                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bDuel> &7A duel request has been sent to &e" + rival.getName() + "&7. This request will expire in &e30 seconds&7."));
                                }
                            } else {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&bDuel> &7Invalid arguments, try /duel <username> <kit>"));
                            }
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bDuel> &e" + args[0] + " &7is not online."));
                }
            }
        }
    }
}