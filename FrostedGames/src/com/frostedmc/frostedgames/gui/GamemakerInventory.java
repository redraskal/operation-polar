package com.frostedmc.frostedgames.gui;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.ActionBar;
import com.frostedmc.core.utils.Utils;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.LocationUtil;
import com.frostedmc.frostedgames.MathUtils;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import com.frostedmc.frostedgames.game.Status;
import com.frostedmc.frostedgames.game.effect.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class GamemakerInventory implements Listener {

    private Map<Integer, ItemStack[]> pages = new HashMap<Integer, ItemStack[]>();
    private Player player;
    private int currentPage = 0;
    private int points = 0;

    public GamemakerInventory(Player player) {
        this.player = player;
        this.setup();
        FrostedGames.getInstance().getServer().getPluginManager().registerEvents(this, FrostedGames.getInstance());
        this.setPage(0);
        new BukkitRunnable() {
            public void run() {
                if(player.isOnline()) {
                    if(InternalGameSettings.status == Status.INGAME) {
                        if(points < 200) {
                            points+=2;
                        }
                        double percent = (((Integer) points).doubleValue()/200)*100;
                        String greenDots = "";
                        String greyDots = "";
                        for(int i=1; i<=10; i++) {
                            if(percent >= (10*i)) {
                                greenDots+="■";
                            } else {
                                greyDots+="■";
                            }
                        }
                        ActionBar.send(player, ChatColor.translateAlternateColorCodes('&', "&8| &a"
                                + greenDots + "&7" + greyDots + " &8| &a"
                                + new DecimalFormat("#.#").format(percent) + "% &8| &a" + points + " points"));
                    }
                }
            }
        }.runTaskTimer(FrostedGames.getInstance(), 0, 20L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL)
            return;
        if(event.getAction() == Action.LEFT_CLICK_AIR
                || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if(!InternalGameSettings.districtMap.isEmpty()) {
                if(event.getPlayer().getName().equalsIgnoreCase(InternalGameSettings.gameMaker.getName())) {
                    event.setCancelled(true);
                    Player[] play = InternalGameSettings.districtMap.keySet()
                            .toArray(new Player[InternalGameSettings.districtMap.keySet().size()]);
                    event.getPlayer().teleport(play[new Random().nextInt(play.length)]);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 5, 1);
                }
            }
        }
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if(event.getItem() != null) {
            if(event.getItem().getType() != Material.AIR) {
                if(event.getItem().hasItemMeta()) {
                    if(event.getItem().getItemMeta().hasDisplayName()) {
                        String d = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());
                        if(d.equalsIgnoreCase("< Previous Page")) {
                            setPage((currentPage-1));
                        }
                        if(d.equalsIgnoreCase("Next Page >")) {
                            setPage((currentPage+1));
                        }
                        if(d.equalsIgnoreCase("SUPPLY DROP (25 points)")) {
                            if(points >= 25) {
                                if(event.getClickedBlock() != null
                                        && event.getClickedBlock().getType() != Material.AIR
                                        && event.getClickedBlock().getType() != Material.CHEST) {
                                    for(Block near : Utils.getBlocksInRadius(event.getClickedBlock().getLocation(), 6, false)) {
                                        if(near.getType() == Material.CHEST || near.getType() == Material.BEACON) {
                                            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                    "&bGame> &7You are too close to another supply drop."));
                                            return;
                                        }
                                    }
                                    points-=25;
                                    new SupplyDrop(event.getClickedBlock());
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7A supply drop has been spawned."));
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7You must click a block to use this ability."));
                                }
                            }
                        }
                        if(d.equalsIgnoreCase("METEOR (50 points)")) {
                            if(points >= 50) {
                                Player target = LocationUtil.findNearestTarget();
                                if(target != null) {
                                    points-=50;
                                    new MeteoriteShower(target);
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7A meteorite shower is now targeting &e" + target.getName() + "&7."));
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7We couldn't find a player in a 60 block radius of you."));
                                }
                            }
                        }
                        if(d.equalsIgnoreCase("SNOWMEN HOARD (50 points)")) {
                            if(points >= 50) {
                                Player target = LocationUtil.findNearestTarget();
                                if(target != null) {
                                    points-=50;
                                    new SnowmenHoard(target);
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7A snowmen hoard is now targeting &e" + target.getName() + "&7."));
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7We couldn't find a player in a 60 block radius of you."));
                                }
                            }
                        }
                        if(d.equalsIgnoreCase("BUNNY MODE (60 points)")) {
                            if(points >= 60) {
                                points-=60;
                                for(Player p : InternalGameSettings.districtMap.keySet()) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000, 1), true);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 1000, 1), true);
                                }
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&bGame> &eBunny Mode &7has been activated!"));
                            }
                        }
                        if(d.equalsIgnoreCase("DEATH MODE (60 points)")) {
                            if(points >= 60) {
                                points-=60;
                                for(Player p : InternalGameSettings.districtMap.keySet()) {
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1), true);
                                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 150, 1), true);
                                }
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&bGame> &eDeath Mode &7has been activated!"));
                            }
                        }
                        if(d.equalsIgnoreCase("RESPAWN (200 points)")) {
                            if(points >= 200) {
                                points-=200;
                                int currentspawn = 0;
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    if(InternalGameSettings.districtMap.containsKey(p)) {
                                        Location spawnpoint = null;
                                        if(InternalGameSettings.map.getSpawnpoints().length >= currentspawn) {
                                            spawnpoint = InternalGameSettings.map.getSpawnpoints()[currentspawn];
                                        } else {
                                            spawnpoint = InternalGameSettings.map.getSpawnpoints()
                                                    [new Random().nextInt(InternalGameSettings.map.getSpawnpoints().length)];
                                        }
                                        p.teleport(spawnpoint);
                                        currentspawn++;
                                    } else {
                                        p.teleport(InternalGameSettings.map.getSpectator());
                                    }
                                }
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                                        "&bGame> &eRespawn &7has been activated!"));
                            }
                        }
                        if(d.equalsIgnoreCase("FIRE STORM (35 points)")) {
                            if(points >= 35) {
                                Player target = LocationUtil.findNearestTarget();
                                if(target != null) {
                                    points-=35;
                                    new FireStorm(target);
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7A storm of fire is now targeting &e" + target.getName() + "&7."));
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7We couldn't find a player in a 60 block radius of you."));
                                }
                            }
                        }
                        if(d.equalsIgnoreCase("ANIMAL KINGDOM (35 points)")) {
                            if(points >= 35) {
                                Player target = LocationUtil.findNearestTarget();
                                if(target != null) {
                                    points-=35;
                                    for(int i=0; i<5; i++) {
                                        Sheep sheep = target.getWorld().spawn(target.getLocation().clone().add(i*2, 0, i*2), Sheep.class);
                                        sheep.setCustomName("jeb_");
                                        sheep.setCustomNameVisible(true);
                                    }
                                    for(int i=0; i<2; i++) {
                                        target.getWorld().spawn(target.getLocation().clone().add(i*2, 0, i*2), Pig.class);
                                    }
                                    for(int i=0; i<2; i++) {
                                        target.getWorld().spawn(target.getLocation().clone().add(i*2, 0, i*2), Cow.class);
                                    }
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7We couldn't find a player in a 60 block radius of you."));
                                }
                            }
                        }
                        if(d.equalsIgnoreCase("FROSTBITE (100 points)")) {
                            if(points >= 100) {
                                Player target = null;
                                for(Entity n : event.getPlayer().getNearbyEntities(60, 60, 60)) {
                                    if(n instanceof Player) {
                                        Player t = (Player) n;
                                        if(t.getGameMode() != GameMode.CREATIVE
                                                && InternalGameSettings.districtMap.containsKey(t)) {
                                            target = t;
                                            break;
                                        }
                                    }
                                }
                                if(target != null) {
                                    points-=100;
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200), true);
                                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 200), true);
                                    final Player fT = target;
                                    new BukkitRunnable() {
                                        int radius = 0;
                                        int ticks = 0;
                                        Map<Block, Object[]> regen = new HashMap<Block, Object[]>();
                                        public void run() {
                                            if(ticks % 40 == 0) {
                                                if(radius >= 6) {
                                                    this.cancel();
                                                    fT.removePotionEffect(PotionEffectType.JUMP);
                                                    fT.removePotionEffect(PotionEffectType.SLOW);
                                                    fT.getWorld().playSound(fT.getLocation(), Sound.DIG_SNOW, 3, 1);
                                                    fT.getWorld().playSound(fT.getLocation(), Sound.STEP_SNOW, 3, 1);
                                                    for(Map.Entry<Block, Object[]> entry : regen.entrySet()) {
                                                        new BukkitRunnable() {
                                                            public void run() {
                                                                entry.getKey().setType((Material) entry.getValue()[0]);
                                                                entry.getKey().setType((Material) entry.getValue()[1]);
                                                            }
                                                        }.runTaskLater(FrostedGames.getInstance(), 120+(2*new Random().nextInt(6)));
                                                    }
                                                } else {
                                                    for(Block b : Utils.getBlocksInRadius(fT.getLocation(), radius, false)) {
                                                        if(b.getType() != Material.ICE && b.getType() != Material.AIR) {
                                                            regen.put(b, new Object[]{b.getType(), b.getData()});
                                                            b.setType(Material.ICE);
                                                        }
                                                    }
                                                    fT.getWorld().playSound(fT.getLocation(), Sound.STEP_SNOW, 3, 1);
                                                    radius++;
                                                }
                                            }
                                            if(fT.isOnline()) {
                                                for(Entity near : fT.getNearbyEntities(5, 5, 5)) {
                                                    if(near.getName() != fT.getName()) {
                                                        MathUtils.fling(near, 2);
                                                    }
                                                }
                                                fT.playEffect(EntityEffect.HURT);
                                            }
                                            ticks++;
                                        }
                                    }.runTaskTimer(FrostedGames.getInstance(), 0, 1L);
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7We couldn't find a player in a 60 block radius of you."));
                                }
                            }
                        }
                        if(d.equalsIgnoreCase("OWEN APOCALYPSE (60 points)")) {
                            if(points >= 60) {
                                Player target = LocationUtil.findNearestTarget();
                                if(target != null) {
                                    points-=60;
                                    new OwenApocalypse(target);
                                } else {
                                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                                            "&bGame> &7We couldn't find a player in a 60 block radius of you."));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setup() {
        pages.put(0, new ItemStack[]{
                ItemCreator.getInstance().createItem(Material.CHEST, 1, 0, "&e&lSUPPLY DROP &7(25 points)"),
                ItemCreator.getInstance().createItem(Material.FIREBALL, 1, 0, "&e&lMETEOR &7(50 points)"),
                ItemCreator.getInstance().createItem(Material.SNOW_BLOCK, 1, 0, "&e&lSNOWMEN HOARD &7(50 points)"),
                ItemCreator.getInstance().createItem(Material.RABBIT_FOOT, 1, 0, "&e&lBUNNY MODE &7(60 points)"),
                ItemCreator.getInstance().createItem(Material.IRON_HOE, 1, 0, "&e&lDEATH MODE &7(60 points)"),
                ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&e&lRESPAWN &7(200 points)"),
                ItemCreator.getInstance().createItem(Material.FIREWORK_CHARGE, 1, 0, "&e&lFIRE STORM &7(35 points)")
        });
        pages.put(1, new ItemStack[]{
                ItemCreator.getInstance().createItem(Material.SADDLE, 1, 0, "&e&lANIMAL KINGDOM &7(35 points)"),
                ItemCreator.getInstance().createItem(Material.ICE, 1, 0, "&e&lFROSTBITE &7(100 points)"),
                ItemCreator.getInstance().createItem(Material.DIAMOND, 1, 0, "&e&lOWEN APOCALYPSE &7(60 points)")
        });
    }

    public void setPage(int page) {
        this.currentPage = page;
        player.getInventory().clear();
        if(page != 0) {
            player.getInventory().setItem(0, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&l< Previous Page"));
        }
        if(pages.containsKey((page+1))) {
            player.getInventory().setItem(8, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&lNext Page >"));
        }
        for(int i=0; i<pages.get(page).length; i++) {
            player.getInventory().setItem((i+1), pages.get(page)[i]);
        }
    }
}