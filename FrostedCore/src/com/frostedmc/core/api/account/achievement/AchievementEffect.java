package com.frostedmc.core.api.account.achievement;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.utils.ChatUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class AchievementEffect {

    public static void giveAchievement(UUID uuid, Achievement achievement, int icicles, int xp, JavaPlugin javaPlugin) {
        AchievementProfile achievementProfile = Core.getInstance().getAchievementManager().fetchProfile(uuid);
        if(achievementProfile.contains(achievement.getTitle())) {
            return;
        }
        if(Bukkit.getPlayer(uuid) != null) {
            new BukkitRunnable() {
                public void run() {
                    new AchievementEffect(Bukkit.getPlayer(uuid), achievement, icicles, xp, javaPlugin);
                }
            }.runTaskLater(javaPlugin, 40L);
        }
        Core.getInstance().getAchievementManager().add(uuid, achievement);
    }

    public AchievementEffect(Player player, Achievement achievement, int icicles, int xp, JavaPlugin javaPlugin) {
        player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 10, 1);
        player.playSound(player.getLocation(), Sound.FIREWORK_TWINKLE, 10, 1);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
        String[] middleMsg = new String[]{
                "Wow!",
                "You're awesome!",
                "This looks kool",
                "A new discovery!"
        };
        ChatUtils.sendBlockMessage("Achievement Get!", new String[]{
                "&d» &a" + middleMsg[new Random().nextInt(middleMsg.length)] + " &d«",
                "&b" + achievement.getTitle() + " - " + achievement.getDescription(),
                "&3+" + icicles + " icicles &e// &3+" + xp + " xp"
        }, player);
        for(int i=0; i<3; i++) {
            this.shootRandomFirework(player.getLocation(), javaPlugin);
        }
        Core.getInstance().getAccountManager().update(player.getUniqueId(),
                new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                        (Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getIcicles()+icicles)));
    }

    private void shootRandomFirework(Location startLocation, JavaPlugin javaPlugin) {
        Firework firework = startLocation.getWorld().spawn(startLocation, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)))
                .withFade(Color.WHITE)
                .flicker(true)
                .trail(true)
                .build());
        firework.setFireworkMeta(meta);
        new BukkitRunnable() {
            public void run() {
                firework.detonate();
            }
        }.runTaskLater(javaPlugin, 3L);
    }
}