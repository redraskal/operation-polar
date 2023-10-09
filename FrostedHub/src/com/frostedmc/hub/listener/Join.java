package com.frostedmc.hub.listener;

import com.frostedmc.core.api.account.achievement.Achievement;
import com.frostedmc.core.api.account.achievement.AchievementEffect;
import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.title.TitleAnimator;
import com.frostedmc.core.utils.title.defaults.SlideRightAnimation;
import com.frostedmc.hub.Hub;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Redraskal_2 on 2/2/2017.
 */
public class Join implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        JukeboxManager.getInstance().update(event.getPlayer().getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/HubChristmas.mp3");
        Hub.getInstance().publishServer();
        for(int i=0; i<200; i++) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
        }
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&lWelcome to FrostedMC!"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7If you find any bugs, please use"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b/bugreport &7to notify us."));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));

        event.getPlayer().getInventory().setItem(4, ItemCreator.getInstance().createSkull(1, event.getPlayer().getName(), "&5&lPROFILE"));
        event.getPlayer().getInventory().setBoots(new ItemStack(Material.AIR));

        new BukkitRunnable() {
            public void run() {
                new TitleAnimator(event.getPlayer(), Hub.getInstance(), new SlideRightAnimation(
                        ChatColor.AQUA + "Welcome to FrostedMC",
                        ChatColor.YELLOW + event.getPlayer().getName(),
                        2, true, Sound.ORB_PICKUP, Sound.CLICK, Sound.BLAZE_DEATH));
            }
        }.runTaskLater(Hub.getInstance(), 20L);

        AchievementEffect.giveAchievement(event.getPlayer().getUniqueId(),
                new Achievement("Traveller I", "Welcome to the server!"), 100, 50, Hub.getInstance());

        if(!Hub.allowLogin) {
            new BukkitRunnable() {
                public void run() {
                    for(int x = -10; x < 10; x++) {
                        for(int z = -10; z < 10; z++) {
                            event.getPlayer().getWorld().refreshChunk(x, z);
                        }
                    }
                }
            }.runTaskLater(Hub.getInstance(), 20L);
        }
    }
}