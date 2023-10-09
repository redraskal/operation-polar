package com.frostedmc.core.utils.title;

import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 11/15/2016.
 */
public abstract class TitleAnimation {

    /**
     * Sets the amount of frames to display
     * @return
     */
    public abstract int frames();

    /**
     * Specifies the speed in which frames advance at
     * @return
     */
    public abstract long speed();

    /**
     * The amount of seconds for the fade out animation
     * @return
     */
    public abstract int fadeOut();

    /**
     * Called when a frame advances
     * @param player
     * @param currentFrame
     */
    public abstract String[] advance(Player player, int currentFrame);

    /**
     * Called when the animation starts
     * @param player
     */
    public abstract void onStart(Player player);

    /**
     * Called when the animation ends
     * @param player
     */
    public abstract void onFinish(Player player);
}