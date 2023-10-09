package com.frostedmc.core.cosmetics;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Redraskal_2 on 9/8/2016.
 */
public class CosmeticsManager {

    private static CosmeticsManager instance;

    public static CosmeticsManager getInstance() {
        if(instance == null) {
            instance = new CosmeticsManager();
        }

        return instance;
    }

    private Map<Player, Gadget> gadgets = new HashMap<Player, Gadget>();
    private Map<Player, Particle> particles = new HashMap<Player, Particle>();

    public boolean hasCosmetics(Player player) {
        boolean has = false;

        if(gadgets.containsKey(player)) { has = true; }
        if(particles.containsKey(player)) { has = true; }

        return has;
    }

    public void disableCosmetics(Player player) {
        if(hasGadget(player)) {
            disableGadget(player);
        }

        if(hasParticle(player)) {
            disableParticle(player);
        }
    }

    public boolean hasGadget(Player player) {
        return gadgets.containsKey(player);
    }
    public boolean hasParticle(Player player) {
        return particles.containsKey(player);
    }

    public String getGadget(Player player) {
        if(hasGadget(player)) {
            return gadgets.get(player).name();
        } else {
            return "None";
        }
    }

    public String getParticle(Player player) {
        if(hasParticle(player)) {
            return particles.get(player).name();
        } else {
            return "None";
        }
    }

    public void enableGadget(Player player, Gadget gadget) {
        if(hasGadget(player)) {
            disableGadget(player);
        }

        gadgets.put(player, gadget);
        gadget.enable(player);
    }

    public void enableParticle(Player player, Particle particle) {
        if(hasParticle(player)) {
            disableParticle(player);
        }

        particles.put(player, particle);
    }

    public void disableGadget(Player player) {
        if(hasGadget(player)) {
            gadgets.get(player).disable(player);
        }

        gadgets.remove(player);
    }

    public void disableParticle(Player player) {
        if(!hasParticle(player)) {
            return;
        }

        particles.remove(player);
    }
}