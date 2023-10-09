package com.frostedmc.gamemanager.game.aerial;

import com.frostedmc.core.Core;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.*;
import com.frostedmc.gamemanager.game.aerial.runnable.GameStartRunnable;
import com.frostedmc.gamemanager.gui.TeamQueueGUI;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import com.frostedmc.gamemanager.manager.TeamManager;
import com.frostedmc.gamemanager.runnable.TeamQueueExecutor;
import org.bukkit.*;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class AerialDomination extends Game {

    @Override
    public String gameName() {
        return "AERIAL";
    }

    public TeamManager teamManager;
    private MapInfo mapInfo;

    private Team teamA;
    private Team teamB;

    public AerialDomination() {
        this.gameFlags.minPlayers = 2;
        this.gameFlags.maxPlayers = 8;
        this.gameFlags.enablePVE = false;
        this.gameFlags.enablePVP = true;
        this.gameFlags.enablePVM = true;
        this.gameFlags.dropArmorOnDeath = false;
        this.gameFlags.dropItemsOnDeath = false;
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&4", 9);
        board.add("&bTime Limit » &a00&f:&a00", 8);
        board.add("&3", 7);
        board.add("&2&lSLIME: &a0%", 6);
        board.add("&5&lENDERMAN: &a0%", 5);
        board.add("&2", 4);
        board.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
        board.add("&6Server » &7" + Bukkit.getServerName(), 2);
        board.add("&1", 1);
        board.add("&bwww.frostedmc.com", 0);
    }

    @Override
    public void onGameLoad() {
        this.teamManager = new TeamManager(this, 4);
        this.teamA = new Team("Slime", new MaterialData(Material.SLIME_BLOCK, (byte) 1), ChatColor.DARK_GREEN);
        this.teamB = new Team("Enderman", new MaterialData(Material.DRAGON_EGG, (byte) 1), ChatColor.DARK_PURPLE);
        teamManager.addTeam(teamA);
        teamManager.addTeam(teamB);

        new TeamQueueExecutor(this);

        this.mapInfo = new MapInfo();
        List<Map> possibleMaps = new ArrayList(mapInfo.getPossibleMaps().keySet());
        this.loadMap(possibleMaps.get(random.nextInt(possibleMaps.size())));
    }

    @Override
    public void onGameUnload() {}

    @Override
    public void onMapLoad(Map map) {
        map.getInstance().setDifficulty(Difficulty.EASY);
    }

    @Override
    public void onMapUnload(Map map) {}

    @Override
    public void onGameStart() {
        teamManager.queuePlayersWithoutTeam();
        teamManager.executeQueue();
        teamManager.setAllowQueue(false);

        this.spawnPowerCrystals();

        this.teleportPlayers(this.teamA, true);
        this.teleportPlayers(this.teamB, false);

        new GameStartRunnable(this);
    }

    public void spawnPowerCrystals() {
        List<CustomLocation> spawnpoints = this.mapInfo.getPossibleMaps()
                .get(this.getLoadedMaps().get(0)).getEnderCrystals();

        for(CustomLocation spawnpoint : spawnpoints) {
            Location location = spawnpoint.convert(this.getLoadedMaps().get(0).getInstance());
            EnderCrystal enderCrystal = location.getWorld().spawn(location, EnderCrystal.class);
            enderCrystal.setCustomName(ChatColor.translateAlternateColorCodes('&', "&6&lPOWER CRYSTAL"));
            enderCrystal.setCustomNameVisible(true);
        }
    }

    public void teleportPlayers(Team team, boolean isTeamA) {
        List<CustomLocation> spawnpoints;

        if(isTeamA) {
            spawnpoints = this.mapInfo.getPossibleMaps()
                    .get(this.getLoadedMaps().get(0)).getTeamASpawnpoints();
        } else {
            spawnpoints = this.mapInfo.getPossibleMaps()
                    .get(this.getLoadedMaps().get(0)).getTeamBSpawnpoints();
        }

        int current = 0;
        NametagModule nametagModule = (NametagModule) Core.getInstance().getModule(NametagModule.class);

        for(Player player : team.getPlayers()) {
            player.teleport(spawnpoints.get(0).convert(this.getLoadedMaps().get(0).getInstance()));
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
                nametagModule.updateTag(player, team.getTeamColor().toString() + " [" + team.getName() + "]");
            }
            player.getInventory().clear();
            Move.dontAllow.add(player);
            //TODO: Give Kit/Abilities to player
            if(current >= spawnpoints.size()) {
                current = 0;
            } else {
                current++;
            }
        }
    }

    @Override
    public void onPlayerJoinLobby(Player player) {
        player.getInventory().setItem(0, ItemCreator.getInstance().createItem(Material.NETHER_STAR, 1, 0, "&b&lSELECT TEAM"));
    }

    @Override
    public void onItemClick(Player player, ItemStack itemStack, int slot) {
        if(itemStack.getItemMeta().getDisplayName().equals(
                ChatColor.translateAlternateColorCodes('&', "&b&lSELECT TEAM"))) {
            new TeamQueueGUI(player, teamManager);
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent playerToggleFlightEvent) {
        if(playerToggleFlightEvent.getPlayer().getGameMode() == GameMode.SURVIVAL
                || playerToggleFlightEvent.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            playerToggleFlightEvent.setCancelled(true);
            playerToggleFlightEvent.getPlayer().setAllowFlight(false);
            playerToggleFlightEvent.getPlayer().playSound(playerToggleFlightEvent.getPlayer().getLocation(), Sound.IRONGOLEM_THROW, 1f, 0.9f);
            playerToggleFlightEvent.getPlayer().getWorld().spigot().playEffect(playerToggleFlightEvent.getPlayer().getLocation(), Effect.CLOUD,
                    0, 0,
                    1, 1, 1,
                    0, 5, 30);
            playerToggleFlightEvent.getPlayer().setVelocity(playerToggleFlightEvent.getPlayer().getLocation().getDirection()
                    .multiply((1.6D)).setY((1.0D)));

            new BukkitRunnable() {
                public void run() {
                    new BukkitRunnable() {
                        public void run() {
                            if(playerToggleFlightEvent.getPlayer().isOnline()) {
                                if(playerToggleFlightEvent.getPlayer().isOnGround()) {
                                    this.cancel();
                                    if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
                                        playerToggleFlightEvent.getPlayer().setAllowFlight(true);
                                    }
                                }
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(GameManager.getInstance(), 0, 1L);
                }
            }.runTaskLater(GameManager.getInstance(), 10L);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().setAllowFlight(false);
        new BukkitRunnable() {
            public void run() {
                teamManager.removeOfflinePlayers();
            }
        }.runTaskLater(GameManager.getInstance(), 3L);
    }

    @Override
    public void onSpectatorJoin(Player player) {
        //TODO
    }

    @Override
    public void onGameEnd() {
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());
    }
}