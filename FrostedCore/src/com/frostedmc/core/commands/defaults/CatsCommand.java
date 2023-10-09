package com.frostedmc.core.commands.defaults;

import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.Command;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.core.utils.ParticleEffect;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPCLib;
import net.techcable.npclib.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 9/10/2016.
 */
public class CatsCommand extends Command {

    private boolean enabled = false;

    @Override
    public Rank requiredRank() {
        return Rank.DEV;
    }

    @Override
    public String commandLabel() {
        return "cats";
    }

    @Override
    public String commandDescription() {
        return "CATS, CATS, MOAR CATS!!!!!!!?!??!?!!?!?!";
    }

    @Override
    public void onCommand(Player player, String[] args) {
        if(enabled) {
            player.sendMessage(PredefinedMessage.CATS_ENABLED.build());
            return;
        }

        enabled = true;
        player.sendMessage(PredefinedMessage.CATS_ENABLE.build());
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e&lWARNING:"));
        Location location = player.getLocation().clone();

        new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l&oCATS ARE INCOMING."));
            }
        }.runTaskLater(CommandManager.getInstance().getPlugin(), 20L);

        new BukkitRunnable() {
            public void run() {
                task1(location);
            }
        }.runTaskLater(CommandManager.getInstance().getPlugin(), 60L);
    }

    private void spawnNPC(Location location) {
        if(new Random().nextBoolean()) {
            location.setX((location.getX()+new Random().nextInt(3)));
            location.setZ((location.getZ() + new Random().nextInt(3)));
        } else {
            location.setX((location.getX()-new Random().nextInt(3)));
            location.setZ((location.getZ()-new Random().nextInt(3)));
        }

        if(new Random().nextInt(2) == 0) {
            location.setX((location.getX()-new Random().nextInt(3)));
            location.setZ((location.getZ()+new Random().nextInt(3)));
        } else {
            location.setX((location.getX()-new Random().nextInt(3)));
            location.setZ((location.getZ()+new Random().nextInt(3)));
        }

        NPCRegistry registry = NPCLib.getNPCRegistry("CatsRegistry", CommandManager.getInstance().getPlugin());
        HumanNPC npc = registry.createHumanNPC("Phineas");
        npc.setShowInTabList(true);
        npc.setProtected(true);
        npc.setName("Phineas");
        npc.setSkin("Phineas");
        npc.spawn(location);

        new BukkitRunnable() {
            int amount = 0;
            boolean up = true;
            public void run() {
                if(!npc.eP().isAlive()) {
                    this.cancel();
                    return;
                }

                if(new Random().nextBoolean()) {
                    npc.eP().setLocation(npc.eP().locX+amount, npc.eP().locY+amount, npc.eP().locZ+amount, new Random().nextFloat(), new Random().nextFloat());
                } else {
                    npc.eP().setLocation(npc.eP().locX-amount, npc.eP().locY+amount, npc.eP().locZ-amount, new Random().nextFloat(), new Random().nextFloat());
                }

                Location tempLocation = new Location(location.getWorld(), npc.eP().locX, npc.eP().locY, npc.eP().locZ);
                ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 3, tempLocation, 15);
                ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 3, tempLocation, 15);

                if(up) {
                    if(amount >= 3) {
                        up = false;
                    } else {
                        amount++;
                    }
                } else {
                    if(amount <= -3) {
                        up = true;
                    } else {
                        amount--;
                    }
                }
            }
        }.runTaskTimer(CommandManager.getInstance().getPlugin(), 0, 5L);
    }

    private void task1(Location location) {
        for(int i=0; i<=5; i++) {
            spawnNPC(location);
        }

        new BukkitRunnable() {
            public void run() {
                //Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&c&l&oCATS ARE INCOMING."));
            }
        }.runTaskTimer(CommandManager.getInstance().getPlugin(), 0, 5L);
    }
}
