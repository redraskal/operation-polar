package com.frostedmc.core.cosmetics.handler;

import com.frostedmc.core.cosmetics.CosmeticsManager;
import com.frostedmc.core.cosmetics.particles.SeaVortex;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public class SeaVortexHandler {

    public SeaVortexHandler(Player player, JavaPlugin javaPlugin) {
        if(CosmeticsManager.getInstance().hasParticle(player)) {
            if(CosmeticsManager.getInstance().getParticle(player).equalsIgnoreCase("sea vortex")) {
                CosmeticsManager.getInstance().disableParticle(player);
                return;
            }
        }

        player.closeInventory();
        CosmeticsManager.getInstance().enableParticle(player, new SeaVortex(player, javaPlugin));
    }
}
