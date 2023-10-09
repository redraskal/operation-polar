package com.frostedmc.kingdoms.title;

import com.frostedmc.core.utils.Title;
import com.frostedmc.kingdoms.Kingdoms;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 11/15/2016.
 */
public class TitleAnimator {

    /**
     * Creates a title animation
     * @param player
     * @param titleAnimation
     */
    public TitleAnimator(Player player, TitleAnimation titleAnimation) {
        titleAnimation.onStart(player);
        new BukkitRunnable() {
            int currentFrame = 0;
            public void run() {
                if(currentFrame >= titleAnimation.frames()) {
                    this.cancel();
                    titleAnimation.onFinish(player);
                } else {
                    String[] titleData = titleAnimation.advance(player, currentFrame);
                    int fadeOut = 0;
                    if(currentFrame == (titleAnimation.frames()-1)) { fadeOut = titleAnimation.fadeOut(); }
                    Title title = new Title(
                            ChatColor.translateAlternateColorCodes('&', titleData[0]),
                            ChatColor.translateAlternateColorCodes('&', titleData[1]),
                            0, 1, fadeOut
                    );
                    title.send(player);
                    currentFrame++;
                }
            }
        }.runTaskTimer(Kingdoms.getInstance(), 0, titleAnimation.speed());
    }
}