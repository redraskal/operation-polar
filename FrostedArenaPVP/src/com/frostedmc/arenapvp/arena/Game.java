package com.frostedmc.arenapvp.arena;

import com.frostedmc.arenapvp.ArenaPVP;
import com.frostedmc.arenapvp.BarUtil;
import com.frostedmc.arenapvp.FireworkUtil;
import com.frostedmc.arenapvp.VelocityUtil;
import com.frostedmc.arenapvp.arena.elo.EloManager;
import com.frostedmc.arenapvp.manager.ArenaManager;
import com.frostedmc.arenapvp.manager.ScoreboardManager;
import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.AccountDetails;
import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.core.utils.Title;
import me.redraskal.Glacier.check.CheckManager;
import me.redraskal.Glacier.check.ViolationEvent;
import me.redraskal.Glacier.check.defaults.chat.AdvertisingCheck;
import me.redraskal.Glacier.check.defaults.chat.SpamCheck;
import me.redraskal.Glacier.check.defaults.movement.SpeedCheck;
import me.redraskal.Glacier.frostbyte.Analyser;
import me.redraskal.Glacier.utils.Utils;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Random;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class Game implements Listener {

    private Game instance;
    private Arena arena;
    private Player player1;
    private Player player2;
    private Kit kit;
    private double duration = 0.0D;
    public boolean start = false;
    public boolean end = false;
    public boolean fullyEnd = false;
    private GameType gameType;

    private int criticalHits1 = 0;
    private int criticalHits2 = 0;

    private double damageDealt1 = 0;
    private double damageDealt2 = 0;

    private long lastPearl1 = System.currentTimeMillis();
    private long lastPearl2 = System.currentTimeMillis();

    public Game(Player player1, Player player2, Arena arena, Kit kit, GameType gameType) {
        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You are now fighting &b"
                + player2.getName() + "&7."));
        player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You are now fighting &b"
                + player1.getName() + "&7."));
        CheckManager.getInstance().getViolationHistory(player1)
                .clear(CheckManager.getInstance().byClass(AdvertisingCheck.class));
        CheckManager.getInstance().getViolationHistory(player1)
                .clear(CheckManager.getInstance().byClass(SpamCheck.class));
        CheckManager.getInstance().getViolationHistory(player2)
                .clear(CheckManager.getInstance().byClass(AdvertisingCheck.class));
        CheckManager.getInstance().getViolationHistory(player2)
                .clear(CheckManager.getInstance().byClass(SpamCheck.class));
        JukeboxManager.getInstance().update(player1.getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/PVPGameMusic1.mp3");
        JukeboxManager.getInstance().update(player2.getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/PVPGameMusic1.mp3");
        Objective objective = player1
                .getScoreboard().registerNewObjective("title", "health");
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7/ &f20 &c♥"));
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        Objective objective2 = player2
                .getScoreboard().registerNewObjective("title", "health");
        objective2.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&7/ &f20 &c♥"));
        objective2.setDisplaySlot(DisplaySlot.BELOW_NAME);
        player1.setHealth(player1.getHealth());
        player2.setHealth(player2.getHealth());
        this.instance = this;
        this.gameType = gameType;
        this.player1 = player1;
        this.player2 = player2;
        this.arena = arena;
        this.kit = kit;
        this.init();
        player1.setNoDamageTicks(20);
        player1.setMaximumNoDamageTicks(20);
        player2.setNoDamageTicks(20);
        player2.setMaximumNoDamageTicks(20);
        if(this.kit == Kit.COMBO) {
            player1.setNoDamageTicks(0);
            player1.setMaximumNoDamageTicks(0);
            player2.setNoDamageTicks(0);
            player2.setMaximumNoDamageTicks(0);
        }
        if(this.kit == Kit.ROBINHOOD) {
            player1.setNoDamageTicks(15);
            player1.setMaximumNoDamageTicks(15);
            player2.setNoDamageTicks(15);
            player2.setMaximumNoDamageTicks(15);
        }
        if(this.kit == Kit.NINJA) {
            player1.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
            player2.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
        }
        if(!Analyser.ongoing(player1)) {
            new Analyser(player1);
        }
        if(!Analyser.ongoing(player2)) {
            new Analyser(player2);
        }
        ArenaPVP.getInstance().getServer().getPluginManager().registerEvents(this, ArenaPVP.getInstance());
        new BukkitRunnable() {
            public double countdown = 5.0D;
            public void run() {
                if(countdown <= 0.0D) {
                    this.cancel();
                    start = true;
                    SimpleScoreboard board1 = ScoreboardManager.getInstance().playerBoards.get(player1);
                    board1.add("&3Duration » &f0.0s", 5);
                    board1.update();
                    SimpleScoreboard board2 = ScoreboardManager.getInstance().playerBoards.get(player2);
                    board2.add("&3Duration » &f0.0s", 5);
                    board2.update();
                    player1.playSound(player1.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player2.playSound(player2.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player1.setVelocity(player1.getLocation().getDirection().multiply(4.3D).setY(0.23D));
                    player2.setVelocity(player2.getLocation().getDirection().multiply(4.3D).setY(0.23D));
                    player1.playSound(player1.getLocation(), Sound.IRONGOLEM_THROW, 1, 2);
                    player2.playSound(player2.getLocation(), Sound.IRONGOLEM_THROW, 1, 2);
                    player1.getWorld().playEffect(player1.getLocation().add(0, new Random().nextDouble() + 0.5D, 0), Effect.SMOKE, 5);
                    player2.getWorld().playEffect(player2.getLocation().add(0, new Random().nextDouble() + 0.5D, 0), Effect.SMOKE, 5);
                    new BukkitRunnable() {
                        public void run() {
                            if(!end) {
                                duration+=0.1D;
                                DecimalFormat df = new DecimalFormat("#.#");
                                String co = df.format(duration);
                               if(!player1.isOnline() || !player2.isOnline()) {
                                   this.cancel();
                                   if(player1.isOnline()) {
                                       player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lThe game was ended due to a player logging out."));
                                   } else {
                                       player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lThe game was ended due to a player logging out."));
                                   }
                                   end();
                               } else {
                                   SimpleScoreboard board1 = ScoreboardManager.getInstance().playerBoards.get(player1);
                                   board1.add("&3Duration » &f" + co + "s", 5);
                                   board1.update();
                                   SimpleScoreboard board2 = ScoreboardManager.getInstance().playerBoards.get(player2);
                                   board2.add("&3Duration » &f" + co + "s", 5);
                                   board2.update();
                                   if(duration >= 400) {
                                       this.cancel();
                                       if(player1.isOnline())
                                           player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lThe game was ended due to the time limit of 400 seconds."));
                                       if(player2.isOnline())
                                           player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6&lThe game was ended due to the time limit of 400 seconds."));
                                       end();
                                   }
                               }
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(ArenaPVP.getInstance(), 0, 2L);
                } else {
                    countdown-=0.1D;
                    DecimalFormat df = new DecimalFormat("#.#");
                    String co = df.format(countdown);
                    SimpleScoreboard board1 = ScoreboardManager.getInstance().playerBoards.get(player1);
                    board1.add("&3Starting in » &f" + co + "s", 5);
                    board1.update();
                    SimpleScoreboard board2 = ScoreboardManager.getInstance().playerBoards.get(player2);
                    board2.add("&3Starting in » &f" + co + "s", 5);
                    board2.update();
                    if(!co.contains(".") && (!co.equalsIgnoreCase("0.0") && !co.equalsIgnoreCase("0"))) {
                        player1.playSound(player1.getLocation(), Sound.NOTE_PIANO, 1, 1);
                        player2.playSound(player2.getLocation(), Sound.NOTE_PIANO, 1, 1);
                    }
                }
            }
        }.runTaskTimer(ArenaPVP.getInstance(), 0, 2L);
    }

    @EventHandler
    public void onViolation(ViolationEvent event) {
        if(event.getViolation().getPlayer() == player1 || event.getViolation().getPlayer() == player2) {
            if(!start) {
                event.setCancelled(true);
            } else {
                if(event.getViolation().getCheck().getName().equalsIgnoreCase("Hover")
                        || event.getViolation().getCheck().getName().equalsIgnoreCase("Speed")) {
                    if(kit == Kit.ROCKETEER) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() == player1 || event.getEntity() == player2) {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setDamage(0);
            }
            double health = (((Player) event.getEntity()).getHealth() - event.getFinalDamage());
            if(health <= 0) {
                event.setCancelled(true);
                if(event.getEntity() == player1) {
                    death((Player) event.getEntity(), player2);
                } else {
                    death((Player) event.getEntity(), player1);
                }
            }
        }
        if(end) {
            if(event.getEntity() == player1 || event.getEntity() == player2) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getWhoClicked() == player1 || event.getWhoClicked() == player2) {
            if(event.getCurrentItem() != null) {
                if(event.getCurrentItem().getType() == Material.GOLD_SWORD) {
                    if(event.getCurrentItem().getAmount() > 1) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        if(event.getPlayer() == player1 || event.getPlayer() == player2) {
            if(event.getItem().getType() == Material.POTION) {
                new BukkitRunnable() {
                    public void run() {
                        if(event.getPlayer().isOnline()) {
                            event.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
                        }
                    }
                }.runTaskLater(ArenaPVP.getInstance(), 5L);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if(event.getEntity().hasMetadata("robinhood")) {
            event.getEntity().remove();
        }
        if(kit == Kit.NINJA) {
            if(event.getEntity().getType() == EntityType.SNOWBALL) {
                ParticleEffect.CLOUD.display(3, 3, 3, 0f, 600, event.getEntity().getLocation(), 60);
                ParticleEffect.SMOKE_LARGE.display(3, 3, 3, 0f, 550, event.getEntity().getLocation(), 60);
                event.getEntity().getWorld().playSound(event.getEntity().getLocation(), Sound.GHAST_FIREBALL, 5, 1);
            }
        }
    }

    @EventHandler
    public void onProjectileShoot(EntityShootBowEvent event) {
        if(event.getEntity() == player1 || event.getEntity() == player2) {
            if(kit == Kit.ROBINHOOD) {
                event.getProjectile().setMetadata("robinhood", new FixedMetadataValue(ArenaPVP.getInstance(), true));
                event.getProjectile().setMetadata("robinhood_d", new FixedMetadataValue(ArenaPVP.getInstance(), event.getEntity().getName()));
                new BukkitRunnable() {
                    public void run() {
                        if(event.getProjectile().isDead()) {
                            this.cancel();
                            ParticleEffect.SNOW_SHOVEL.display(1, 1, 1, 0, 5, event.getProjectile().getLocation(), 15);
                            ParticleEffect.FIREWORKS_SPARK.display(1, 1, 1, 0, 5, event.getProjectile().getLocation(), 15);
                            event.getProjectile().getWorld().playSound(event.getProjectile().getLocation(), Sound.DIG_SNOW, 1, 1);
                        } else {
                            ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 3, event.getProjectile().getLocation(), 15);
                            ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 3, event.getProjectile().getLocation(), 15);
                        }
                    }
                }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);
            } else {
                event.getProjectile().setMetadata("robinhood", new FixedMetadataValue(ArenaPVP.getInstance(), true));
                event.getProjectile().setMetadata("robinhood_d", new FixedMetadataValue(ArenaPVP.getInstance(), event.getEntity().getName()));
                event.getProjectile().setMetadata("ignore_kit", new FixedMetadataValue(ArenaPVP.getInstance(), true));
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(EntityDamageByEntityEvent event) {
        if(event.getDamager() instanceof Arrow) {
            if(event.getDamager().hasMetadata("robinhood")) {
                if(event.getEntity() == player1 || event.getEntity() == player2) {
                    Player d = (Player) event.getEntity();
                    if(!event.getDamager().hasMetadata("ignore_kit")) {
                        d.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 60, 1), true);
                    }
                    DecimalFormat df = new DecimalFormat("#.#");
                    Bukkit.getPlayer(event.getDamager().getMetadata("robinhood_d").get(0).asString())
                            .sendMessage(ChatColor.translateAlternateColorCodes('&', "&b" + d.getName() + " &7currently has &c" + df.format(d.getHealth()) + " &4♥"));
                    double health = (((Player) event.getEntity()).getHealth() - 4.5D);
                    if(health <= 0) {
                        event.setCancelled(true);
                        death(d, Bukkit.getPlayer(event.getDamager().getMetadata("robinhood_d").get(0).asString()));
                    } else {
                        event.setDamage(4.5D);
                    }
                    event.getDamager().remove();
                }
            }
        }
        if(event.getDamager() == player1 || event.getDamager() == player2) {
            event.getEntity().getWorld().playEffect(event.getEntity().getLocation().add(0, new Random().nextDouble() + 0.5D, 0), Effect.STEP_SOUND, 152);
            event.getEntity().getLocation().getWorld().playSound(event.getEntity().getLocation(), Sound.SHOOT_ARROW, 1, 2);
        }
        if(end)
            return;
        if(event.getDamager() == player1 || event.getDamager() == player2) {
            if(isCriticalHit((Player) event.getDamager())) {
                if(event.getDamager() == player1) {
                    criticalHits1++;
                } else {
                    criticalHits2++;
                }
            }
            if(event.getDamager() == player1) {
                damageDealt1+=event.getDamage();
            } else {
                damageDealt2+=event.getDamage();
            }
            double health = (((Player) event.getEntity()).getHealth() - event.getFinalDamage());
            if(health <= 0) {
                event.setCancelled(true);
                death((Player) event.getEntity(), (Player) event.getDamager());
            }
        }
    }

    private boolean isCriticalHit(Player player) {
        return player.getFallDistance() > 0.0f
                && !((CraftPlayer) player).getHandle().onGround
                && player.getEyeLocation().getBlock().getType() != Material.LADDER
                && player.getEyeLocation().getBlock().getType() != Material.VINE
                && !Utils.hoveringOverLiquid(player)
                && !player.hasPotionEffect(PotionEffectType.BLINDNESS)
                && !player.isInsideVehicle();
    }

    private void death(Player damaged, Player damager) {
        DecimalFormat df = new DecimalFormat("#.#");
        String co = df.format(duration);
        ParticleEffect.CRIT_MAGIC.display(1, 1, 1, 0, 6, damaged.getLocation(), 15);
        damaged.getLocation().getWorld().playSound(damaged.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 2);
        if(damager == player1) {
            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 10, 1);
        } else {
            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 10, 1);
        }
        ParticleEffect.EXPLOSION_NORMAL.display(1, 1, 1, 0, 7, damaged.getLocation(), 15);
        ParticleEffect.EXPLOSION_LARGE.display(1, 1, 1, 0, 4, damaged.getLocation(), 15);
        for(int i=0; i<24; i++) {
            Location randomLocation = damaged.getLocation().clone();
            randomLocation.setY(randomLocation.getY()+10+new Random().nextInt(3));
            if(new Random().nextDouble() >= .5) {
                randomLocation.setX(randomLocation.getX()-9-new Random().nextInt(8));
            } else {
                randomLocation.setX(randomLocation.getX()+9+new Random().nextInt(8));
            }
            if(new Random().nextDouble() <= .5) {
                randomLocation.setZ(randomLocation.getZ()-9-new Random().nextInt(8));
            } else {
                randomLocation.setZ(randomLocation.getZ()+9+new Random().nextInt(8));
            }
            FireworkUtil.shootRandomFirework(randomLocation);
        }
        JukeboxManager.getInstance().stopMusic(player1.getName());
        JukeboxManager.getInstance().stopMusic(player2.getName());
        JukeboxManager.getInstance().update(player1.getName(), SoundType.SOUND_EFFECT, "https://downloads.frostedmc.com/music/effects/LevelUp2.mp3");
        JukeboxManager.getInstance().update(player2.getName(), SoundType.SOUND_EFFECT, "https://downloads.frostedmc.com/music/effects/LevelUp2.mp3");
        int durationElo = 0;
        if(duration <= 10) {
            durationElo = 50;
        }
        if(duration <= 20 && duration > 10) {
            durationElo = 45;
        }
        if(duration <= 30 && duration > 20) {
            durationElo = 40;
        }
        if(duration <= 40 && duration > 30) {
            durationElo = 35;
        }
        if(duration <= 50 && duration > 40) {
            durationElo = 30;
        }
        if(duration <= 60 && duration > 50) {
            durationElo = 25;
        }
        if(duration > 60.0D) {
            durationElo = 20;
        }
        final int finalDurationElo = durationElo;
        if(kit == Kit.COMBO) {
            damageDealt1 = (damageDealt1 / 4);
            damageDealt2 = (damageDealt2 / 4);
            criticalHits1 = (criticalHits1 / 2);
            criticalHits2 = (criticalHits2 / 2);
        }
        if(damaged == player1) {
            //TODO: Player1 died
            if(this.gameType == GameType.RANKED) {
                Core.getInstance().getStatisticsManager().add(player2.getUniqueId(), "arenapvp_wins_ranked", 1);
                Core.getInstance().getStatisticsManager().add(player1.getUniqueId(), "arenapvp_deaths_ranked", 1);
            } else {
                Core.getInstance().getStatisticsManager().add(player2.getUniqueId(), "arenapvp_wins_unranked", 1);
                Core.getInstance().getStatisticsManager().add(player1.getUniqueId(), "arenapvp_deaths_unranked", 1);
            }
            player1.playSound(player1.getLocation(), Sound.WITHER_DEATH, 1, 2);
            ParticleEffect.MOB_APPEARANCE.display(0, 0, 0, 0, 1, player1.getLocation(), player1);
            player2.hidePlayer(player1);
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have beaten &e" + player1.getName() + " &7after &e" + co + " &7seconds."));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hearts left: &c" + df.format(player2.getHealth()) + " &4♥"));
            //player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+&a100 elo"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));

            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have been defeated by &e" + player2.getName() + " &7after &e" + co + " &7seconds."));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hearts left: &c" + df.format(player2.getHealth()) + " &4♥"));
            //player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l-&c100 elo"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));

            if(gameType == GameType.UNRANKED) {
                new BukkitRunnable() {
                    public void run() {
                        fullyEnd = true;
                        end();
                    }
                }.runTaskLater(ArenaPVP.getInstance(), 60L);
                end = true;
                player1.setHealth(20);
                player2.setHealth(20);
                return;
            }

            int healthElo = 0;
            if(player2.getHealth() >= 20) {
                healthElo = 11;
            }
            if(player2.getHealth() < 20 && player2.getHealth() > 15) {
                healthElo = 9;
            }
            if(player2.getHealth() < 15 && player2.getHealth() > 10) {
                healthElo = 7;
            }
            if(player2.getHealth() < 10 && player2.getHealth() >= 5) {
                healthElo = 5;
            }
            if(player2.getHealth() < 5) {
                healthElo = 3;
            }
            final int finalHealthElo = healthElo;
            final int currentElo = EloManager.getInstance().fetchProfile(player2.getUniqueId()).getElo(kit);
            final int criticalHitsElo = (criticalHits2*3);
            final int damageDealtElo = (Double.valueOf(damageDealt2).intValue()/4);
            final int totalElo = (currentElo+finalDurationElo+finalHealthElo+criticalHitsElo+damageDealtElo);
            EloManager.getInstance().updateElo(player2.getUniqueId(), kit, totalElo);

            new BukkitRunnable() {
                int ticks = 0;
                int tempDurationElo = 0;
                int tempHealthElo = 0;
                int tempCriticalHitsElo = 0;
                int tempDamageDealtElo = 0;
                int tempTotalElo = currentElo;
                int frame = 0;
                boolean flicker = true;
                public void kk() { this.cancel(); }
                public void run() {
                    if(frame == 0) {
                        if(tempDurationElo != finalDurationElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempDurationElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eDuration Reward &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempDurationElo++;
                        } else {
                            frame = 1;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 2;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 2) {
                        if(tempHealthElo != finalHealthElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempHealthElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eHealth Reward &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempHealthElo++;
                        } else {
                            frame = 3;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 4;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 4) {
                        if(tempCriticalHitsElo != criticalHitsElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempCriticalHitsElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eCritical Hits Reward &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempCriticalHitsElo++;
                        } else {
                            frame = 5;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 6;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 6) {
                        if(tempDamageDealtElo != damageDealtElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempDamageDealtElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eDamage Dealt Reward &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempDamageDealtElo++;
                        } else {
                            frame = 7;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 8;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 8) {
                        if(tempTotalElo != totalElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eNew elo for " + kit.getName() + " &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempTotalElo++;
                        } else {
                            frame = 9;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.VILLAGER_YES, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    kk();
                                    fullyEnd = true;
                                    end();
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 60L);
                        }
                    }
                    if(frame == 9) {
                        if(flicker) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &a" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&f&lYou won the match!"), 0, 1, 0);
                            title.send(player2);
                        } else {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &a" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&a&lYou won the match!"), 0, 1, 0);
                            title.send(player2);
                        }
                        flicker = !flicker;
                    }
                    ticks++;
                }
            }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);

            final int currentElo2 = EloManager.getInstance().fetchProfile(player1.getUniqueId()).getElo(kit);;
            final int totalElo2 = (currentElo2-finalDurationElo);
            EloManager.getInstance().updateElo(player1.getUniqueId(), kit, totalElo2);

            new BukkitRunnable() {
                int ticks = 0;
                int tempDurationElo = 0;
                int tempTotalElo = currentElo2;
                int frame = 0;
                boolean flicker = true;
                public void kk() { this.cancel(); }
                public void run() {
                    if(frame == 0) {
                        if(tempDurationElo != finalDurationElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&c&l-&6" + tempDurationElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eDuration Consequence &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempDurationElo++;
                        } else {
                            frame = 1;
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 2;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 2) {
                        if(tempTotalElo != totalElo2) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&c" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eNew elo for " + kit.getName() + " &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempTotalElo--;
                        } else {
                            frame = 3;
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.VILLAGER_YES, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    if(fullyEnd) {
                                        this.cancel();
                                        kk();
                                    }
                                }
                            }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);
                        }
                    }
                    if(frame == 3) {
                        if(flicker) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &c" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&f&lYou lost the match!"), 0, 1, 0);
                            title.send(player1);
                        } else {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &c" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&c&lYou lost the match!"), 0, 1, 0);
                            title.send(player1);
                        }
                        flicker = !flicker;
                    }
                    ticks++;
                }
            }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);
        } else {
            //TODO: Player2 died
            if(this.gameType == GameType.RANKED) {
                Core.getInstance().getStatisticsManager().add(player1.getUniqueId(), "arenapvp_wins_ranked", 1);
                Core.getInstance().getStatisticsManager().add(player2.getUniqueId(), "arenapvp_deaths_ranked", 1);
            } else {
                Core.getInstance().getStatisticsManager().add(player1.getUniqueId(), "arenapvp_wins_unranked", 1);
                Core.getInstance().getStatisticsManager().add(player2.getUniqueId(), "arenapvp_deaths_unranked", 1);
            }
            player2.playSound(player2.getLocation(), Sound.WITHER_DEATH, 1, 2);
            ParticleEffect.MOB_APPEARANCE.display(0, 0, 0, 0, 1, player2.getLocation(), player2);
            player1.hidePlayer(player2);
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have beaten &e" + player2.getName() + " &7after &e" + co + " &7seconds."));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hearts left: &c" + df.format(player1.getHealth()) + " &4♥"));
            //player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&l+&a10 elo"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player1.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));

            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7You have been defeated by &e" + player1.getName() + " &7after &e" + co + " &7seconds."));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Hearts left: &c" + df.format(player1.getHealth()) + " &4♥"));
            //player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&l-&c10 elo"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b"));
            player2.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b&m----------------------------------------------------"));

            if(gameType == GameType.UNRANKED) {
                new BukkitRunnable() {
                    public void run() {
                        fullyEnd = true;
                        end();
                    }
                }.runTaskLater(ArenaPVP.getInstance(), 60L);
                end = true;
                player1.setHealth(20);
                player2.setHealth(20);
                return;
            }

            int healthElo = 0;
            if(player1.getHealth() >= 20) {
                healthElo = 11;
            }
            if(player1.getHealth() < 20 && player1.getHealth() > 15) {
                healthElo = 9;
            }
            if(player1.getHealth() < 15 && player1.getHealth() > 10) {
                healthElo = 7;
            }
            if(player1.getHealth() < 10 && player1.getHealth() >= 5) {
                healthElo = 5;
            }
            if(player1.getHealth() < 5) {
                healthElo = 3;
            }
            final int finalHealthElo = healthElo;
            final int currentElo = EloManager.getInstance().fetchProfile(player1.getUniqueId()).getElo(kit);;
            final int criticalHitsElo = (criticalHits1*3);
            final int damageDealtElo = (Double.valueOf(damageDealt1).intValue()/4);
            final int totalElo = (currentElo+finalDurationElo+finalHealthElo+criticalHitsElo+damageDealtElo);
            EloManager.getInstance().updateElo(player1.getUniqueId(), kit, totalElo);

            new BukkitRunnable() {
                int ticks = 0;
                int tempDurationElo = 0;
                int tempHealthElo = 0;
                int tempCriticalHitsElo = 0;
                int tempDamageDealtElo = 0;
                int tempTotalElo = currentElo;
                int frame = 0;
                boolean flicker = true;
                public void kk() { this.cancel(); }
                public void run() {
                    if(frame == 0) {
                        if(tempDurationElo != finalDurationElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempDurationElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eDuration Reward &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempDurationElo++;
                        } else {
                            frame = 1;
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 2;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 2) {
                        if(tempHealthElo != finalHealthElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempHealthElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eHealth Reward &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempHealthElo++;
                        } else {
                            frame = 3;
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 4;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 4) {
                        if(tempCriticalHitsElo != criticalHitsElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempCriticalHitsElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eCritical Hits Reward &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempCriticalHitsElo++;
                        } else {
                            frame = 5;
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempCriticalHitsElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eCritical Hits Reward &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 6;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 6) {
                        if(tempDamageDealtElo != damageDealtElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a&l+&6" + tempDamageDealtElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eDamage Dealt Reward &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempDamageDealtElo++;
                        } else {
                            frame = 7;
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 8;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 8) {
                        if(tempTotalElo != totalElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&a" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eNew elo for " + kit.getName() + " &e&kB"), 0, 1, 0);
                            title.send(player1);
                            player1.playSound(player1.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempTotalElo++;
                        } else {
                            frame = 9;
                            player1.playSound(player1.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player1.playSound(player1.getLocation(), Sound.LEVEL_UP, 1, 1);
                            player1.playSound(player1.getLocation(), Sound.VILLAGER_YES, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    kk();
                                    fullyEnd = true;
                                    end();
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 20L);
                        }
                    }
                    if(frame == 9) {
                        if(flicker) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &a" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&f&lYou won the match!"), 0, 1, 0);
                            title.send(player1);
                        } else {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &a" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&a&lYou won the match!"), 0, 1, 0);
                            title.send(player1);
                        }
                        flicker = !flicker;
                    }
                    ticks++;
                }
            }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);

            final int currentElo2 = EloManager.getInstance().fetchProfile(player2.getUniqueId()).getElo(kit);
            final int totalElo2 = (currentElo2-finalDurationElo);
            EloManager.getInstance().updateElo(player2.getUniqueId(), kit, totalElo2);

            new BukkitRunnable() {
                int ticks = 0;
                int tempDurationElo = 0;
                int tempTotalElo = currentElo2;
                int frame = 0;
                boolean flicker = true;
                public void kk() { this.cancel(); }
                public void run() {
                    if(frame == 0) {
                        if(tempDurationElo != finalDurationElo) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&c&l-&6" + tempDurationElo + " elo"),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eDuration Consequence &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempDurationElo++;
                        } else {
                            frame = 1;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    frame = 2;
                                }
                            }.runTaskLater(ArenaPVP.getInstance(), 15L);
                        }
                    }
                    if(frame == 2) {
                        if(tempTotalElo != totalElo2) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&c" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&e&kB &eNew elo for " + kit.getName() + " &e&kB"), 0, 1, 0);
                            title.send(player2);
                            player2.playSound(player2.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
                            tempTotalElo--;
                        } else {
                            frame = 3;
                            player2.playSound(player2.getLocation(), Sound.FIREWORK_LARGE_BLAST, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.ORB_PICKUP, 1, 2);
                            player2.playSound(player2.getLocation(), Sound.LEVEL_UP, 1, 1);
                            player2.playSound(player2.getLocation(), Sound.VILLAGER_YES, 1, 1);
                            new BukkitRunnable() {
                                public void run() {
                                    if(fullyEnd) {
                                        this.cancel();
                                        kk();
                                    }
                                }
                            }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);
                        }
                    }
                    if(frame == 3) {
                        if(flicker) {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &c" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&f&lYou lost the match!"), 0, 1, 0);
                            title.send(player2);
                        } else {
                            Title title = new Title(ChatColor.translateAlternateColorCodes('&', "&eNew elo: &c" + tempTotalElo),
                                    ChatColor.translateAlternateColorCodes('&', "&c&lYou lost the match!"), 0, 1, 0);
                            title.send(player2);
                        }
                        flicker = !flicker;
                    }
                    ticks++;
                }
            }.runTaskTimer(ArenaPVP.getInstance(), 0, 1L);
        }
        end = true;
        player1.setHealth(20);
        player2.setHealth(20);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!start) {
            if(event.getPlayer() == player1 || event.getPlayer() == player2) {
                Location newLocation = event.getFrom();
                newLocation.setY(event.getTo().getY());
                newLocation.setYaw(event.getTo().getYaw());
                newLocation.setPitch(event.getTo().getPitch());
                event.setTo(newLocation);
            }
        } else {
            if(event.getPlayer() == player1 || event.getPlayer() == player2) {
                if(event.getPlayer().getLocation().getY() <= 16) {
                    Location newTo;
                    if(new Random().nextBoolean()) {
                        newTo = this.arena.getSpawnpointA();
                    } else {
                        newTo = this.arena.getSpawnpointB();
                    }
                    event.setTo(newTo);
                }
                if(event.getPlayer().getLocation().getBlock().getType() == Material.SLIME_BLOCK
                        || event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
                    if(CheckManager.getInstance().byClass(SpeedCheck.class) != null) {
                        CheckManager.getInstance().getViolationHistory(event.getPlayer()).clear(CheckManager.getInstance().byClass(SpeedCheck.class));
                    }
                    event.setTo(event.getTo().add(0, 0.5, 0));
                    event.getPlayer().setVelocity(new Vector(0, 1.5, 0));
                    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
                    event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.IRONGOLEM_THROW, 1, 1);
                    ParticleEffect.FIREWORKS_SPARK.display(1, 1, 1, 0, 5, event.getPlayer().getLocation(), 15);
                    ParticleEffect.SMOKE_NORMAL.display(1, 1, 1, 0, 5, event.getPlayer().getLocation(), 15);
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 80, 100));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPearl(ProjectileLaunchEvent event) {
        if(!start) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPearl(PlayerInteractEvent event) {
        if(event.getPlayer() == player1 || event.getPlayer() == player2) {
            if(event.getItem() != null) {
                if(event.getItem().getType() == Material.ENDER_PEARL) {
                    if(!start) {
                        event.setCancelled(true);
                        event.setUseItemInHand(Event.Result.DENY);
                        return;
                    }
                    if(event.getPlayer() == player1) {
                        if((System.currentTimeMillis() - lastPearl1) <= 10000) {
                            event.setCancelled(true);
                            event.setUseItemInHand(Event.Result.DENY);
                            String left = new DecimalFormat("#.#").format(10-((System.currentTimeMillis() - lastPearl1)/1000));
                            event.getPlayer().sendMessage(
                                    ChatColor.translateAlternateColorCodes('&', "&bGame> &7Your ender pearls are currently on cooldown for &e" + left
                                            + " seconds&7. Wait a few seconds and try again."));
                            return;
                        } else {
                            lastPearl1 = System.currentTimeMillis();
                        }
                    }
                    if(event.getPlayer() == player2) {
                        if((System.currentTimeMillis() - lastPearl2) <= 10000) {
                            event.setCancelled(true);
                            event.setUseItemInHand(Event.Result.DENY);
                            String left = new DecimalFormat("#.#").format(10-((System.currentTimeMillis() - lastPearl2)/1000));
                            event.getPlayer().sendMessage(
                                    ChatColor.translateAlternateColorCodes('&', "&bGame> &7Your ender pearls are currently on cooldown for &e" + left
                                            + " seconds&7. Wait a few seconds and try again."));
                            return;
                        } else {
                            lastPearl2 = System.currentTimeMillis();
                        }
                    }
                }
                if(start) {
                    if(event.getItem().getType() == Material.GOLD_SWORD && event.getItem().getAmount() == 1) {
                        if(kit == Kit.ROCKETEER) {
                            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                new BukkitRunnable() {
                                    int current = 100;
                                    public void run() {
                                        if(!event.getPlayer().isOnline()
                                                || !event.getPlayer().isBlocking() || end || current <= 0) {
                                            this.cancel();
                                            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.IRONGOLEM_THROW, 1, 1);
                                            if(!end && event.getPlayer().isOnline()) {
                                                ItemStack newItem = event.getPlayer().getInventory().getItem(0);
                                                newItem.setAmount(30);
                                                event.getPlayer().getInventory().setItem(0, newItem);
                                                new BukkitRunnable() {
                                                    int countdown = 30;
                                                    public void run() {
                                                        if(event.getPlayer().isOnline() && !end) {
                                                            if(countdown > 0) {
                                                                ItemStack newItem = event.getPlayer().getInventory().getItem(0);
                                                                newItem.setAmount(countdown);
                                                                event.getPlayer().getInventory().setItem(0, newItem);
                                                                countdown--;
                                                            } else {
                                                                this.cancel();
                                                                ItemStack newItem = event.getPlayer().getInventory().getItem(0);
                                                                newItem.setAmount(1);
                                                                event.getPlayer().getInventory().setItem(0, newItem);
                                                            }
                                                        } else {
                                                            this.cancel();
                                                        }
                                                    }
                                                }.runTaskTimer(ArenaPVP.getInstance(), 0, 20L);
                                            }
                                        } else {
                                            boolean t = false;
                                            for(Entity near : event.getPlayer().getNearbyEntities(4, 4, 4)) {
                                                if(near instanceof Player) {
                                                    Player n = (Player) near;
                                                    if(n != event.getPlayer()) {
                                                        if(n == player1 || n == player2) {
                                                            t = true;
                                                            event.getPlayer().getInventory().setHeldItemSlot(1);
                                                            event.getPlayer().getInventory().setHeldItemSlot(0);
                                                            if(n.getInventory().getHeldItemSlot() == 0) {
                                                                n.getInventory().setHeldItemSlot(1);
                                                                n.getInventory().setHeldItemSlot(0);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 12, event.getPlayer().getLocation(), 15);
                                            ParticleEffect.FLAME.display(0, 0, 0, 0, 12, event.getPlayer().getLocation(), 15);
                                            ParticleEffect.CLOUD.display(0, 0, 0, 0, 6, event.getPlayer().getLocation(), 15);
                                            event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.FIZZ, .1f, 1);
                                            if(t) {
                                                this.cancel();
                                                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.IRONGOLEM_THROW, 3, 1);
                                                event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.EXPLODE, 5, 1);
                                                VelocityUtil.flingNearby(event.getPlayer().getLocation(), 10, 1);
                                                ParticleEffect.FIREWORKS_SPARK.display(0, 0, 0, 0, 12, event.getPlayer().getLocation(), 15);
                                                ParticleEffect.FLAME.display(0, 0, 0, 0, 12, event.getPlayer().getLocation(), 15);
                                                ParticleEffect.CLOUD.display(0, 0, 0, 0, 6, event.getPlayer().getLocation(), 15);
                                                ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, 0, 12, event.getPlayer().getLocation(), 15);
                                                ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 12, event.getPlayer().getLocation(), 15);
                                                if(!end && event.getPlayer().isOnline()) {
                                                    new BukkitRunnable() {
                                                        int countdown = 30;
                                                        public void run() {
                                                            if(event.getPlayer().isOnline() && !end) {
                                                                if(countdown > 0) {
                                                                    ItemStack newItem = event.getPlayer().getInventory().getItem(0);
                                                                    newItem.setAmount(countdown);
                                                                    event.getPlayer().getInventory().setItem(0, newItem);
                                                                    countdown--;
                                                                } else {
                                                                    ItemStack newItem = event.getPlayer().getInventory().getItem(0);
                                                                    newItem.setAmount(1);
                                                                    event.getPlayer().getInventory().setItem(0, newItem);
                                                                }
                                                            } else {
                                                                this.cancel();
                                                            }
                                                        }
                                                    }.runTaskTimer(ArenaPVP.getInstance(), 0, 20L);
                                                }
                                            } else {
                                                current-=3;
                                                BarUtil.playProgressBar(event.getPlayer(), '[', '|', ']', current);
                                                event.getPlayer().setVelocity(new Vector(0, 0.3, 0).add(event.getPlayer().getLocation().getDirection()));
                                            }
                                        }
                                    }
                                }.runTaskTimer(ArenaPVP.getInstance(), 0, 3L);
                            }
                        }
                    }
                }
            }
        }
    }

    private void init() {
        this.player1.teleport(arena.getSpawnpointA());
        this.player2.teleport(arena.getSpawnpointB());
        this.player1.getInventory().clear();
        this.player2.getInventory().clear();
        this.player1.setHealth(20);
        this.player2.setHealth(20);
        //TODO: Set inventories
        this.player1.getInventory().addItem(this.kit.getHotbar());
        this.player2.getInventory().addItem(this.kit.getHotbar());
        this.player1.getInventory().setHelmet(this.kit.getArmour()[0]);
        this.player1.getInventory().setChestplate(this.kit.getArmour()[1]);
        this.player1.getInventory().setLeggings(this.kit.getArmour()[2]);
        this.player1.getInventory().setBoots(this.kit.getArmour()[3]);
        this.player2.getInventory().setHelmet(this.kit.getArmour()[0]);
        this.player2.getInventory().setChestplate(this.kit.getArmour()[1]);
        this.player2.getInventory().setLeggings(this.kit.getArmour()[2]);
        this.player2.getInventory().setBoots(this.kit.getArmour()[3]);
        for(Map.Entry<Integer, ItemStack> entry : this.kit.getInventory().entrySet()) {
            this.player1.getInventory().setItem(entry.getKey(), entry.getValue());
            this.player2.getInventory().setItem(entry.getKey(), entry.getValue());
        }
        this.setGameBoard1();
        this.setGameBoard2();
    }

    private void setGameBoard1() {
        SimpleScoreboard board = ScoreboardManager.getInstance().playerBoards.get(this.player1);
        ScoreboardManager.getInstance().setGameLines(board, this.player2.getName(), this.kit.getName(), this.arena.getMapInfo().getName());
        board.update();
    }

    private void setGameBoard2() {
        SimpleScoreboard board = ScoreboardManager.getInstance().playerBoards.get(this.player2);
        ScoreboardManager.getInstance().setGameLines(board, this.player1.getName(), this.kit.getName(), this.arena.getMapInfo().getName());
        board.update();
    }

    private void resetBoards() {
        if(this.player1.isOnline()) {
            SimpleScoreboard board1 = ScoreboardManager.getInstance().playerBoards.get(this.player1);
            AccountDetails accountDetails1 = Core.getInstance().getAccountManager().parseDetails(player1.getUniqueId());
            ScoreboardManager.getInstance().setDefaultLines(board1, accountDetails1);
            board1.update();
        }
        if(this.player2.isOnline()) {
            SimpleScoreboard board2 = ScoreboardManager.getInstance().playerBoards.get(this.player2);
            AccountDetails accountDetails2 = Core.getInstance().getAccountManager().parseDetails(player2.getUniqueId());
            ScoreboardManager.getInstance().setDefaultLines(board2, accountDetails2);
            board2.update();
        }
    }

    private void end() {
        end = true;
        HandlerList.unregisterAll(instance);
        if(player1.isOnline()) {
            resetInv(player1);
            lobbyInv(player1);
            player1.teleport(ArenaPVP.getInstance().LOBBY_SPAWN);
            player1.showPlayer(player2);
            //player1.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
            player1.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).unregister();
            JukeboxManager.getInstance().update(player1.getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/HubChristmas.mp3");
        }
        if(player2.isOnline()) {
            resetInv(player2);
            lobbyInv(player2);
            player2.teleport(ArenaPVP.getInstance().LOBBY_SPAWN);
            player2.showPlayer(player1);
            //player2.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
            player2.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).unregister();
            JukeboxManager.getInstance().update(player2.getName(), SoundType.MUSIC, "https://downloads.frostedmc.com/music/HubChristmas.mp3");
        }
        this.resetBoards();
        ArenaManager.getInstance().resetGame(this);
    }

    public Arena getArena() {
        return this.arena;
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public boolean ingame(Player player) {
        return player1 == player || player2 == player;
    }

    public Kit getKit() { return this.kit; }

    public static void resetInv(Player player) {
        for(PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setFireTicks(0);
        player.setNoDamageTicks(20);
        player.setMaximumNoDamageTicks(20);
    }

    public static void queueInv(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&c&lLEAVE QUEUE"));
    }

    public static void lobbyInv(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, ItemCreator.getInstance().createItem(Material.DIAMOND_SWORD, 1, 0, "&6&lRANKED QUEUE"));
        player.getInventory().setItem(1, ItemCreator.getInstance().createItem(Material.STONE_SWORD, 1, 0, "&e&lUNRANKED QUEUE"));
        player.getInventory().setItem(4, ItemCreator.getInstance().createSkull(1, player.getName(), "&5&lPROFILE"));
        player.getInventory().setItem(7, ItemCreator.getInstance().createItem(Material.SLIME_BALL, 1, 0, "&e&lCOSMETICS"));
        player.getInventory().setItem(8, ItemCreator.getInstance().createItem(Material.COMPASS, 1, 0, "&6&lLOBBY SELECTOR"));
    }
}