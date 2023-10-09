package com.frostedmc.frostedgames.game;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.api.account.achievement.AchievementEffect;
import com.frostedmc.core.api.account.statistics.StatisticProfile;
import com.frostedmc.core.api.jukebox.JukeboxManager;
import com.frostedmc.core.api.jukebox.SoundType;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.frostedgames.FireworkUtil;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.manager.ScoreboardManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Created by Redraskal_2 on 1/22/2017.
 */
public class CustomDamage {

    public static void handleDeath(Player entity, Entity damager, String cause) {
        for(Player player : Bukkit.getOnlinePlayers()) {
            JukeboxManager.getInstance().update(player.getName(), SoundType.SOUND_EFFECT, "https://downloads.frostedmc.com/music/effects/cannon.mp3");
        }
        for(ItemStack item : entity.getInventory().getContents()) {
            if(item != null && item.getType() != Material.AIR) {
                entity.getWorld().dropItemNaturally(entity.getLocation(), item);
            }
        }
        for(ItemStack item : entity.getInventory().getArmorContents()) {
            if(item != null && item.getType() != Material.AIR) {
                entity.getWorld().dropItemNaturally(entity.getLocation(), item);
            }
        }
        Core.getInstance().getStatisticsManager().add(entity.getUniqueId(), "frostedgames_deaths", 1);
        if(cause.equalsIgnoreCase("Fall")) {
            Core.getInstance().getStatisticsManager().add(entity.getUniqueId(), "frostedgames_fall_deaths", 1);
        }
        StatisticProfile statisticProfile =
                Core.getInstance().getStatisticsManager().fetchProfile(entity.getUniqueId());
        if(statisticProfile.getStatistic("frostedgames_fall_deaths") >= 5) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Ground Lover I", "Killed by Fall Damage 5 times"),
                    100, 50, FrostedGames.getInstance()
            );
        }
        if(statisticProfile.getStatistic("frostedgames_fall_deaths") >= 30) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Ground Lover II", "Killed by Fall Damage 30 times"),
                    100, 50, FrostedGames.getInstance());
        }
        if(statisticProfile.getStatistic("frostedgames_fall_deaths") >= 60) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Ground Lover III", "Killed by Fall Damage 60 times"),
                    100, 50, FrostedGames.getInstance());
        }
        if(statisticProfile.getStatistic("frostedgames_fall_deaths") >= 100) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Ground Lover IV", "Killed by Fall Damage 100 times"),
                    100, 50, FrostedGames.getInstance());
        }

        if(statisticProfile.getStatistic("frostedgames_deaths") >= 5) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Not A Great Tribute I", "Died 5 times"),
                    100, 50, FrostedGames.getInstance()
            );
        }
        if(statisticProfile.getStatistic("frostedgames_deaths") >= 30) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Not A Great Tribute II", "Died 30 times"),
                    100, 50, FrostedGames.getInstance());
        }
        if(statisticProfile.getStatistic("frostedgames_deaths") >= 60) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Not A Great Tribute III", "Died 60 times"),
                    100, 50, FrostedGames.getInstance());
        }
        if(statisticProfile.getStatistic("frostedgames_deaths") >= 100) {
            AchievementEffect.giveAchievement(entity.getUniqueId(),
                    new com.frostedmc.core.api.account.achievement.Achievement(
                            "Not A Great Tribute IV", "Died 100 times"),
                    100, 50, FrostedGames.getInstance());
        }

        if(damager != null) {
            boolean defaultM = true;
            if(cause.equalsIgnoreCase("Projectile")) {
                if(damager instanceof Projectile) {
                    Projectile projectile = (Projectile) damager;
                    if(projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e"
                                + ((Player) projectile.getShooter()).getName() + " &7with &e"
                                + formatElement(projectile.getType().toString().toLowerCase()) + "&7."));
                        defaultM = false;
                    }
                }
            }
            if(damager instanceof Player) {
                String item = "Fist";
                if(((Player) damager).getItemInHand() != null) {
                    if(((Player) damager).getItemInHand().getType() != Material.AIR) {
                        item = ((Player) damager).getItemInHand().getType().toString().toLowerCase();
                        item = formatElement(item);
                    }
                }
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e"
                        + ((Player) damager).getName() + " &7with &e" + item + "&7."));
                defaultM = false;
            }
            if(defaultM) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e"
                        + damager.getName() + " &7with &e" + cause + "&7."));
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &e" + entity.getName() + " &7was killed by &e" + cause + "&7."));
        }
        if(damager != null) {
            if(damager instanceof Player) {
                Core.getInstance().getStatisticsManager().add(damager.getUniqueId(), "frostedgames_kills", 1);
                if(InternalGameSettings.kills.containsKey(((Player) damager))) {
                    InternalGameSettings.kills.put((Player) damager, (InternalGameSettings.kills.get(((Player) damager)) + 1));
                } else {
                    InternalGameSettings.kills.put((Player) damager, 1);
                }
            }
        }
        entity.getWorld().strikeLightningEffect(entity.getLocation());
        entity.getWorld().playSound(entity.getLocation(), Sound.FIREWORK_LARGE_BLAST, 10, 5);
        SpectatorMode.getInstance().add(entity);
        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 3), true);
        if(InternalGameSettings.deathmatch) {
            entity.teleport(new Location(Bukkit.getWorld("deathmatch"), 25.5, 76.0, 32.5, 142.8f, 18.3f));
        } else {
            entity.teleport(InternalGameSettings.map.getSpectator());
        }
        InternalGameSettings.districtMap.remove(entity);
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                ScoreboardManager.getInstance().playerBoards.get(player)
                        .add("&eTributes » &7" + InternalGameSettings.districtMap.size(), 5);
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
        }
        Player winner = null;
        if(InternalGameSettings.districtMap.size() == 0) {
            winner = entity;
        } else {
            if(InternalGameSettings.districtMap.size() == 1) {
                winner = InternalGameSettings.districtMap.entrySet().iterator().next().getKey();
            }
        }
        if(winner != null) {
            SpectatorMode.getInstance().add(winner);
            Core.getInstance().getStatisticsManager().add(winner.getUniqueId(), "frostedgames_wins", 1);
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 1);
                if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                    ScoreboardManager.getInstance()
                            .setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                    ScoreboardManager.getInstance().playerBoards.get(player)
                            .add("&aGame has ended", 5);
                    ScoreboardManager.getInstance().playerBoards.get(player).update();
                }
                for(Player other : Bukkit.getOnlinePlayers()) {
                    other.showPlayer(player);
                }
                int kills = 0;
                if(InternalGameSettings.kills.containsKey(player)) {
                    kills = InternalGameSettings.kills.get(player);
                }
                int icicles = (kills*3)+10;
                if(player.getName().equalsIgnoreCase(winner.getName())
                        || player.getName().equalsIgnoreCase(InternalGameSettings.gameMaker.getName())) {
                    icicles+=10;
                }
                Rank rank = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getRank();
                if(Rank.compare(rank, Rank.VIP)) {
                    if(rank == Rank.VIP) {
                        icicles*=2;
                    } else {
                        icicles*=3;
                    }
                }
                if(!InternalGameSettings.rewards.contains(player)) {
                    icicles = 0;
                }
                Core.getInstance().getAccountManager().update(player.getUniqueId(),
                        new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                (Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getIcicles()+icicles)));
                if(rank == Rank.PLAYER) {
                    ChatUtils.sendBlockMessage("Game Summary", new String[]{
                            "&7&lLast Person Standing: &3" + winner.getName(),
                            "&7",
                            "&7You have killed &3" + kills + " &7player(s)!",
                            "&7You have earned &3" + icicles + " &7icicle(s)!"
                    }, player);
                } else {
                    if(rank == Rank.VIP) {
                        ChatUtils.sendBlockMessage("Game Summary", new String[]{
                                "&7&lLast Person Standing: &3" + winner.getName(),
                                "&7",
                                "&7You have killed &3" + kills + " &7player(s)!",
                                "&7You have earned &3" + icicles + " &7icicle(s)!",
                                "&7",
                                "&3&lx2 &7icicles due to being " + rank.getPrefix(false) + "&7."
                        }, player);
                    } else {
                        ChatUtils.sendBlockMessage("Game Summary", new String[]{
                                "&7&lLast Person Standing: &3" + winner.getName(),
                                "&7",
                                "&7You have killed &3" + kills + " &7player(s)!",
                                "&7You have earned &3" + icicles + " &7icicle(s)!",
                                "&7",
                                "&3&lx3 &7icicles due to being " + rank.getPrefix(false) + "&7."
                        }, player);
                    }
                }
            }
            for(int i=0; i<24; i++) {
                Location randomLocation = winner.getLocation().clone();
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
            InternalGameSettings.status = Status.ENDED;
            InternalGameSettings.gameMaker.getInventory().clear();
            new BukkitRunnable() {
                public void run() {
                    for(Player o : Bukkit.getOnlinePlayers()) {
                        MessageChannel.getInstance().Switch(o, o.getName(), "FGL-");
                    }
                }
            }.runTaskLater(FrostedGames.getInstance(), (20*10));
            new BukkitRunnable() {
                public void run() {
                    FrostedGames.getInstance().jedis.publish("custom-rca", Bukkit.getServerName());
                }
            }.runTaskLater(FrostedGames.getInstance(), (20*10));
            FrostedGames.getInstance().publishServerUpdate();
        }
    }

    public static String formatElement(String element) {
        String temp = element.toString().toLowerCase().replace("_", " ");
        temp = WordUtils.capitalizeFully(temp);
        return temp;
    }

    public static String formatCause(EntityDamageEvent.DamageCause damageCause) {
        String temp = damageCause.toString().toLowerCase().replace("_", " ");
        temp = WordUtils.capitalizeFully(temp);
        return temp;
    }
}