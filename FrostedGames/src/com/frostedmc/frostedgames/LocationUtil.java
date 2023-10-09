package com.frostedmc.frostedgames;

import com.frostedmc.frostedgames.game.InternalGameSettings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 2/3/2017.
 */
public class LocationUtil {

    public static Player findNearestTarget() {
        Player target = null;
        for(Entity near : InternalGameSettings.gameMaker.getNearbyEntities(60, 60, 60)) {
            if(near instanceof Player) {
                if(InternalGameSettings.districtMap.containsKey(((Player) near))) {
                    if(target == null) {
                        target = (Player) near;
                    } else {
                        if(near.getLocation()
                                .distance(InternalGameSettings.gameMaker.getLocation())
                                < target.getLocation()
                                .distance(InternalGameSettings.gameMaker.getLocation())) {
                            target = (Player) near;
                        }
                    }
                }
            }
        }
        return target;
    }
}