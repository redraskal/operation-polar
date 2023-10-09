package com.frostedmc.arenapvp.arena.elo;

import com.frostedmc.arenapvp.arena.Kit;
import com.frostedmc.core.api.account.Timestamp;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 1/13/2017.
 */
public class EloResetRunnable extends BukkitRunnable {

    private Player player;
    private Kit kit;
    private long lastCheck = System.currentTimeMillis();

    public EloResetRunnable(Player player, Kit kit) {
        this.player = player;
        this.kit = kit;
    }

    @Override
    public void run() {
        if(player.isOnline()) {
            if((System.currentTimeMillis() - lastCheck) >= 60000L) {
                if(checkReset(player.getUniqueId(), kit)) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lYour elo for " + kit.getName() + " has been reset. You are now able to play Ranked with this kit."));
                    this.cancel();
                }
            }
        } else {
            this.cancel();
        }
    }

    public static boolean checkReset(UUID uuid, Kit kit) {
        EloProfile eloProfile = EloManager.getInstance().fetchProfile(uuid);
        if(eloProfile.getElo(kit) == -1) {
            Date date = eloProfile.getLastReset(kit).toDate();
            long diff = Timestamp.getCurrentTimestamp().toDate().getTime() - date.getTime();
            long diffMinutes = diff / (60 * 1000) % 60;
            if(diffMinutes >= 15) {
                EloManager.getInstance().updateElo(uuid, kit, 250);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
