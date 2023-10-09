package com.frostedmc.arenapvp;

import com.frostedmc.core.utils.ActionBar;
import org.bukkit.entity.Player;

/**
 * Created by Redraskal_2 on 12/23/2016.
 */
public class BarUtil {

    public static void playProgressBar(Player player, char left, char progress, char right, int percentLeft) {
        String red = "";
        String green = "";
        if(percentLeft >= 10) {
            green+=progress;
        }
        if(percentLeft >= 20) {
            green+=progress;
        }
        if(percentLeft >= 30) {
            green+=progress;
        }
        if(percentLeft >= 40) {
            green+=progress;
        }
        if(percentLeft >= 50) {
            green+=progress;
        }
        if(percentLeft >= 60) {
            green+=progress;
        }
        if(percentLeft >= 70) {
            green+=progress;
        }
        if(percentLeft >= 80) {
            green+=progress;
        }
        if(percentLeft >= 90) {
            green+=progress;
        }
        if(percentLeft >= 100) {
            green+=progress;
        }
        for(int i=(green.length()-1); i<10; i++) {
            red+=progress;
        }
        red = red + red;
        green = green + green;
        ActionBar.send(player, "&8" + left + "&c" + red + "&a" + green + "&8" + right);
    }
}