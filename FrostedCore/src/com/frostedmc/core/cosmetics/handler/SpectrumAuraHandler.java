package com.frostedmc.core.cosmetics.handler;

import com.frostedmc.core.cosmetics.CosmeticsManager;
import com.frostedmc.core.cosmetics.particles.SpectrumAura;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public class SpectrumAuraHandler {

    public SpectrumAuraHandler(Player player, JavaPlugin javaPlugin) {
        if(CosmeticsManager.getInstance().hasParticle(player)) {
            if(CosmeticsManager.getInstance().getParticle(player).equalsIgnoreCase("spectrum aura")) {
                CosmeticsManager.getInstance().disableParticle(player);
                return;
            }
        }

        player.closeInventory();
        CosmeticsManager.getInstance().enableParticle(player, new SpectrumAura(player, javaPlugin));
    }
}
