package com.frostedmc.gamemanager.game.oitc;

import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.core.utils.Title;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Utils;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.Map;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.commands.GodModeCommand;
import com.frostedmc.gamemanager.event.CustomDeathEvent;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.DamageManager;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class OITC extends Game {

    @Override
    public String gameName() {
        return "OITC";
    }

    private MapInfo mapInfo = new MapInfo();
    private List<Team> teams = new ArrayList<Team>();
    public java.util.Map<Player, Integer> score = new HashMap<Player, Integer>();
    public java.util.Map<Player, Integer> killstreaks = new HashMap<Player, Integer>();

    public OITC() {
        this.gameFlags.minPlayers = 2;
        this.gameFlags.maxPlayers = 12;
        this.gameFlags.enablePVE = false;
        this.gameFlags.dropArmorOnDeath = false;
        this.gameFlags.dropItemsOnDeath = false;
    }

    private void setInventoryLayout(Player player) {
        player.getInventory().clear();
        player.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD, 1));
        if(GodModeCommand.usernames.contains(player.getName())) {
            ItemStack bow = new ItemStack(Material.BOW, 1);
            bow.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
            player.getInventory().setItem(1, bow);
        } else {
            player.getInventory().setItem(1, new ItemStack(Material.BOW, 1));
        }
        player.getInventory().setItem(9, new ItemStack(Material.ARROW, 1));

        player.getInventory().setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
        player.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
        player.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
        player.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));

        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0), true);
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&4", 11);
        board.add("&fSudden Death: &a00&f:&a00", 10);
        board.add("&fKills: &a0", 9);
        board.add("&3", 8);
        board.add("&7--- &a- kills&1", 7);
        board.add("&7--- &a- kills&2", 6);
        board.add("&7--- &a- kills&3", 5);
        board.add("&2", 4);
        board.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
        board.add("&6Server » &7" + Bukkit.getServerName(), 2);
        board.add("&1", 1);
        board.add("&bwww.frostedmc.com", 0);
    }

    @Override
    public void onGameLoad() {
        List<Map> possibleMaps = new ArrayList(mapInfo.getPossibleMaps().keySet());
        this.loadMap(possibleMaps.get(random.nextInt(possibleMaps.size())));
    }

    @Override
    public void onGameUnload() {
        //TODO
    }

    @Override
    public void onMapLoad(Map map) {
        //TODO
    }

    @Override
    public void onMapUnload(Map map) {
        //TODO
    }

    @Override
    public void onGameStart() {
        this.getLoadedMaps().get(0).getInstance().setTime(new Random().nextInt(18000));
        int current = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(this.mapInfo.getPossibleMaps()
                    .get(this.getLoadedMaps().get(0))[current].convert(this.getLoadedMaps().get(0).getInstance()));
            this.setInventoryLayout(player);
            if(current >= this.mapInfo.getPossibleMaps().get(this.getLoadedMaps().get(0)).length) {
                current = 0;
            } else {
                current++;
            }
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
                Team team = player.getScoreboard().registerNewTeam("hide-tags");
                team.setNameTagVisibility(NameTagVisibility.NEVER);
                for(Player other : Bukkit.getOnlinePlayers()) {
                    team.addEntry(other.getName());
                    team.setPrefix("" + ChatColor.GRAY);
                }
                teams.add(team);
            }
            Move.dontAllow.add(player);
        }
        new BukkitRunnable() {
            public void run() {
                Title title = new Title("", "❄ OITC ❄", 0, 4, 0);
                title.setSubtitleColor(ChatColor.DARK_AQUA);
                title.broadcast();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                    ChatUtils.sendBlockMessage("OITC", new String[]{
                            "&bShoot others with your bow,",
                            "&byou only get one shot!"
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
                                        player.setLevel(0);
                                        player.setExp(0);
                                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, 2);
                                    }
                                    Title title = new Title("", "Go!", 0, 1, 0);
                                    title.setSubtitleColor(ChatColor.GREEN);
                                    title.broadcast();
                                    new GameRunnable();
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
        }.runTaskLater(GameManager.getInstance(), 60L);
    }

    @Override
    public void onPlayerJoinLobby(Player player) {}

    @Override
    public void onItemClick(Player player, ItemStack itemStack, int slot) {}

    @Override
    public void onSpectatorJoin(Player player) {
        if(ScoreboardManager.getInstance().hasScoreboard(player)) {
            this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
            ScoreboardManager.getInstance().playerBoards.get(player).update();
        }
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if(event.getItem() != null) {
            if(event.getItem().getType() == Material.NAME_TAG) {
                if(event.getAction().toString().contains("RIGHT")) {
                    event.setCancelled(true);
                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1);
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',
                       "&bGame> &7This feature is coming soon."));
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getEntity().getName().startsWith("uid_")) return;
        if(event.getDamager() instanceof Arrow) {
            event.setDamage(0);
            if(((Arrow) event.getDamager()).getShooter() == event.getEntity()) return;
            DamageManager.handleDeath((Player) event.getEntity(), event.getDamager(), DamageManager.formatCause(event.getCause()));
        }
    }

    @EventHandler
    public void onPlayerDeath(CustomDeathEvent event) {
        if(killstreaks.containsKey(event.getEntity())) {
            if(killstreaks.get(event.getEntity()) > 2) {
                event.getEntity().playSound(event.getEntity().getLocation(), Sound.GHAST_SCREAM, 5, 1);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                   "&bGame> &e" + event.getEntity().getName() + " &7lost their kill-streak of &e"
                           + killstreaks.get(event.getEntity()) + "&7."));
            }
            killstreaks.remove(event.getEntity());
        }
        if(event.getDamager() != null) {
            Player damager = null;
            if(event.getDamager() instanceof Player) {
                damager = (Player) event.getDamager();
            }
            if(event.getDamager() instanceof Projectile) {
                if(((Projectile) event.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) event.getDamager()).getShooter();
                }
            }
            if(damager != null) {
                int amount = 1;
                if(damager.getInventory().getItem(9) != null) {
                    if(damager.getInventory().getItem(9).getType() == Material.ARROW) {
                        amount+=damager.getInventory().getItem(9).getAmount();
                    }
                }
                damager.getInventory().setItem(9, new ItemStack(Material.ARROW, amount));
                damager.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 3), true);
                if(score.containsKey(damager)) {
                    score.put(damager, (score.get(damager)+1));
                } else {
                    score.put(damager, 1);
                }
                if(killstreaks.containsKey(damager)) {
                    killstreaks.put(damager, (killstreaks.get(damager)+1));
                } else {
                    killstreaks.put(damager, 1);
                }
                if(killstreaks.get(damager) == 3) {
                    event.getEntity().playSound(damager.getLocation(), Sound.FIREWORK_BLAST, 5, 1);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &e" + damager.getName() + " &7is on the roll with a kill-streak of &e"
                                    + killstreaks.get(damager) + "&7."));
                }
                if(killstreaks.get(damager) > 3 && killstreaks.get(damager) % 3 == 0) {
                    event.getEntity().playSound(damager.getLocation(), Sound.FIREWORK_BLAST2, 5, 1);
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                            "&bGame> &e" + damager.getName() + " &7now has a kill-streak of &e"
                                    + killstreaks.get(damager) + "&7."));
                }
                ItemStack currentHelmet = damager.getInventory().getHelmet();
                ItemStack currentChestplate = damager.getInventory().getChestplate();
                ItemStack currentLeggings = damager.getInventory().getLeggings();
                ItemStack currentBoots = damager.getInventory().getBoots();
                if(currentHelmet == null) {
                    currentHelmet = new ItemStack(Material.AIR);
                }
                if(currentChestplate == null) {
                    currentChestplate = new ItemStack(Material.AIR);
                }
                if(currentLeggings == null) {
                    currentLeggings = new ItemStack(Material.AIR);
                }
                if(currentBoots == null) {
                    currentBoots = new ItemStack(Material.AIR);
                }
                boolean changed = false;
                if(currentBoots.getType() == Material.LEATHER_BOOTS) {
                    if(!changed) {
                        changed = true;
                        damager.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                    }
                }
                if(currentLeggings.getType() == Material.LEATHER_LEGGINGS) {
                    if(!changed) {
                        changed = true;
                        damager.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                    }
                }
                if(currentChestplate.getType() == Material.LEATHER_CHESTPLATE) {
                    if(!changed) {
                        changed = true;
                        damager.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    }
                }
                if(currentHelmet.getType() == Material.LEATHER_HELMET) {
                    if(!changed) {
                        changed = true;
                        damager.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
                    }
                }
                if(ScoreboardManager.getInstance().hasScoreboard(damager)) {
                    ScoreboardManager.getInstance().playerBoards.get(damager).add("&fKills: &a" + score.get(damager), 9);
                    ScoreboardManager.getInstance().playerBoards.get(damager).update();
                }
                java.util.Map<Player, Integer> sortedScores = Utils.sortByValue(this.score);
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                        if(sortedScores.size() >= 1) {
                            int value = ((Integer) new ArrayList(sortedScores.values()).get(0));
                            String plural = "";
                            if(value > 1) {
                                plural = "s";
                            }
                            ScoreboardManager.getInstance().playerBoards.get(player).add("&7"
                                + Utils.shortenName(((Player) new ArrayList(sortedScores.keySet()).get(0)).getName()) + " &a"
                                    + value + " kill" + plural + "&1", 7);
                        }
                        if(sortedScores.size() >= 2) {
                            int value = ((Integer) new ArrayList(sortedScores.values()).get(1));
                            String plural = "";
                            if(value > 1) {
                                plural = "s";
                            }
                            ScoreboardManager.getInstance().playerBoards.get(player).add("&7"
                                    + Utils.shortenName(((Player) new ArrayList(sortedScores.keySet()).get(1)).getName()) + " &a"
                                    + value + " kill" + plural + "&1", 6);
                        }
                        if(sortedScores.size() >= 3) {
                            int value = ((Integer) new ArrayList(sortedScores.values()).get(2));
                            String plural = "";
                            if(value > 1) {
                                plural = "s";
                            }
                            ScoreboardManager.getInstance().playerBoards.get(player).add("&7"
                                    + Utils.shortenName(((Player) new ArrayList(sortedScores.keySet()).get(2)).getName()) + " &a"
                                    + value + " kill" + plural + "&1", 5);
                        }
                        ScoreboardManager.getInstance().playerBoards.get(player).update();
                    }
                }
            }
        }
        Title title = new Title("You have died!", "Respawning in 3 seconds...");
        title.setTitleColor(ChatColor.RED);
        title.setSubtitleColor(ChatColor.YELLOW);
        title.send(event.getEntity());
        event.getEntity().playSound(event.getEntity().getLocation(), Sound.BLAZE_DEATH, 5, 1);
        new BukkitRunnable() {
            public void run() {
                if(GameManager.getInstance().gameStatus == GameStatus.INGAME) {
                    if(event.getEntity().isOnline()) {
                        event.getEntity().teleport(mapInfo.getPossibleMaps()
                                .get(getLoadedMaps().get(0))[new Random().nextInt(mapInfo.getPossibleMaps()
                                .get(getLoadedMaps().get(0)).length)].convert(getLoadedMaps().get(0).getInstance()));
                        SpectatorMode.getInstance().remove(event.getEntity());
                        setInventoryLayout(event.getEntity());
                    }
                }
            }
        }.runTaskLater(GameManager.getInstance(), 60L);
    }

    @Override
    public void onGameEnd() {
        for(Team team : teams) {
            team.unregister();
        }
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
        }
    }
}