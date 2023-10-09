package com.frostedmc.gamemanager.game.rocketroyal.powerupAbility;

import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.NMSUtils;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.game.rocketroyal.PowerupAbility;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.InvocationTargetException;

/**
 * Copyright (c) Redraskal 2018.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class SuperSpeedAbility extends PowerupAbility {

    @Override
    public String name() {
        return "Super Speed";
    }

    @Override
    public int ticksEnabled() {
        return 20*6;
    }

    public SuperSpeedAbility(Player player) {
        super(player);
    }

    @Override
    public void onActivate() {
        this.getPlayer().addPotionEffect(
                new PotionEffect(
                        PotionEffectType.SPEED, Integer.MAX_VALUE, 15),
                true);
    }

    @Override
    public void onTick() {
        if(this.getTicks() % 10 == 0) {
            try {
                NMSUtils.sendActionBar(getPlayer(), "&7SUPER SPEED YAHOOOOO!!!");
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
        this.getPlayer().removePotionEffect(PotionEffectType.SPEED);
        if(!SpectatorMode.getInstance().contains(getPlayer())
                && GameManager.getInstance().gameStatus == GameStatus.INGAME) {
            this.getPlayer().addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.SPEED, Integer.MAX_VALUE, 0),
                    true);
        }
    }
}