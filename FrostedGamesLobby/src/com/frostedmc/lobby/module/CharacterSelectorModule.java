package com.frostedmc.lobby.module;

import com.frostedmc.core.module.Module;
import com.frostedmc.lobby.utils.NPC;
import com.frostedmc.lobby.utils.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/4/2017.
 */
public class CharacterSelectorModule extends Module {

    private JavaPlugin javaPlugin;
    private NPC npc;
    private ArmorStand nameTag;

    public CharacterSelectorModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    public String name() {
        return "CharacterSelector";
    }

    public void onEnable() {
        npc = new NPC(javaPlugin, ChatColor.translateAlternateColorCodes('&', "&b"),
                UUID.fromString("4e94f45c-759c-4293-af16-fdf219237a5d"),
                new Location(Bukkit.getWorld("world"), 51.5, 48.0, -350.5, -25.7f, -0.4f), new NPCInteractEvent() {
            @Override
            public void on(Player player) {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bServer> &7This feature is coming soon."));
            }
        });
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    this.cancel();
                    nameTag = Bukkit.getWorld("world").spawn(new Location(Bukkit.getWorld("world"), 51.5, 48.0, -350.5)
                            .clone().add(0, 0.8, 0), ArmorStand.class);
                    nameTag.setSmall(true);
                    nameTag.setBasePlate(false);
                    nameTag.setVisible(false);
                    nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b&lCharacter Selector"));
                    nameTag.setCustomNameVisible(true);
                    nameTag.setGravity(false);
                }
            }
        }.runTaskTimer(javaPlugin, 0, 20L);
    }

    public void onDisable() {
        npc.despawn();
        if(nameTag != null) {
            nameTag.remove();
        }
    }
}