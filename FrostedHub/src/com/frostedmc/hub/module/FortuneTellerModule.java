package com.frostedmc.hub.module;

import com.frostedmc.core.events.PlayerChatEvent;
import com.frostedmc.core.gui.CustomSkull;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.NBTUtils;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.hub.Hub;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Witch;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/16/2017.
 */
public class FortuneTellerModule extends Module implements Listener {

    @Override
    public String name() {
        return "Fortune Teller";
    }

    private Location location;
    private Witch witch;
    private ArmorStand base;
    private ArmorStand nameTag;
    private boolean using = false;
    private Player player;
    private Player player_2;
    private boolean question = false;
    private boolean question_finished = false;

    private Location ball_spawn;
    private String[] fortunes = new String[]{
            "&aIt is certain",
            "&aIt is decidedly so",
            "&aWithout a doubt",
            "&aYes, definitely",
            "&aYou may rely on it",
            "&aAs I see it, yes",
            "&aMost likely",
            "&aOutlook good",
            "&aYes",
            "&aSigns point to yes",
            "&bReply hazy try again",
            "&bAsk again later",
            "&bBetter not tell you now",
            "&bCannot predict now",
            "&bConcentrate and ask again",
            "&cDon't count on it",
            "&cMy reply is no",
            "&cMy sources say no",
            "&cOutlook not so good",
            "&cVery doubtful"
    };
    private Map<UUID, Long> lastMsg = new HashMap<UUID, Long>();

    @Override
    public void onEnable() {
        this.location = new Location(Bukkit.getWorld("world"), -26.5, 77.0, 62.5, -11.9f, 3.3f);
        this.witch = location.getWorld().spawn(this.location, Witch.class);
        witch.setRemoveWhenFarAway(false);
        witch.setCanPickupItems(false);
        witch.setMetadata("fortune-teller", new FixedMetadataValue(Hub.getInstance(), true));
        this.base = location.getWorld().spawn(location.clone().subtract(0, 1.5, 0), ArmorStand.class);
        base.setVisible(false);
        base.setBasePlate(false);
        base.setSmall(true);
        base.setGravity(false);
        base.setPassenger(this.witch);
        nameTag = Bukkit.getWorld("world").spawn(location.clone().add(0, 1.2, 0), ArmorStand.class);
        nameTag.setSmall(true);
        nameTag.setBasePlate(false);
        nameTag.setVisible(false);
        nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&5&lFortune Teller"));
        nameTag.setCustomNameVisible(true);
        nameTag.setGravity(false);
        NBTUtils.defaultNPCTags(nameTag);
        NBTUtils.defaultNPCTags(base);
        NBTUtils.defaultNPCTags(witch);

        this.ball_spawn = new Location(Bukkit.getWorld("world"), -26.5, 76, 63.5);
        Hub.getInstance().getServer().getPluginManager().registerEvents(this, Hub.getInstance());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getFrom().distance(location) <= 10D) return;
        if(event.getTo().distance(location) <= 10D) {
            boolean msg = true;
            if(lastMsg.containsKey(event.getPlayer().getUniqueId())) {
                if((System.currentTimeMillis()-lastMsg.get(event.getPlayer().getUniqueId()))
                        <= 10000) {
                    msg = false;
                }
            }
            if(msg) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_PIANO, 10, 1);
                ChatUtils.sendBlockMessage("Fortune Teller", new String[]{
                        "&bPssst, want your fortune told?",
                        "&bThe spirits are waiting.",
                        "&a&lRight-Click to enter"
                }, event.getPlayer());
                lastMsg.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        if(event.getEntity().hasMetadata("fortune-teller")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if(event.getEntity().hasMetadata("fortune-teller")) {
            event.setCancelled(true);
        }
    }

