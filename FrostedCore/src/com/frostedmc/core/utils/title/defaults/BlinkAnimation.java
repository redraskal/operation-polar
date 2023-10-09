package com.frostedmc.core.utils.title.defaults;

import com.frostedmc.core.utils.BlinkEffect;
import com.frostedmc.core.utils.title.TitleAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 3/18/2017.
 */
public class BlinkAnimation extends TitleAnimation {

    private String message;
    private int speed;
    private boolean fadeOut;
    private BlinkEffect blinkEffect;

    private Sound startSound;
    private Sound frameSound;
    private Sound finishSound;

    /**
     * A nice blink effect like the scoreboard! :D
     * @param message
     * @param fadeOut
     * @param speed
     * @param startSound
     * @param frameSound
     * @param finishSound
     */
    public BlinkAnimation(String message, ChatColor backgroundColor, ChatColor mainColor, ChatColor waveColor,
                          boolean fadeOut, int speed,
                               Sound startSound, Sound frameSound, Sound finishSound) {
        this.message = message;
        this.speed = speed;
        this.fadeOut = fadeOut;
        this.blinkEffect = new BlinkEffect(message, backgroundColor, mainColor, waveColor);
        blinkEffect.next();

        this.startSound = startSound;
        this.frameSound = frameSound;
        this.finishSound = finishSound;
    }

    @Override
    public int frames() {
        return this.message.length();
    }

    @Override
    public long speed() {
        return this.speed;
    }

    @Override
    public int fadeOut() {
        if(this.fadeOut) { return 1; } return 0;
    }

    @Override
    public String[] advance(Player player, int currentFrame) {
        if(this.frameSound != null)
            player.playSound(player.getLocation(), this.frameSound, 10, 1);
        return new String[]{"&b",blinkEffect.next()};
    }

    @Override
    public void onStart(Player player) {
        if(this.startSound != null) {
            player.playSound(player.getLocation(), this.startSound, 10, 1);
        }
    }

    @Override
    public void onFinish(Player player) {
        if(this.finishSound != null) {
            player.playSound(player.getLocation(), this.finishSound, 10, 1);
        }
    }
}