package com.frostedmc.fabrication.game;

import com.frostedmc.core.utils.Utils;
import com.frostedmc.fabrication.Fabrication;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public class Arena implements Listener {

    private static List<Arena> arenas = new ArrayList<Arena>();
    private static double MAX_HEIGHT = 60;

    public static Arena[] getArenas() {
        return arenas.toArray(new Arena[arenas.size()]);
    }

    public static Arena getArena(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayer().getName().equalsIgnoreCase(player.getName())) {
                return arena;
            }
        }
        return null;
    }

    private Player player;
    private List<Block> area;
    private Spawnpoint spawnpoint;
    private boolean allowExplosion = true;
    private boolean allowEmote = true;

    public Arena(Player player, Spawnpoint spawnpoint) {
        this.player = player;
        this.area = new ArrayList<Block>();
        this.spawnpoint = spawnpoint;
        for(Block block : Utils.getBlocksInRadius(player.getLocation(), 32, false)) {
            if(block.getType() == Material.STAINED_CLAY || block.getType() == Material.BEACON) {
                area.add(block);
            }
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 100), true);
        player.setFlying(true);

        Fabrication.getInstance().getServer().getPluginManager().registerEvents(this, Fabrication.getInstance());
        arenas.add(this);
    }

    public Player getPlayer() { return this.player; }

    public Spawnpoint getSpawnpoint() {
        return this.spawnpoint;
    }

    public List<Block> getFloor() {
        return area;
    }

    public boolean inArea(Block block) {
        if(block.getLocation().getBlockY() < 36
                || block.getLocation().getBlockY() > 59) {
            return false;
        }

        for(Block b : area) {
            if(b.getLocation().getBlockX() == block.getLocation().getBlockX()
                    && b.getLocation().getBlockZ() == block.getLocation().getBlockZ()) {
                return true;
            }
        }
        return false;
    }

    private List<Block> getAboveBlocks(Block base) {
        List<Block> total = new ArrayList<Block>();

        for(int i=1; i<40; i++) {
            Block n = base.getRelative(0, i, 0);
            if(n.getLocation().getBlockY() < 60) {
                total.add(n);
            }
        }

        return total;
    }

    public void clear() {
        Block spawn = spawnpoint.get().getBlock();
        for(Block b : area) {
            if(b.getLocation().getBlockY() == 36) {
                b.setType(Material.STAINED_CLAY);
                b.setData((byte) 3);
            } else {
                b.setType(Material.AIR);
            }

            for(Block a : getAboveBlocks(b)) {
                a.setType(Material.AIR);
            }
        }
        spawn.getLocation().subtract(0, 1, 0).getBlock().setType(Material.BEACON);
    }

    public boolean explode() {
        if(!allowExplosion) {
            return false;
        } else {
            this.allowExplosion = false;
        }
        for(Block b : area) {
            for(Block a : getAboveBlocks(b)) {
                if(a.getType() != Material.AIR) {
                    Material m = a.getType();
                    byte d = a.getData();
                    a.setType(Material.AIR);
                    FallingBlock fallingBlock = a.getWorld().spawnFallingBlock(a.getLocation(), m, d);
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(0, 2.5, 0).add(randomVector().multiply(0.3)));
                }
            }
        }
        return true;
    }

    public boolean emote(ItemStack skull) {
        if(!allowEmote) {
            return false;
        } else {
            this.allowEmote = false;
        }
        int current = 0;
        for(Block b : area) {
            if(current == 0) {
                ArmorStand emoteSkull = b.getWorld().spawn(b.getLocation().subtract(0, 1, 0), ArmorStand.class);
                emoteSkull.setHelmet(skull);
                emoteSkull.setGravity(true);
                emoteSkull.setBasePlate(false);
                emoteSkull.setRemoveWhenFarAway(false);
                emoteSkull.setVisible(false);
                emoteSkull.setVelocity(new Vector(0, 3.0, 0));

                new BukkitRunnable() {
                    float c = 0f;
                    public void run() {
                        new BukkitRunnable() {
                            public void run() {
                                if(emoteSkull.isDead()) {
                                    this.cancel();
                                } else if(emoteSkull.isOnGround()) {
                                    emoteSkull.remove();
                                } else {
                                    emoteSkull.setHeadPose(new EulerAngle(0, c, 0));
                                    c+=.1f;
                                }
                            }
                        }.runTaskTimer(Fabrication.getInstance(), 0, 3L);
                    }
                }.runTaskLater(Fabrication.getInstance(), 20L);
            }
            if(current >= 10) {
                current = 0;
            } else {
                current++;
            }
        }
        return true;
    }

    public static Vector randomVector() {
        double rnd = new Random().nextDouble() * 2.0D * 3.14D;
        double x = Math.cos(rnd);
        double z = Math.sin(rnd);
        return new Vector(x, 0.0D, z);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent blockPlaceEvent) {
        if(blockPlaceEvent.getPlayer().getName().equalsIgnoreCase(player.getName())) {
            if(!inArea(blockPlaceEvent.getBlock())) {
                blockPlaceEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent blockBreakEvent) {
        if(blockBreakEvent.getPlayer().getName().equalsIgnoreCase(player.getName())) {
            if(!inArea(blockBreakEvent.getBlock())) {
                blockBreakEvent.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if(playerMoveEvent.getPlayer().getName().equalsIgnoreCase(player.getName())) {
            if(playerMoveEvent.getTo().getY() >= MAX_HEIGHT) {
                playerMoveEvent.setTo(playerMoveEvent.getFrom());
            }
        }
    }
}