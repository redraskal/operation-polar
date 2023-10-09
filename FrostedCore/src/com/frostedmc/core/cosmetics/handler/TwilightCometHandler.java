package com.frostedmc.core.cosmetics.handler;

import com.frostedmc.core.cosmetics.CosmeticsManager;
import com.frostedmc.core.cosmetics.particles.TwilightComet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public class TwilightCometHandler {

    public TwilightCometHandler(Player player, JavaPlugin javaPlugin) {
        if(CosmeticsManager.getInstance().hasParticle(player)) {
            if(CosmeticsManager.getInstance().getParticle(player).equalsIgnoreCase("twilight comet")) {
                CosmeticsManager.getInstance().disableParticle(player);
                return;
            }
        }

        player.closeInventory();
        CosmeticsManager.getInstance().enableParticle(player, new TwilightComet(player, javaPlugin));
    }
}
