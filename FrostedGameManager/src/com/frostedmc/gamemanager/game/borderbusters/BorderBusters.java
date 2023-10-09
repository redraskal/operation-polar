package com.frostedmc.gamemanager.game.borderbusters;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.core.utils.Title;
import com.frostedmc.core.utils.Utils;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.Map;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.event.CustomDeathEvent;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.DamageManager;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Redraskal_2 on 2/26/2017.
 */
public class BorderBusters extends Game {

    @Override
    public String gameName() {
        return "BORDER BUSTERS";
    }

    public MapInfo mapInfo = new MapInfo();
    public int stage = 1;
    public List<Block> possibleLocations = new ArrayList<Block>();
    public java.util.Map<Player, Integer> deaths = new HashMap<Player, Integer>();
    public java.util.Map<Player, Long> deathTime = new HashMap<Player, Long>();

    public BorderBusters() {
        this.gameFlags.minPlayers = 2;
        this.gameFlags.maxPlayers = 12;
        this.gameFlags.enablePVE = true;
        this.gameFlags.enablePVP = false;
        this.gameFlags.enablePVM = false;
        this.gameFlags.dropArmorOnDeath = false;
        this.gameFlags.dropItemsOnDeath = false;
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&3", 6);
        board.add("&fStage: &a" + stage, 5);
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

    }

    @Override
    public void onMapLoad(Map map) {

    }

    @Override
    public void onMapUnload(Map map) {

    }

    @Override
    public void onGameStart() {
        this.getLoadedMaps().get(0).getInstance().setTime(new Random().nextInt(18000));
        int current = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(this.mapInfo.getPossibleMaps()
                    .get(this.getLoadedMaps().get(0))[current].convert(this.getLoadedMaps().get(0).getInstance()));
            player.getInventory().clear();
            if(current >= this.mapInfo.getPossibleMaps().get(this.getLoadedMaps().get(0)).length) {
                current = 0;
            } else {
                current++;
            }
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
            Move.dontAllow.add(player);
        }
        for(CustomLocation location : this.mapInfo.getPossibleMaps()
                .get(this.getLoadedMaps().get(0))) {
            for(Block near :
                    Utils.getBlocksInRadius(location.convert(this.getLoadedMaps().get(0).getInstance()), 5, false)) {
                if(near.getRelative(BlockFace.DOWN).getType().isSolid()) {
                    if(!possibleLocations.contains(near)) {
                        possibleLocations.add(near);
                    }
                }
            }
        }
        possibleLocations = possibleLocations.stream().distinct().collect(Collectors.toList());
        new BukkitRunnable() {
            public void run() {
                Title title = new Title("", "❄ Border Busters ❄", 0, 4, 0);
                title.setSubtitleColor(ChatColor.DARK_AQUA);
                title.broadcast();
                for(Player player : Bukkit.getOnlinePlayers()) {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
                    ChatUtils.sendBlockMessage("Border Busters", new String[]{
                            "&bRun to the safe haven",
                            "&bbefore it's too late!"
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
                                    new BukkitRunnable() {
                                        public void run() {
                                            new GameRunnable();
                                        }
                                    }.runTaskLater(GameManager.getInstance(), 20L);
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

    @EventHandler
    public void onPlayerDeath(CustomDeathEvent event) {
        deaths.put(event.getEntity(), this.stage);
        deathTime.put(event.getEntity(), System.currentTimeMillis());
        if(SpectatorMode.getInstance().get().size() >= Bukkit.getOnlinePlayers().size()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if (!deaths.containsKey(player)) {
                    deaths.put(player, stage);
                }
                if(!SpectatorMode.getInstance().contains(player)
                        && !deathTime.containsKey(player)) {
                    deathTime.put(player, System.currentTimeMillis());
                }
            }
            GameManager.getInstance().endGame(this.getLoadedMaps().get(0)
                    .getSpectatorLocation().convert(this.getLoadedMaps().get(0).getInstance()));
            java.util.Map<Player, Long> sortedScores = com.frostedmc.gamemanager.Utils.sortByValue(this.deathTime);
            for(Player player : Bukkit.getOnlinePlayers()) {
                int temp = this.deaths.get(player);
                int icicles = 10;
                String firstPlace = "---";
                String secondPlace = "---";
                String thirdPlace = "---";
                if(sortedScores.size() >= 1) {
                    firstPlace = ((Player) new ArrayList(sortedScores.keySet()).get(0)).getName();
                }
                if(sortedScores.size() >= 2) {
                    secondPlace = ((Player) new ArrayList(sortedScores.keySet()).get(1)).getName();
                }
                if(sortedScores.size() >= 3) {
                    thirdPlace = ((Player) new ArrayList(sortedScores.keySet()).get(2)).getName();
                }
                if(player.getName().equalsIgnoreCase(firstPlace)) {
                    icicles+=9;
                }
                if(player.getName().equalsIgnoreCase(secondPlace)) {
                    icicles+=6;
                }
                if(player.getName().equalsIgnoreCase(thirdPlace)) {
                    icicles+=3;
                }
                Core.getInstance().getAccountManager().update(player.getUniqueId(),
                        new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                (Core.getInstance().getAccountManager().parseDetails(player.getUniqueId())
                                        .getIcicles()+icicles)));
                ChatUtils.sendBlockMessage("Game Summary", new String[]{
                        "&a&l1st Place: &3" + firstPlace,
                        "&e&l2nd Place: &3" + secondPlace,
                        "&c&l3rd Place: &3" + thirdPlace,
                        "&7",
                        "&7You have survived until &3Stage " + temp + "&7!",
                        "&7You have earned &3" + icicles + " &7icicle(s)!"
                }, player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            event.setDamage(1);
            DamageManager.handleDeath((Player) event.getEntity(), null, "World Border");
        }
    }

    @Override
    public void onSpectatorJoin(Player player) {
        if(ScoreboardManager.getInstance().hasScoreboard(player)) {
            this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
            ScoreboardManager.getInstance().playerBoards.get(player).update();
        }
    }

    @Override
    public void onGameEnd() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
        }
    }
}