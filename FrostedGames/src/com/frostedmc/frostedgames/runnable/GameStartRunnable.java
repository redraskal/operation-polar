package com.frostedmc.frostedgames.runnable;

import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 1/21/2017.
 */
public class GameStartRunnable extends BukkitRunnable implements Listener {

    public GameStartRunnable() {
        FrostedGames.getInstance().getServer().getPluginManager().registerEvents(this, FrostedGames.getInstance());
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 5, 1);
            if(InternalGameSettings.gameMaker == player) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 3), true);
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 10), true);
            }
            JukeboxManager.getInstance().update(player.getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/PVPGameMusic3.mp3");
            if(!InternalGameSettings.gameMaker.getName().equalsIgnoreCase(player.getName())) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }
        this.runTaskTimer(FrostedGames.getInstance(), 0, 20L);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(InternalGameSettings.gameMaker != null) {
            if(!event.getPlayer().getName().equalsIgnoreCase(InternalGameSettings.gameMaker.getName())) {
                if(event.getFrom().getX() != event.getTo().getX()
                        || event.getFrom().getZ() != event.getTo().getZ()) {
                    Location newLocation = event.getFrom();
                    newLocation.setY(event.getTo().getY());
                    newLocation.setYaw(event.getTo().getYaw());
                    newLocation.setPitch(event.getTo().getPitch());
                    event.setTo(newLocation);
                }
            }
        }
    }

    private int countdown = 10;

    @Override
    public void run() {
        if(InternalGameSettings.status == Status.ENDED) {
            this.cancel();
            return;
        }
        if(countdown <= 0) {
            this.cancel();
            HandlerList.unregisterAll(this);
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.PISTON_EXTEND, 10, 1);
            }
            new PeaceRunnable();
        } else {
            String co = "";
            if(countdown < 10) {
                co = "0" + countdown;
            } else {
                co = "" + countdown;
            }
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                    ScoreboardManager.getInstance().playerBoards.get(player)
                            .add("&fGame start: &a00&f:&a" + co, 5);
                    ScoreboardManager.getInstance().playerBoards.get(player).update();
                }
                player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
            }
            if(countdown == 5) {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(InternalGameSettings.gameMaker.getName().equalsIgnoreCase(player.getName())) {
                        ChatUtils.sendBlockMessage("You're the game-maker!", new String[]{
                                "&bYou will gather points over time,",
                                "&blater being able to trade them in",
                                "&bto use certain abilities.",
                                "&a&lLeft-Click to teleport",
                                "&a&lRight-Click to use an ability"
                        }, player);
                    } else {
                        ChatUtils.sendBlockMessage("You're a tribute!", new String[]{
                                "&bGet supplies and run into the wilderness!",
                                "&bYou have another district buddy,",
                                "&bbut you could be betrayed.",
                                "&7Watch out for the game-maker..."
                        }, player);
                    }
                    player.playSound(player.getLocation(), Sound.NOTE_PIANO, 10, 1);
                }
            }
            countdown--;
        }
    }
}
