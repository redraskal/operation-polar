package com.frostedmc.gamemanager.game.parkourmania;

import com.frostedmc.core.Core;
import com.frostedmc.core.api.account.UpdateDetails;
import com.frostedmc.core.utils.ChatUtils;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.gamemanager.BlankGenerator;
import com.frostedmc.gamemanager.FileUtils;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Utils;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.SpectatorMode;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.DamageManager;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/18/2017.
 */
public class ParkourMania extends Game {

    @Override
    public String gameName() {
        return "PARKOUR MANIA";
    }

    public World map;
    public Map<UUID, Location> startLocations = new HashMap<UUID, Location>();

    public ParkourMania() {
        this.gameFlags.minPlayers = 2;
        this.gameFlags.maxPlayers = 12;
        this.gameFlags.enablePVE = false;
        this.gameFlags.enablePVP = false;
        this.gameFlags.enablePVM = false;
        this.gameFlags.dropArmorOnDeath = false;
        this.gameFlags.dropItemsOnDeath = false;
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&3", 6);
        board.add("&fDistance: &a0 blocks", 5);
        board.add("&2", 4);
        board.add("&eOnline » &7" + Bukkit.getOnlinePlayers().size(), 3);
        board.add("&6Server » &7" + Bukkit.getServerName(), 2);
        board.add("&1", 1);
        board.add("&bwww.frostedmc.com", 0);
    }

