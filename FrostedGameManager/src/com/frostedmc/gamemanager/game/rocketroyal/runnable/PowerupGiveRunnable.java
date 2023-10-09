package com.frostedmc.gamemanager.game.rocketroyal.runnable;

import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.game.rocketroyal.PowerupAbility;
import com.frostedmc.gamemanager.game.rocketroyal.RocketRoyal;
import com.frostedmc.gamemanager.game.rocketroyal.powerupAbility.BlazingFastBulletsAbility;
import com.frostedmc.gamemanager.game.rocketroyal.powerupAbility.GhostModeAbility;
import com.frostedmc.gamemanager.game.rocketroyal.powerupAbility.HomingFireballAbility;
import com.frostedmc.gamemanager.game.rocketroyal.powerupAbility.SuperSpeedAbility;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class PowerupGiveRunnable extends BukkitRunnable {

    @Getter private final RocketRoyal rocketRoyal;
    @Getter private final Player player;

    private int ticks = 0;
    private int ticksLeft = 40;
    private List<PowerupAbility> powerupAbilityList = new ArrayList<>();
    private int currentPowerup = 0;

    public PowerupGiveRunnable(RocketRoyal rocketRoyal, Player player) {
        this.rocketRoyal = rocketRoyal;
        this.player = player;

        this.powerupAbilityList.add(new GhostModeAbility(player));
        this.powerupAbilityList.add(new HomingFireballAbility(player));
        this.powerupAbilityList.add(new BlazingFastBulletsAbility(player));
        this.powerupAbilityList.add(new SuperSpeedAbility(player));

        for(int i=0; i<new Random().nextInt(5); i++) {
            Collections.shuffle(this.powerupAbilityList);
        }

        this.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    @Override
    public void run() {
        if(GameManager.getInstance().getCurrentGame() == null
                || GameManager.getInstance().getCurrentGame().getGameUUID() != rocketRoyal.getGameUUID()) {
            this.cancel();
            return;
        }
        if(!player.isOnline() || SpectatorMode.getInstance().contains(player)) {
            this.cancel();
            return;
        }

        if(ticksLeft <= 0) {
            this.cancel();
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1f, 0.9f);
            player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 0.9f);
            powerupAbilityList.get(currentPowerup).run();
        } else {
            if(ticksLeft <= 20) {
                if(ticks % 4 == 0) {
                    player.playSound(player.getLocation(), Sound.CLICK, 0.8f, 1.2f);
                    this.nextValue();
                }
            } else {
                if(ticks % 2 == 0) {
                    player.playSound(player.getLocation(), Sound.CLICK, 0.8f, 1.2f);
                    this.nextValue();
                }
            }
        }

        ticks++;
        ticksLeft--;
    }

    private void nextValue() {
        Title title = new Title("&eChoosing &a&lPOWERUP&e...",
                "&a" + powerupAbilityList.get(currentPowerup).name(), 0, 1, 0);
        title.send(this.getPlayer());
        currentPowerup++;
        if(currentPowerup >= powerupAbilityList.size()) {
            currentPowerup = 0;
        }
    }
}