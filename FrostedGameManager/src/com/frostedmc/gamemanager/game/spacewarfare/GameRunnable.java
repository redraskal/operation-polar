package com.frostedmc.gamemanager.game.spacewarfare;

import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.DamageManager;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.Random;

/**
 * Created by Redraskal_2 on 3/20/2017.
 */
public class GameRunnable extends BukkitRunnable {

    private boolean go = false;

    public GameRunnable() {
        this.runTaskTimer(GameManager.getInstance(), 0, 20L);
        Title title = new Title("", "❄ Space Warfare ❄", 0, 4, 0);
        title.setSubtitleColor(ChatColor.DARK_AQUA);
        title.broadcast();
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 1),
                    true);
            ChatUtils.sendBlockMessage("Space Warfare", new String[]{
                    "&bAvoid debris and kill players",
                    "&bto win the game!",
                    "&b",
                    "&e&lSneak &7to move up",
                    "&e&lDon't Sneak &7to move down"
            }, player);
        }
        new BukkitRunnable() {
            public void run() {
                new BukkitRunnable() {
                    int countdown = 3;
                    public void run() {
                        if(countdown <= 0) {
                            this.cancel();
                            Move.dontAllow.clear();
                            for(Player player : Bukkit.getOnlinePlayers()) {
                                if(SpectatorMode.getInstance().contains(player)) continue;
                                player.setLevel(0);
                                player.setExp(0);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
                                ((SpaceWarfare) GameManager.getInstance().getCurrentGame())
                                        .vehicles.put(player, new Cruiser(player));
                            }
                            Title title = new Title("", "Go!", 0, 1, 0);
                            title.setSubtitleColor(ChatColor.GREEN);
                            title.broadcast();
                            go = true;
                        } else {
                            if(countdown == 3) {
                                for(Player player : Bukkit.getOnlinePlayers()) {
                                    player.setLevel(3);
                                    player.setExp(0.9f);
                                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                }
                                Title title = new Title("", "3...", 0, 1, 0);
                                title.setSubtitleColor(ChatColor.RED);
                                title.broadcast();
                            }
                            if(countdown == 2) {
                                for(Player player : Bukkit.getOnlinePlayers()) {
                                    player.setLevel(2);
                                    player.setExp(0.6f);
                                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                }
                                Title title = new Title("", "2...", 0, 1, 0);
                                title.setSubtitleColor(ChatColor.YELLOW);
                                title.broadcast();
                            }
                            if(countdown == 1) {
                                for(Player player : Bukkit.getOnlinePlayers()) {
                                    player.setLevel(1);
                                    player.setExp(0.3f);
                                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                                }
                                Title title = new Title("", "1...", 0, 1, 0);
                                title.setSubtitleColor(ChatColor.DARK_GREEN);
                                title.broadcast();
                            }
                            countdown--;
                        }
                    }
                }.runTaskTimer(GameManager.getInstance(), 0, 20L);
            }
        }.runTaskLater(GameManager.getInstance(), 40L);
    }

    @Override
    public void run() {
        if(go) {
            if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
                Player far = this.farthest();
                if(far != null) {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(!SpectatorMode.getInstance().contains(player)) {
                            Location spawn = new Location(far.getWorld(), player.getLocation().getX(),
                                    player.getLocation().getY(), far.getLocation().getZ()-60);
                            int amount = 3+new Random().nextInt(8);
                            for(int i=0; i<amount; i++) {
                                spawn(spawn.clone().add(new Random().nextInt(6),
                                        (-1 + new Random().nextInt(6)), new Random().nextInt(6)));
                            }
                        }
                    }
                }
            } else {
                this.cancel();
            }
        }
    }

    private void spawn(Location spawn) {
        if(new Random().nextBoolean()) {
            block(spawn);
        } else {
            carpet(spawn);
        }
    }

    private void carpet(Location spawn) {
        int[] types = new int[]{
                7, 8, 15
        };
        ArmorStand armorStand = spawn.getWorld().spawn(spawn, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setVisible(false);
        armorStand.setSmall(new Random().nextBoolean());
        armorStand.setHelmet(new ItemStack(Material.CARPET, 1,
                (byte) types[new Random().nextInt(types.length)]));
        armorStand.setHeadPose(new EulerAngle(new Random().nextDouble(),
                new Random().nextDouble(), new Random().nextDouble()));
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if (ticks >= (20 * 10)) {
                    this.cancel();
                    armorStand.remove();
                } else {
                    boolean explode = false;
                    for (Entity near : armorStand.getNearbyEntities(1, 1, 1)) {
                        if (near.getUniqueId() != armorStand.getUniqueId()) {
                            if(near instanceof Player) {
                                if(SpectatorMode.getInstance().contains((Player) near)) continue;
                            }
                            explode = true;
                            if(near instanceof Player) {
                                DamageManager.handleDeath((Player) near, null, "Debris");
                            }
                        }
                    }
                    if (explode) {
                        this.cancel();
                        armorStand.remove();
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.EXPLODE, 1f, 1.5f);
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.GHAST_SCREAM, 1f, 1.5f);
                        ParticleEffect.SMOKE_LARGE.display(1, 1, 1, 1, 15, armorStand.getLocation(), 54);
                        ParticleEffect.FLAME.display(1, 1, 1, 1, 15, armorStand.getLocation(), 54);
                        ParticleEffect.EXPLOSION_HUGE.display(1, 1, 1, 1, 5, armorStand.getLocation(), 54);
                    } else {
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.NOTE_BASS, .5f, 1.5f);
                    }
                    ticks++;
                }
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    private void block(Location spawn) {
        Material[] types = new Material[]{
                Material.STONE,
                Material.COAL_ORE,
                Material.SOUL_SAND,
                Material.DIAMOND_ORE,
                Material.QUARTZ_ORE,
                Material.BEDROCK,
                Material.BEDROCK
        };
        ArmorStand armorStand = spawn.getWorld().spawn(spawn, ArmorStand.class);
        armorStand.setGravity(false);
        armorStand.setBasePlate(false);
        armorStand.setVisible(false);
        armorStand.setSmall(new Random().nextBoolean());
        armorStand.setHelmet(new ItemStack(types[new Random().nextInt(types.length)]));
        armorStand.setHeadPose(new EulerAngle(new Random().nextDouble(),
                new Random().nextDouble(), new Random().nextDouble()));
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if (ticks >= (20 * 10)) {
                    this.cancel();
                    armorStand.remove();
                } else {
                    boolean explode = false;
                    for (Entity near : armorStand.getNearbyEntities(1, 1, 1)) {
                        if (near.getUniqueId() != armorStand.getUniqueId()) {
                            if(near instanceof Player) {
                                if(SpectatorMode.getInstance().contains((Player) near)) continue;
                            }
                            explode = true;
                            if(near instanceof Player) {
                                DamageManager.handleDeath((Player) near, null, "Debris");
                            }
                        }
                    }
                    if (explode) {
                        this.cancel();
                        armorStand.remove();
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.EXPLODE, 1f, 1.5f);
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.GHAST_SCREAM, 1f, 1.5f);
                        ParticleEffect.SMOKE_LARGE.display(1, 1, 1, 1, 15, armorStand.getLocation(), 54);
                        ParticleEffect.FLAME.display(1, 1, 1, 1, 15, armorStand.getLocation(), 54);
                        ParticleEffect.EXPLOSION_HUGE.display(1, 1, 1, 1, 5, armorStand.getLocation(), 54);
                    } else {
                        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.NOTE_BASS, .5f, 1.5f);
                    }
                    ticks++;
                }
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    private Player farthest() {
        Player far = null;
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(far == null) {
                far = player;
            } else {
                if(player.getLocation().getZ() < far.getLocation().getZ()) {
                    far = player;
                }
            }
        }
        return far;
    }
}