    private void animation() {
        ArmorStand ball = this.ball_spawn.getWorld().spawn(this.ball_spawn, ArmorStand.class);
        ball.setVisible(false);
        ball.setBasePlate(false);
        ball.setSmall(true);
        ball.setGravity(false);
        ball.setHelmet(CustomSkull.getInstance().create("Skull",
                "eyJ0aW1lc3RhbXAiOjE0ODcyOTM1MDg4MTIsInByb2ZpbGVJZCI6ImUzYjQ0NWM4NDdmNTQ4ZmI4YzhmYTNmMWY3ZWZiYThlIiwicHJvZmlsZU5hbWUiOiJNaW5pRGlnZ2VyVGVzdCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ0MGNhMWM2YzQ5MDEyNzhmNWJiOTQyMjdjZDQzNGY0ZmJlOTdmOTg4NjM5OWIxNWU4ZjE5MTZlNGEifX19"));
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(ball.getLocation().getY() >= (ball_spawn.getY()+2)) {
                    this.cancel();
                    ball.getWorld().playSound(ball.getLocation(), Sound.FIZZ, 10, 1);
                    animation2(ball);
                } else {
                    ball.teleport(ball.getLocation().add(0, 0.1, 0));
                    ball.setHeadPose(new EulerAngle(0, ticks, 0));
                    ball.getWorld().playSound(ball.getLocation(), Sound.FUSE, 3, 1);
                }
                ticks++;
            }
        }.runTaskTimer(Hub.getInstance(), 0, 1L);
    }

    private void animation2(ArmorStand ball) {
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(ticks >= 40) {
                    this.cancel();
                    ball.getWorld().playSound(ball.getLocation(), Sound.ORB_PICKUP, 10, 1);
                    AntiMoveModule.disallow.remove(player_2);
                    player_2 = null;
                    ArmorStand fortune = Bukkit.getWorld("world").spawn(ball.getLocation()
                            .clone().add(0, 0.3, 0), ArmorStand.class);
                    ParticleEffect.CRIT_MAGIC.display(0, 0, 0, 1, 10, fortune.getLocation(), 15);
                    ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 1, 8, fortune.getLocation(), 15);
                    fortune.setSmall(true);
                    fortune.setBasePlate(false);
                    fortune.setVisible(false);
                    fortune.setCustomName(ChatColor.translateAlternateColorCodes('&', fortunes
                            [new Random().nextInt(fortunes.length)]));
                    fortune.setCustomNameVisible(true);
                    fortune.setGravity(false);
                    new BukkitRunnable() {
                        public void run() {
                            ball.remove();
                            fortune.remove();
                            using = false;
                        }
                    }.runTaskLater(Hub.getInstance(), 60L);
                } else {
                    ball.setHeadPose(new EulerAngle(0, (ticks/2), 0));
                    if(ticks % 2 == 0) ball.getWorld().playSound(ball.getLocation(), Sound.FUSE, 3, 1);
                }
                ticks++;
            }
        }.runTaskTimer(Hub.getInstance(), 0, 1L);
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if(!using) return;
        if(player_2 == null) return;
        if(player_2.getUniqueId() != event.getPlayer().getUniqueId()) return;
        if(!question) return;
        event.setCancelled(true);
        question_finished = true;
        question = false;
        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_YES, 10, 1);
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&bFortune Teller> &7Here we go."));
        new BukkitRunnable() {
            public void run() {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_HAGGLE, 10, 1);
                String formatedQuestion = event.getMessage();
                if(!formatedQuestion.contains("?")) {
                    formatedQuestion+="?";
                }
                String msg = "&b" + event.getPlayer().getName() + "> &7Spirits, " + formatedQuestion;
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        msg));
                ArmorStand question = Bukkit.getWorld("world").spawn(event.getPlayer().getLocation()
                        .clone().add(0, 1.0, 0), ArmorStand.class);
                question.setSmall(true);
                question.setBasePlate(false);
                question.setVisible(false);
                question.setCustomName(ChatColor.translateAlternateColorCodes('&', msg));
                question.setCustomNameVisible(true);
                question.setGravity(false);
                new BukkitRunnable() {
                    public void run() {
                        question.remove();
                    }
                }.runTaskLater(Hub.getInstance(), 120L);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.AMBIENCE_THUNDER, 10, 1);
                new BukkitRunnable() {
                    public void run() {
                        animation();
                    }
                }.runTaskLater(Hub.getInstance(), 40L);
            }
        }.runTaskLater(Hub.getInstance(), 40L);
    }

    @EventHandler
    public void onPreProcessCommand(PlayerCommandPreprocessEvent event) {
        if(!using) return;
        if(player == null) return;
        if(player.getUniqueId() != event.getPlayer().getUniqueId()) return;
        if(event.getMessage().equalsIgnoreCase("/fortuneaccept")) {
            event.setCancelled(true);
            player = null;
            player_2 = event.getPlayer();
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_YES, 10, 1);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bFortune Teller> &7Very well."));
            new BukkitRunnable() {
                public void run() {
                    question = true;
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_HAGGLE, 10, 1);
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bFortune Teller> &7Ask me your question, " + event.getPlayer().getName() + "."));
                    question_finished = false;
                    new BukkitRunnable() {
                        int seconds = 20;
                        int ticks = 0;
                        public void run() {
                            ticks++;
                            if(ticks % 20 != 0) return;
                            if(seconds <= 1 || !event.getPlayer().isOnline()
                                    || player_2 == null || player_2.getUniqueId() != event.getPlayer().getUniqueId()) {
                                this.cancel();
                                event.getPlayer().setLevel(0);
                                if(question_finished) {
                                    return;
                                }
                                AntiMoveModule.disallow.remove(event.getPlayer());
                                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIZZ, 10, 1);
                                using = false;
                            } else {
                                seconds--;
                                event.getPlayer().setLevel(seconds);
                            }
                        }
                    }.runTaskTimer(Hub.getInstance(), 0, 1L);
                }
            }.runTaskLater(Hub.getInstance(), 40L);
        }
        if(event.getMessage().equalsIgnoreCase("/fortunedeny")) {
            event.setCancelled(true);
            player = null;
            player_2 = null;
            using = false;
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.VILLAGER_HAGGLE, 10, 1);
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bFortune Teller> &7You can always come back."));
        }
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().hasMetadata("fortune-teller")) {
            event.setCancelled(true);
            if(using) {
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLAZE_HIT, 10, 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLAZE_BREATH, 10, 1);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&bFortune Teller> &7Wait your turn!"));
            } else {
                using = true;
                question = false;
                this.player = event.getPlayer();
                event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), -26.5, 77.0, 65.5, 171, 5));
                AntiMoveModule.disallow.add(event.getPlayer());
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIREWORK_LAUNCH, 10, 1);
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.PORTAL_TRAVEL, 10, 1);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&bFortune Teller> &7Do you wish to call upon the spirits to tell your future?"));
                new BukkitRunnable() {
                    public void run() {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.CLICK, 10, 1);
                        new FancyMessage("Fortune Teller> ")
                            .color(ChatColor.AQUA)
                            .then("[YES] ")
                            .color(ChatColor.GREEN)
                            .style(ChatColor.BOLD)
                            .tooltip("Left-Click to accept")
                            .command("/fortuneaccept")
                            .then("[NO]")
                            .color(ChatColor.RED)
                            .style(ChatColor.BOLD)
                            .tooltip("Left-Click to deny")
                            .command("/fortunedeny")
                            .send(event.getPlayer());
                    }
                }.runTaskLater(Hub.getInstance(), 40L);
                new BukkitRunnable() {
                    int seconds = 10;
                    int ticks = 0;
                    public void run() {
                        ticks++;
                        if(ticks % 20 != 0) return;
                        if(seconds <= 1 || !event.getPlayer().isOnline()
                                || player == null || player.getUniqueId() != event.getPlayer().getUniqueId()) {
                            this.cancel();
                            event.getPlayer().setLevel(0);
                            if(player_2 != null
                                    && player_2.getUniqueId() == event.getPlayer().getUniqueId()) {
                                return;
                            }
                            AntiMoveModule.disallow.remove(event.getPlayer());
                            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.FIZZ, 10, 1);
                            using = false;
                        } else {
                            seconds--;
                            event.getPlayer().setLevel(seconds);
                        }
                    }
                }.runTaskTimer(Hub.getInstance(), 0, 1L);
            }
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.witch.remove();
        this.base.remove();
        this.nameTag.remove();
    }
}