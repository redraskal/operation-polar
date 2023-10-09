package com.frostedmc.gamemanager.game.rocketroyal.powerupAbility;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.NMSUtils;
import com.frostedmc.gamemanager.game.rocketroyal.PowerupAbility;
import com.frostedmc.gamemanager.game.rocketroyal.RocketRoyal;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class BlazingFastBulletsAbility extends PowerupAbility {

    @Override
    public String name() {
        return "Blazing Fast Bullets";
    }

    @Override
    public int ticksEnabled() {
        return 20*6;
    }

    public BlazingFastBulletsAbility(Player player) {
        super(player);
    }

    @Override
    public void onActivate() {
        if(GameManager.getInstance().getCurrentGame() != null
                && GameManager.getInstance().getCurrentGame() instanceof RocketRoyal) {
            RocketRoyal rocketRoyal = (RocketRoyal) GameManager.getInstance().getCurrentGame();
            rocketRoyal.getReloadDelays().put(getPlayer(), 5);
        }
    }

    @Override
    public void onTick() {
        if(getTicks() % 10 == 0) {
            try {
                NMSUtils.sendActionBar(getPlayer(), "&7The reload delay has been significantly decreased on your Rocket Launcher.");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDeactivate() {
        if(GameManager.getInstance().getCurrentGame() != null
                && GameManager.getInstance().getCurrentGame() instanceof RocketRoyal) {
            RocketRoyal rocketRoyal = (RocketRoyal) GameManager.getInstance().getCurrentGame();
            if(rocketRoyal.getReloadDelays().containsKey(getPlayer())) {
                rocketRoyal.getReloadDelays().remove(getPlayer());
            }
        }
    }
}