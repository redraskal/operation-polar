package com.frostedmc.core.cosmetics.handler;

import com.frostedmc.core.cosmetics.CosmeticsManager;
import com.frostedmc.core.cosmetics.particles.InfernosVengeance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public class InfernosVengeanceHandler {

    public InfernosVengeanceHandler(Player player, JavaPlugin javaPlugin) {
        if(CosmeticsManager.getInstance().hasParticle(player)) {
            if(CosmeticsManager.getInstance().getParticle(player).equalsIgnoreCase("inferno's vengeance")) {
                CosmeticsManager.getInstance().disableParticle(player);
                return;
            }
        }

        player.closeInventory();
        CosmeticsManager.getInstance().enableParticle(player, new InfernosVengeance(player, javaPlugin));
    }
}