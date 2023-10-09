package com.frostedmc.core.glacier;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.Rank;
import com.frostedmc.core.commands.CommandManager;
import com.frostedmc.core.commands.defaults.GlacierLogCommand;
import com.frostedmc.core.glacier.checks.Derp;
import com.frostedmc.core.glacier.checks.Step;
import com.frostedmc.core.messages.Prefix;
import com.frostedmc.core.module.Module;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.core.utils.Utils;
import com.frostedmc.core.utils.VectorUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Redraskal_2 on 8/30/2016.
 */
public class GlacierModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    private List<Check> checks;
    private List<MoveCheck> moveChecks;
    private List<DamageCheck> damageChecks;
    private List<Player> disableMovement;
    private KillAuraThread killAuraThread;
    private Map<Player, Map<Check, Integer>> vl = new HashMap<Player, Map<Check, Integer>>();

    private static GlacierModule instance = null;

    public static GlacierModule getInstance() {
        return instance;
    }

    public GlacierModule(JavaPlugin javaPlugin) {
        instance = this;
        this.javaPlugin = javaPlugin;
        //this.killAuraThread = new KillAuraThread(javaPlugin);
        CommandManager.getInstance().registerCommand(new GlacierLogCommand());
    }

    public JavaPlugin getPlugin() {
        return this.javaPlugin;
    }

    @Override
    public String name() {
        return "Glacier";
    }

    @Override
    public void onEnable() {
        this.checks = new ArrayList<Check>();
        this.moveChecks = new ArrayList<MoveCheck>();
        this.damageChecks = new ArrayList<DamageCheck>();
        this.disableMovement = new ArrayList<Player>();
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);

        this.checks.add(new Step());
        this.checks.add(new Derp());

        //TODO Implement Checks.
        //this.killAuraThread.register();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        if(this.disableMovement.contains(playerMoveEvent.getPlayer())) {
            playerMoveEvent.setTo(playerMoveEvent.getFrom());
            return;
        }
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        //this.killAuraThread.unregister();
    }

    public Check getCheck(String name) {
        for(Check enabledChecks : checks) {
            if(enabledChecks.getClass().getName().endsWith(name)) {
                return enabledChecks;
            }
        }
        return null;
    }

    public int getVL(Player player, Check violation) {
        if(!this.vl.containsKey(player)) {
            return 0;
        }
        if(this.vl.get(player).containsKey(violation)) {
            return this.vl.get(player).get(violation);
        } else {
            return 0;
        }
    }

    public void handleViolation(Player player, Check violation) {
        if(!this.vl.containsKey(player)) {
            Map<Check, Integer> newMap = new HashMap<Check, Integer>();
            newMap.put(violation, 1);
            this.vl.put(player, newMap);
        } else {
            Map<Check, Integer> newMap = this.vl.get(player);
            if(newMap.containsKey(violation)) {
                newMap.put(violation, (newMap.get(violation) + 1));
            } else {
                newMap.put(violation, 1);
            }
            this.vl.put(player, newMap);
        }

        if(!this.disableMovement.contains(player)) {
            if(((CraftPlayer) player).getHandle().ping >= 2000) {
                if(this.getVL(player, violation) == 1) {
                    this.pushMessage("*Possible unfair advantages for " + player.getName() + "* _[Type: "
                            + violation.getName().toUpperCase().replace(" ", "_")
                            + ", VL: "
                            + this.getVL(player, violation)
                            + ", Flags: {HIGH_PING}]_ `/server "
                            + Bukkit.getServerName() + "`");
                    this.javaPlugin.getLogger().info("[Glacier] " + player.getName() + " could possibly be hacking. [" + violation + "]");
                }
                return;
            }

            if(this.getVL(player, violation) == violation.countToNotify) {
                this.pushMessage("*Possible unfair advantages for " + player.getName() + "* _[Type: "
                        + violation.getName().toUpperCase().replace(" ", "_")
                        + ", VL: "
                        + this.getVL(player, violation)
                        + "]_ `/server "
                        + Bukkit.getServerName() + "`");
            }

            if(this.getVL(player, violation) == Math.floor((2/violation.maxViolations))) {
                this.pushMessage("*Possible unfair advantages for " + player.getName() + "* _[Type: "
                        + violation.getName().toUpperCase().replace(" ", "_")
                        + ", VL: "
                        + this.getVL(player, violation)
                        + ", Flags: {HIGH_DETECTION}]_ `/server "
                        + Bukkit.getServerName() + "`");
            }

            if(this.getVL(player, violation) < violation.maxViolations
                    || !violation.bannable
                    || !violation.isEnabled()) {
                return;
            }

            String violations = "";

            for(Map.Entry<Check, Integer> vio : this.vl.get(player).entrySet()) {
                if(vio.getValue() >= (2/vio.getKey().maxViolations)) {
                    violations = Utils.add(violations, vio.getKey().getName().toUpperCase().replace(" ", "_"));
                }
            }

            this.pushMessage("*" + player.getName() + " was detected for " + violations + "*");

            this.broadcast();
            this.disableMovement.add(player);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            this.animation(player, violations);
            player.getInventory().clear();

            this.javaPlugin.getLogger().info("[Glacier] " + player.getName() + " was detected for [" + violations + "]");
        }
    }

    private void pushMessage(String message) {
        Core.getInstance().getLogger().info("[Glacier] [Result] Sending Slack request...");

        for(Player player : Bukkit.getOnlinePlayers()) {
            Rank rank = Core.getInstance().getAccountManager().parseDetails(player.getUniqueId()).getRank();
            if(Rank.compare(rank, Rank.HELPER)) {
                player.sendMessage(Prefix.GLACIER.build() + message);
            }
        }

        message = message.replace(" ", "%20");

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL("https://slack.com/api/chat.postMessage?token=xoxb-76481008545-XgrlRgdmOJEkMcvuXxGXVWRv&channel=C28E588RF&as_user=true&text="
                    + message);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
            Core.getInstance().getLogger().info("[Glacier] [Result] " + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcast() {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-----------------------------------------"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e&lA player has been removed from the game"));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&e&ldue to an unfair advantage."));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&7&m-----------------------------------------"));
    }

    private void animation(Player player, String violations) {
        new BukkitRunnable() {
            int radius = 1;
            int particlesOrbital = 5;
            int orbitals = 1;
            double rotation = 90.0D;
            double angularVelocity = 0.039269908169872414D;
            int step = 0;
            double yStop = (player.getLocation().getY()+5);
            boolean spawned = false;
            Snowman s1;
            Snowman s2;
            Snowman s3;
            double offset = 0;
            double rangeOffset = 0;
            public void run() {
                if(player.isOnline()) {
                    player.setFlying(true);
                    Location localLocation1 = player.getLocation().add(0.0D, 1.15D, 0.0D);
                    double d1 = 0.0D;
                    double d2 = 0.0D;
                    double d3 = 0.13D;

                    if(yStop > player.getLocation().getY()) {
                        player.teleport(player.getLocation().add(0, 0.1, 0));
                        player.getWorld().playSound(player.getLocation(), Sound.FIZZ, 10f, 10f);
                    } else {
                        if(!spawned) {
                            s1 = player.getWorld().spawn(player.getLocation().add(0, 0, 0), Snowman.class);
                            s2 = player.getWorld().spawn(player.getLocation().add(0, 0, 0), Snowman.class);
                            s3 = player.getWorld().spawn(player.getLocation().add(0, 0, 0), Snowman.class);
                            spawned = true;
                        }

                        Location l1 = VectorUtils.getLocationAroundCircle(player.getLocation().add(0, offset, 0), (3-rangeOffset), (float) (1.0f*step));
                        Location l2 = VectorUtils.getLocationAroundCircle(player.getLocation().add(0, offset, 0), (3-rangeOffset), (float) (2.0f*step));
                        Location l3 = VectorUtils.getLocationAroundCircle(player.getLocation().add(0, offset, 0), (3-rangeOffset), (float) (3.0f*step));

                        ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, s1.getLocation(), 15);
                        ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, s2.getLocation(), 15);
                        ParticleEffect.SMOKE_LARGE.display(0, 0, 0, 0, 1, s3.getLocation(), 15);

                        s1.teleport(l1);
                        s2.teleport(l2);
                        s3.teleport(l3);

                        s1.setVelocity(new Vector(0, 0.1, 0));
                        s2.setVelocity(new Vector(0, 0.1, 0));
                        s3.setVelocity(new Vector(0, 0.1, 0));

                        if(step < 1000) {
                            offset +=.1;
                            rangeOffset +=.1;
                            player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 10f, 20f);
                        } else {
                            offset -=.5;
                            rangeOffset -=.5;
                            player.getWorld().playSound(player.getLocation(), Sound.SHOOT_ARROW, 10f, 30f);
                        }

                        if(step == 1100) {
                            this.cancel();
                            player.getWorld().strikeLightning(player.getLocation());
                            ParticleEffect.EXPLOSION_HUGE.display(0, 0, 0, 0, 1, player.getLocation(), 15);
                            ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0, 1, player.getLocation(), 15);
                            ParticleEffect.EXPLOSION_NORMAL.display(0, 0, 0, 0, 1, player.getLocation(), 15);
                            player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 10f, 10f);
                            s1.remove();
                            s2.remove();
                            s3.remove();
                            disableMovement.remove(player);
                            player.setAllowFlight(false);
                            player.setFlying(false);
                            player.kickPlayer(ChatColor.translateAlternateColorCodes('&',
                                    "&c&lGlacier Cheat Detection\n&7You were banned by &a&lGlacier&7." +
                                            "\n&6&lReason:&7 Unfair Advantage(s): &e[" + violations + "&e]" +
                                            "\n\n&fPlease appeal on our forums at: &ahttps://frostedmc.com"));
                        }
                    }

                    for (int j = 0; j < this.particlesOrbital; j++) {
                        double d4 = this.step * this.angularVelocity;
                        for (int k = 0; k < this.orbitals; k++) {
                            double d5 = 4.141592653589793D / this.orbitals * k;
                            Vector localVector = new Vector(Math.cos(d4), Math.sin(d4), 0.0D).multiply(this.radius);
                            VectorUtils.rotateAroundAxisX(localVector, d5);
                            VectorUtils.rotateAroundAxisY(localVector, this.rotation);
                            localLocation1.add(localVector);
                            int i = 0;
                            if (i != 0) {
                                ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 1, localLocation1, 15);
                            } else {
                                d1 += d3;
                                d2 += d3;
                                d1 = d1+1;
                                d2 = d2+1;
                                final Location localLocation2 = localLocation1.clone();
                                new BukkitRunnable() {
                                    public void run() {
                                        ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 1, localLocation2, 15);
                                    }
                                }.runTaskLater(javaPlugin, (int) d1);
                            }

                            localLocation1.subtract(localVector);
                        }
                        this.step += 1;

                        if(step > 100) {
                            ParticleEffect.DRIP_WATER.display(0, 0, 0, 0, 1, localLocation1, 15);
                        }

                        if(step == 100
                                || step == 260
                                || step == 300) {
                            orbitals++;
                        }
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this.javaPlugin, 0, 1);
    }
}