package com.frostedmc.gamemanager.manager;

import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class TeamManager {

    private final Game game;
    private List<Team> teams = new ArrayList<>();
    private Map<Player, Team> queue = new HashMap<>();
    private boolean balanceTeams = true;
    private boolean allowQueue = true;
    private int playersPerTeam = 4;

    public TeamManager(Game game) {
        this.game = game;
    }

    public TeamManager(Game game, int playersPerTeam) {
        this.game = game;
        this.playersPerTeam = playersPerTeam;
    }

    public Game getGame() {
        return this.game;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public boolean balanceTeams() {
        return this.balanceTeams;
    }

    public boolean getAllowQueue() {
        return this.allowQueue;
    }

    public int getPlayersPerTeam() {
        return this.playersPerTeam;
    }

    public void setBalanceTeams(boolean balanceTeams) {
        this.balanceTeams = balanceTeams;
    }

    public void setPlayersPerTeam(int playersPerTeam) {
        this.playersPerTeam = playersPerTeam;
    }

    public void setAllowQueue(boolean allowQueue) {
        this.allowQueue = allowQueue;
    }

    public boolean addTeam(Team team) {
        if(this.teams.contains(team)) return false;
        this.teams.add(team);
        return true;
    }

    public boolean removeTeam(Team team) {
        if(!this.teams.contains(team)) return false;
        team.removePlayers();
        this.teams.remove(team);
        return true;
    }

    public void removeOfflinePlayers() {
        for(Team team : this.teams) {
            List<Player> remove = new ArrayList<>();
            for(Player player : team.getPlayers()) {
                if(!player.isOnline()) {
                    remove.add(player);
                }
            }
            for(Player player : remove) {
                team.removePlayer(player);
            }
        }
    }

    public Team getTeam(Player player) {
        for(Team team : this.teams) {
            if(team.isOnTeam(player)) return team;
        }
        return null;
    }

    public int getPlayersInQueue(Team team) {
        int count = 0;

        for(Map.Entry<Player, Team> entry : this.queue.entrySet()) {
            if(entry.getValue().getName().equals(team.getName())) {
                count++;
            }
        }

        return count;
    }

    public void queuePlayer(Player player, Team team) {
        if(this.getTeam(player) != null) {
            if(!this.allowQueue) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &cYou cannot enter the queue right now."));
                return;
            } else {
                this.getTeam(player).removePlayer(player);
            }
        }
        this.queue.put(player, team);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You have been queued for the &e"
                + team.getName() + "&7 Team."));
    }

    public List<Team> getTeamsNotFull() {
        List<Team> notFull = new ArrayList<>();
        for(Team team : this.teams) {
            if(team.getPlayers().size() < this.playersPerTeam) {
                notFull.add(team);
            }
        }
        return notFull;
    }

    public Team getLeastFilledTeam() {
        Team leastFilled = null;
        for(Team team : this.teams) {
            if(leastFilled == null || (leastFilled.getPlayers().size() > team.getPlayers().size())) {
                leastFilled = team;
            }
        }
        return leastFilled;
    }

    public void queuePlayersWithoutTeam() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(this.getTeam(player) == null) {
                this.queuePlayer(player, this.getLeastFilledTeam());
            }
        });
    }

    public void executeQueue() {
        if(this.balanceTeams) {
            Map<Player, Team> oddOnesOut = new HashMap<>();
            for(Map.Entry<Player, Team> entry : this.queue.entrySet()) {
                if(entry.getValue().getPlayers().size() >= this.playersPerTeam) {
                    oddOnesOut.put(entry.getKey(), entry.getValue());
                } else {
                    entry.getValue().addPlayer(entry.getKey());
                }
            }
            for(Map.Entry<Player, Team> entry : oddOnesOut.entrySet()) {
                List<Team> notFull = this.getTeamsNotFull();
                boolean canJoin = (notFull.isEmpty() || notFull.contains(entry.getValue())
                        || entry.getValue().getPlayers().size() == (+1));

                if(canJoin) {
                    entry.getValue().addPlayer(entry.getKey());
                } else {
                    entry.getKey().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &cWe were unable to add you to your requested Team, as Team Balancing is currently enabled."));
                    notFull.get(new Random().nextInt(notFull.size())).addPlayer(entry.getKey());
                }
            }
        } else {
            for(Map.Entry<Player, Team> entry : this.queue.entrySet()) {
                entry.getValue().addPlayer(entry.getKey());
            }
        }
        this.queue.clear();
    }
}