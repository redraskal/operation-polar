package com.frostedmc.gamemanager.api;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class Team {

    private final String name;
    private ChatColor chatColor;
    private final MaterialData icon;
    private List<Player> players = new ArrayList<>();

    public Team(String name, MaterialData icon) {
        this.name = name;
        this.icon = icon;
    }

    public Team(String name, MaterialData icon, ChatColor chatColor) {
        this.name = name;
        this.icon = icon;
        this.chatColor = chatColor;
    }

    public String getName() {
        return this.name;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public ChatColor getTeamColor() {
        return this.chatColor;
    }

    public MaterialData getIcon() {
        return this.icon;
    }

    public boolean isOnTeam(Player player) {
        return this.players.contains(player);
    }

    public boolean addPlayer(Player player) {
        if(this.players.contains(player)) return false;
        this.players.add(player);
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You have joined the &e"
                + this.getName() + "&7 Team."));
        return true;
    }

    public boolean removePlayer(Player player) {
        if(!this.players.contains(player)) return false;
        this.players.remove(player);
        if(player.isOnline()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You have left the &e"
                    + this.getName() + "&7 Team."));
        } else {
            this.players.forEach(pl -> pl.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bGame> &e" + player.getName() + " &7was removed from your team due to logging out.")));
        }
        return true;
    }

    public void removePlayers() {
        this.players.clear();
    }

    public void setTeamColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }
}