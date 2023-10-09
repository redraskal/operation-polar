package com.frostedmc.core.utils.title.defaults;

import com.frostedmc.core.utils.title.TitleAnimation;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 11/15/2016.
 */
public class SlideRightAnimation extends TitleAnimation {

    private String topMessage;
    private String bottomMessage;
    private int speed;
    private boolean fadeOut;

    private Sound startSound;
    private Sound frameSound;
    private Sound finishSound;

    /**
     * A nice title animation that makes characters display from left to right in an orderly fashion.
     * @param topMessage
     * @param bottomMessage
     * @param speed
     * @param fadeOut
     * @param startSound
     * @param frameSound
     * @param finishSound
     */
    public SlideRightAnimation(String topMessage, String bottomMessage, int speed, boolean fadeOut,
                               Sound startSound, Sound frameSound, Sound finishSound) {
        this.topMessage = topMessage;
        this.bottomMessage = bottomMessage;
        this.speed = speed;
        this.fadeOut = fadeOut;

        this.startSound = startSound;
        this.frameSound = frameSound;
        this.finishSound = finishSound;
    }

    private String get(String fullMessage, int charsToDisplay) {
        String finalMessage = "";
        for(int i=0; i<fullMessage.length(); i++) {
            if(i < charsToDisplay) {
                finalMessage+=fullMessage.charAt(i);
            } else {
                if(i == charsToDisplay) {
                    finalMessage+="&km";
                } else {
                    finalMessage+=" ";
                }
            }
        }
        return finalMessage;
    }

    @Override
    public int frames() {
        if(this.topMessage.length() > this.bottomMessage.length()) {
            return this.topMessage.length()+1;
        } else {
            return this.bottomMessage.length()+1;
        }
    }

    @Override
    public long speed() {
        return this.speed;
    }

    @Override
    public int fadeOut() { if(this.fadeOut) { return 1; } return 0; }

    @Override
    public String[] advance(Player player, int currentFrame) {
        if(this.frameSound != null)
            player.playSound(player.getLocation(), this.frameSound, 10, 1);
        return new String[]{this.get(topMessage, currentFrame),
                this.get(this.bottomMessage, currentFrame)};
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