package com.frostedmc.core.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public enum ColoredParticle {

    MOB_SPELL("MOB_SPELL"), MOB_SPELL_AMBIENT("MOB_SPELL_AMBIENT"), RED_DUST("reddust");

    private ColoredParticle(String name) {
        this.name = name;
    }

    String name;

    public void send(Location location, List<Player> players, int r, int g, int b) {
        ParticleEffect.valueOf(name).display(r/255, g / 255, b / 255, 1, 0, location, players);
    }
    public void send(Location location, int Distance, int r, int g, int b) {
        ParticleEffect.valueOf(name).display(r/255, g / 255, b / 255, 1, 0, location, Distance);
    }
}