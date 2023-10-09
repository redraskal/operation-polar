package com.frostedmc.core.cosmetics.handler;

import com.frostedmc.core.cosmetics.CosmeticsManager;
import com.frostedmc.core.cosmetics.gadgets.FreezeRay;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public class FreezeRayHandler {

    public FreezeRayHandler(Player player, JavaPlugin javaPlugin) {
        if(CosmeticsManager.getInstance().hasGadget(player)) {
            if(CosmeticsManager.getInstance().getGadget(player).equalsIgnoreCase("freeze ray")) {
                CosmeticsManager.getInstance().disableGadget(player);
                return;
            }
        }

        player.closeInventory();
        CosmeticsManager.getInstance().enableGadget(player, new FreezeRay(javaPlugin));
    }
}