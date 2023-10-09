package com.frostedmc.core.glacier;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

/**
 * Created by Redraskal_2 on 11/21/2016.
 */
public class GlacierUtils {

    public static boolean isOnGround(Entity entity) {
        return entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid();
    }
}
