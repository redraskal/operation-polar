package com.frostedmc.core.module.defaults;

import com.frostedmc.core.api.misc.StatisticsCache;
import com.frostedmc.core.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 8/28/2016.
 */
public class DoubleJumpModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    public Sound jumpSound;
    public int thrust;

    private List<Player> doubleJumping = new ArrayList<Player>();

    public DoubleJumpModule(JavaPlugin javaPlugin, Sound jumpSound, int thrust) {
        this.javaPlugin = javaPlugin;
        this.jumpSound = jumpSound;
        this.thrust = thrust;
    }

    @Override
    public String name() {
        return "DoubleJump";
    }

    @Override
    public void onEnable() {
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.enableFlight(player);
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent playerToggleFlightEvent) {
        if(playerToggleFlightEvent.getPlayer().getGameMode() == GameMode.SURVIVAL
                || playerToggleFlightEvent.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            if(StatisticsCache.getInstance().get(playerToggleFlightEvent.getPlayer(), "options_double_jump") == 1) return;
            playerToggleFlightEvent.setCancelled(true);
            playerToggleFlightEvent.getPlayer().setAllowFlight(false);
            this.doubleJumping.add(playerToggleFlightEvent.getPlayer());
            playerToggleFlightEvent.getPlayer().playSound(playerToggleFlightEvent.getPlayer().getLocation(), this.jumpSound, 1f, 1f);
            playerToggleFlightEvent.getPlayer().setVelocity(playerToggleFlightEvent.getPlayer().getLocation().getDirection()
                    .multiply((1.6D * thrust)).setY((1.0D * thrust)));

            new BukkitRunnable() {
                public void run() {
                    new BukkitRunnable() {
                        public void run() {
                            if(playerToggleFlightEvent.getPlayer().isOnline()) {
                                if(playerToggleFlightEvent.getPlayer().isOnGround()) {
                                    this.cancel();
                                    enableFlight(playerToggleFlightEvent.getPlayer());

                                    if(doubleJumping.contains(playerToggleFlightEvent.getPlayer())) {
                                        doubleJumping.remove(playerToggleFlightEvent.getPlayer());
                                    }
                                }
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(javaPlugin, 0, 3L);
                }
            }.runTaskLater(this.javaPlugin, 10L);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent entityDamageEvent) {
        if(entityDamageEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageEvent.getEntity();

            if(this.doubleJumping.contains(player)) {
                entityDamageEvent.setCancelled(true);
                this.doubleJumping.remove(player);
            }
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent playerGameModeChangeEvent) {
        new BukkitRunnable() {
            public void run() {
                if(playerGameModeChangeEvent.getPlayer().isOnline()) {
                    enableFlight(playerGameModeChangeEvent.getPlayer());
                }
            }
        }.runTaskLater(this.javaPlugin, 10L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.enableFlight(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.disableFlight(event.getPlayer());
    }

    private void enableFlight(Player player) {
        if(player.getGameMode() == GameMode.SURVIVAL
                || player.getGameMode() == GameMode.ADVENTURE) {
            player.setAllowFlight(true);
        }
    }

    private void disableFlight(Player player) {
        if(player.getGameMode() == GameMode.SURVIVAL
                || player.getGameMode() == GameMode.ADVENTURE) {
            player.setAllowFlight(false);
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);

        for(Player player : Bukkit.getOnlinePlayers()) {
            this.disableFlight(player);
        }
    }
}