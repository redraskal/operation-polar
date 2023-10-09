package com.frostedmc.gamemanager.game.rocketroyal;

import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.SpectatorMode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public abstract class PowerupAbility {

    @Getter private final Player player;
    @Getter private int ticks = 0;

    public abstract String name();

    public abstract int ticksEnabled();

    public PowerupAbility(Player player) {
        this.player = player;
    }

    public void run() {
        this.onActivate();
        Title title = new Title("&a" + this.name() + " POWERUP",
                "&2has been activated!", 0, 1, 1);
        title.send(this.getPlayer());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                "&bGame> &e" + player.getName() + " &7has activated the " + this.name() + " &a&lPOWERUP&7!"));
        new BukkitRunnable() {
            public void run() {
                if(!player.isOnline()
                        || SpectatorMode.getInstance().contains(player)
                        || ticks >= ticksEnabled()) {
                    this.cancel();
                    onDeactivate();
                    if(player.isOnline()) {
                        Title title = new Title("&c" + name() + " POWERUP",
                                "&4has ended!", 0, 1, 1);
                        title.send(getPlayer());
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                "&bGame> &e" + player.getName() + "&7's " + name() + " &a&lPOWERUP &7has ended!"));
                        getPlayer().playSound(getPlayer().getLocation(), Sound.BLAZE_HIT, 1f, 0.9f);
                        getPlayer().setLevel(0);
                    }
                } else {
                    onTick();
                    ticks++;
                    getPlayer().setLevel((ticksEnabled()-ticks));
                }
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    public abstract void onActivate();

    public abstract void onTick();

    public abstract void onDeactivate();
}