    @Override
    public void onGameLoad() {
        String mapName = "map_" + UUID.randomUUID().toString();
        Bukkit.getServer().getLogger().info("[/] Copying new blank world.");
        Bukkit.getServer().getLogger().info("[/] Loading " + mapName + ".");
        WorldCreator worldCreator = new WorldCreator(mapName);
        worldCreator.generator(new BlankGenerator());
        worldCreator.environment(World.Environment.NETHER);
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.FLAT);
        this.map = Bukkit.getServer().createWorld(worldCreator);
        map.setGameRuleValue("doFireTick", "false");
        map.setGameRuleValue("doMobSpawning", "false");
        map.setGameRuleValue("randomTickSpeed", "0");
        map.setGameRuleValue("doDaylightCycle", "false");
    }

    @Override
    public void onGameUnload() {
        Bukkit.getServer().getLogger().info("[/] Unloading " + map.getName() + ".");
        final File temp = map.getWorldFolder();
        final String name = map.getName();
        this.map.setAutoSave(false);
        this.map = null;
        Utils.UnloadWorld(GameManager.getInstance(), Bukkit.getWorld(name), false);
        Utils.ClearWorldReferences(name);
        File sessionLock = new File(temp, "session.lock");
        sessionLock.setReadable(true);
        sessionLock.setWritable(true);
        sessionLock.setExecutable(true);
        sessionLock.delete();
        try {
            FileUtils.quickDelete(temp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().getLogger().info("[/] Unloaded " + name + ".");
    }

    @Override
    public void onMapLoad(com.frostedmc.gamemanager.api.Map map) {

    }

    @Override
    public void onMapUnload(com.frostedmc.gamemanager.api.Map map) {

    }

    @Override
    public void onGameStart() {
        int x = 0;
        for(Player player : Bukkit.getOnlinePlayers()) {
            Location center = new Location(map, x + .5, 36.5, 0.5, -179f, 8.8f);
            center.getBlock().setType(Material.SEA_LANTERN);
            for(BlockFace blockFace : BlockFace.values()) {
                if(blockFace != BlockFace.UP
                        && blockFace != BlockFace.DOWN
                        && blockFace != BlockFace.SELF) {
                    if(blockFace.toString().contains("_")) {
                        if(!blockFace.toString().replaceFirst("_", "").contains("_")) {
                            center.getBlock().getRelative(blockFace).setType(Material.PRISMARINE);
                            center.getBlock().getRelative(blockFace).setData((byte) 1);
                        }
                    } else {
                        center.getBlock().getRelative(blockFace).setType(Material.PRISMARINE);
                        center.getBlock().getRelative(blockFace).setData((byte) 1);
                    }
                }
            }
            startLocations.put(player.getUniqueId(), center);
            player.setGameMode(GameMode.CREATIVE);
            player.teleport(center);
            player.getInventory().clear();
            player.setHealthScale(6);
            player.setHealth(6);
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 200), true);
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
            player.setGameMode(GameMode.ADVENTURE);
            Move.dontAllow.add(player);
            x+=10;
        }
        new GameRunnable();
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(SpiderCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(SpeedCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(GroundSpoofCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(StepCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(JumpSpeedCheck.class));
    }

    @Override
    public void onPlayerJoinLobby(Player player) {}

    @Override
    public void onItemClick(Player player, ItemStack itemStack, int slot) {}

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if(event.getRegainReason() != EntityRegainHealthEvent.RegainReason.MAGIC_REGEN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo().getY() <= 30) {
            if(!SpectatorMode.getInstance().contains(event.getPlayer())) {
                event.setTo(startLocations.get(event.getPlayer().getUniqueId()).clone().add(0, 2, 0));
                if(event.getPlayer().getHealth() - 6 <= 0) {
                    DamageManager.handleDeath(event.getPlayer(), null, "Void");
                } else {
                    event.getPlayer().setHealth((event.getPlayer().getHealth()-6));
                }
                if(SpectatorMode.getInstance().get().size() >= Bukkit.getOnlinePlayers().size()) {
                    GameManager.getInstance().endGame(event.getTo().clone().add(0, 5, 0));
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        int icicles = 10;
                        Core.getInstance().getAccountManager().update(player.getUniqueId(),
                                new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                        (Core.getInstance().getAccountManager().parseDetails(player.getUniqueId())
                                                .getIcicles() + icicles)
                                )
                        );
                        ChatUtils.sendBlockMessage("Game Summary", new String[]{
                                "&3&lIt's a draw!",
                                "&7",
                                "&7You have earned &3" + icicles + " &7icicle(s)!"
                        }, player);
                    }
                }
            }
        }
        if(event.getTo().getBlock().getType() == Material.DIAMOND_BLOCK
                || event.getTo().getBlock().getRelative(BlockFace.DOWN).getType() == Material.DIAMOND_BLOCK) {
            if(SpectatorMode.getInstance().contains(event.getPlayer())) return;
            Map<Player, Integer> distanceMap = new HashMap<Player, Integer>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!SpectatorMode.getInstance().contains(player)) {
                    distanceMap.put(player, Double.valueOf(player.getLocation()
                            .distance(startLocations.get(player.getUniqueId()))).intValue());
                }
            }
            GameManager.getInstance().endGame(event.getTo().clone().add(0, 5, 0));
            java.util.Map<Player, Integer> sortedScores = Utils.sortByValue(distanceMap);
            for(Player player : Bukkit.getOnlinePlayers()) {
                int icicles = 10;
                String firstPlace = "---";
                String secondPlace = "---";
                String thirdPlace = "---";
                if (sortedScores.size() >= 1) {
                    firstPlace = ((Player) new ArrayList(sortedScores.keySet()).get(0)).getName();
                }
                if (sortedScores.size() >= 2) {
                    secondPlace = ((Player) new ArrayList(sortedScores.keySet()).get(1)).getName();
                }
                if (sortedScores.size() >= 3) {
                    thirdPlace = ((Player) new ArrayList(sortedScores.keySet()).get(2)).getName();
                }
                if (player.getName().equalsIgnoreCase(firstPlace)) {
                    icicles += 9;
                }
                if (player.getName().equalsIgnoreCase(secondPlace)) {
                    icicles += 6;
                }
                if (player.getName().equalsIgnoreCase(thirdPlace)) {
                    icicles += 3;
                }
                Core.getInstance().getAccountManager().update(player.getUniqueId(),
                        new UpdateDetails(UpdateDetails.UpdateType.ICICLES,
                                (Core.getInstance().getAccountManager().parseDetails(player.getUniqueId())
                                        .getIcicles() + icicles)
                        )
                );
                ChatUtils.sendBlockMessage("Game Summary", new String[]{
                        "&a&l1st Place: &3" + firstPlace,
                        "&e&l2nd Place: &3" + secondPlace,
                        "&c&l3rd Place: &3" + thirdPlace,
                        "&7",
                        "&7You have earned &3" + icicles + " &7icicle(s)!"
                }, player);
            }
        }
    }

    @Override
    public void onSpectatorJoin(Player player) {
        player.teleport(new Location(map, 0, 36, 0));
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
        //CheckManager.getInstance().registerCheck(new SpiderCheck());
        //CheckManager.getInstance().registerCheck(new SpeedCheck());
        //CheckManager.getInstance().registerCheck(new GroundSpoofCheck());
        //CheckManager.getInstance().registerCheck(new StepCheck());
        //CheckManager.getInstance().registerCheck(new JumpSpeedCheck());
    }
}