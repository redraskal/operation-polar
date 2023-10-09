package com.frostedmc.core.glacier.checks;

import com.frostedmc.core.glacier.Check;
import com.frostedmc.core.glacier.GlacierModule;
import com.frostedmc.core.glacier.GlacierUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Redraskal_2 on 11/21/2016.
 */
public class Step extends Check {

    public Step() {
        super("Step", true);
        this.bannable = true;
        this.countToNotify = 1;
        this.maxViolations = 3;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if(!GlacierUtils.isOnGround(playerMoveEvent.getPlayer())
                || playerMoveEvent.getPlayer().getAllowFlight()
                || playerMoveEvent.getPlayer().hasPotionEffect(PotionEffectType.JUMP))
            return;
        //TODO Fix slab false-positives
        double yDifference = (playerMoveEvent.getTo().getY() - playerMoveEvent.getFrom().getY());
        if(yDifference > 0.9) {
            this.log(playerMoveEvent.getPlayer().getUniqueId(), "[yD: " + yDifference + "]");
            GlacierModule.getInstance().handleViolation(playerMoveEvent.getPlayer(), this);
        }
    }
}