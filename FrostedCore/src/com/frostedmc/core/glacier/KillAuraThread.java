package com.frostedmc.core.glacier;

import com.frostedmc.core.Core;
import net.minecraft.server.v1_8_R3.WorldSettings;
import net.techcable.npclib.HumanNPC;
import net.techcable.npclib.NPCLib;
import net.techcable.npclib.NPCRegistry;
import net.techcable.npclib.nms.versions.v1_8_R3.entity.EntityNPCPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Redraskal_2 on 9/2/2016.
 */
public class KillAuraThread implements Listener {

    private JavaPlugin javaPlugin;
    private boolean run;
    private List<String> players;
    private List<Player> detecting;
    private Map<Player, Integer> hits;

    public KillAuraThread(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.run = false;
    }

    public void register() {
        this.players = new ArrayList<String>();
        this.hits = new HashMap<Player, Integer>();
        this.detecting = new ArrayList<Player>();
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        if(!run) {
            run = true;
        } else {
            return;
        }
    }

    @EventHandler
    public void onNPCDamaged(EntityDamageByEntityEvent entityDamageByEntityEvent) {
        if(entityDamageByEntityEvent.getDamager() instanceof Player
                && entityDamageByEntityEvent.getEntity() instanceof Player) {
            Player player = (Player) entityDamageByEntityEvent.getDamager();
            Player entity = (Player) entityDamageByEntityEvent.getEntity();

            if(players.contains(entity.getName())) {
                if(hits.containsKey(player)) {
                    hits.put(player, (hits.get(player) + 1));
                } else {
                    hits.put(player, 1);
                }
            }
        }
    }

    public void spawnEntity(Player target) {
        if(!NPCLib.isSupported()) {
            return;
        }

        if(detecting.contains(target)) {
            return;
        }

        detecting.add(target);

        NPCRegistry registry = NPCLib.getNPCRegistry("KillAuraRegistry", this.javaPlugin);
        String ign = randomPlayer(target);
        Core.getInstance().getLogger().info("[Glacier] Spawning clone of " + ign
                + " (" + Bukkit.getPlayer(ign).getUniqueId().toString() + ")...");
        this.players.add(ign);
        HumanNPC npc = registry.createHumanNPC(ign);
        npc.setShowInTabList(true);
        npc.setProtected(false);
        npc.setName(ign);
        npc.setSkin(Bukkit.getPlayer(ign).getUniqueId());
        npc.spawn(target.getLocation());
        Player entity = npc.getEntity();
        this.randomizeGear(entity);
        this.hideEntity(target, npc.eP().getBukkitEntity());

        npc.eP().playerInteractManager.b(WorldSettings.EnumGamemode.SURVIVAL);

        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(ticks > (20*4)) {
                    double health = npc.getEntity().getHealth();
                    players.remove(ign);
                    detecting.remove(target);
                    npc.despawn();
                    this.cancel();

                    if(target.isOnline()
                        && hits.containsKey(target)) {
                        if(hits.get(target) >= 5 || health <= 1) {
                            //GlacierModule.getInstance().handleViolation(target, KillAura.class);
                        }
                    }

                    return;
                } else {
                    ticks++;
                }

                if(target.isOnline()) {
                    if(npc.isSpawned()
                            && !npc.isDestroyed()) {
                        adjustLocation(target, npc.eP());
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 3);
    }

    private String randomPlayer(Player target) {
        List<Player> random = new ArrayList<Player>();
        random.addAll(Bukkit.getOnlinePlayers());

        if(random.size() > 1) {
            random.remove(target);
        }

        return random.toArray(new Player[random.size()])[new Random().nextInt(random.size())].getName();
    }

    private void adjustLocation(Player target, EntityNPCPlayer entityNPCPlayer) {
        Location eyeLocation = target.getEyeLocation();
        org.bukkit.util.Vector vector = target.getLocation().getDirection().multiply(2).normalize().setY(0);
        Location adjustedLocation = eyeLocation.add(vector);

        entityNPCPlayer.setPosition(adjustedLocation.getX(),
                adjustedLocation.getY(),
                adjustedLocation.getZ());
    }

    private void randomizeGear(Player entity) {
        Random random = new Random();
        int value = random.nextInt(100);

        if(value < 50) {
            entity.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        } else {
            entity.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
        }

        if(value < 50) {
            entity.getInventory().setLeggings(new ItemStack(Material.GOLD_LEGGINGS, 1));
        }

        if(value > 50) {
            entity.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
        }
    }

    private void hideEntity(Player show, Player entity) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!player.getName().equalsIgnoreCase(show.getName())) {
                player.hidePlayer(entity);
            }
        }
    }

    public void unregister() {
        if(run) {
            HandlerList.unregisterAll(this);
            run = false;
        }
    }
}