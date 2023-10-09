package com.frostedmc.gamemanager.game.rocketroyal.powerupAbility;

import com.frostedmc.gamemanager.NMSUtils;
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
public class GhostModeAbility extends PowerupAbility {

    @Override
    public String name() {
        return "Ghost Mode";
    }

    @Override
    public int ticksEnabled() {
        return 20*12;
    }

    public GhostModeAbility(Player player) {
        super(player);
    }

    @Override
    public void onActivate() {
        this.getPlayer().addPotionEffect(
                new PotionEffect(
                        PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0),
                true);
    }

    @Override
    public void onTick() {
        if(this.getTicks() % 10 == 0) {
            try {
                NMSUtils.sendActionBar(getPlayer(), "&7Players are currently unable to see your body.");
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
        this.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
    }
}