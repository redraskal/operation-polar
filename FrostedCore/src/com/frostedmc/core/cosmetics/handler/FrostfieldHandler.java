package com.frostedmc.core.cosmetics.handler;

import com.frostedmc.core.cosmetics.CosmeticsManager;
import com.frostedmc.core.cosmetics.particles.Frostfield;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public class FrostfieldHandler {

    public FrostfieldHandler(Player player, JavaPlugin javaPlugin) {
        if(CosmeticsManager.getInstance().hasParticle(player)) {
            if(CosmeticsManager.getInstance().getParticle(player).equalsIgnoreCase("frostfield")) {
                CosmeticsManager.getInstance().disableParticle(player);
                return;
            }
        }

        player.closeInventory();
        CosmeticsManager.getInstance().enableParticle(player, new Frostfield(player, javaPlugin));
    }
}
