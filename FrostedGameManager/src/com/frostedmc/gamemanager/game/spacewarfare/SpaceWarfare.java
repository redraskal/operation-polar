package com.frostedmc.gamemanager.game.spacewarfare;

import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.gamemanager.BlankGenerator;
import com.frostedmc.gamemanager.FileUtils;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Utils;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.Map;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 3/19/2017.
 */
public class SpaceWarfare extends Game {

    @Override
    public String gameName() {
        return "SPACE WARFARE";
    }

    public World map;
    public java.util.Map<Player, Cruiser> vehicles = new HashMap<Player, Cruiser>();

    public SpaceWarfare() {
        this.gameFlags.minPlayers = 2;
        this.gameFlags.maxPlayers = 12;
        this.gameFlags.enablePVE = false;
        this.gameFlags.enablePVP = false;
        this.gameFlags.enablePVM = false;
        this.gameFlags.dropArmorOnDeath = false;
        this.gameFlags.dropItemsOnDeath = false;
        this.gameFlags.allowInteract = false;
        this.gameFlags.allowItemPickup = false;
        this.gameFlags.allowItemDrop = false;
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&3", 6);
        board.add("&fAlive: &a--- players", 5);
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
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generateStructures(false);
        worldCreator.type(WorldType.FLAT);
        this.map = Bukkit.getServer().createWorld(worldCreator);
        map.setGameRuleValue("doFireTick", "false");
        map.setGameRuleValue("doMobSpawning", "false");
        map.setGameRuleValue("randomTickSpeed", "0");
        map.setGameRuleValue("doDaylightCycle", "false");
        map.setTime(18000L);
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
    public void onMapLoad(Map map) {

    }

    @Override
    public void onMapUnload(Map map) {

    }

    @Override
    public void onGameStart() {
        int x = 0;
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(SpiderCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(SpeedCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(GroundSpoofCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(StepCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(JumpSpeedCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(FlyCheck.class));
        //CheckManager.getInstance().unregisterCheck(CheckManager.getInstance().byClass(BlinkCheck.class));
        for(Player player : Bukkit.getOnlinePlayers()) {
            Location center = new Location(map, x + .5, 36, 0.5, -179f, 8.8f);
            center.getBlock().getRelative(BlockFace.DOWN).setType(Material.BARRIER);
            player.setGameMode(GameMode.CREATIVE);
            player.teleport(center);
            player.getInventory().clear();
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
            }
            Move.dontAllow.add(player);
            x+=10;
        }
        new GameRunnable();
    }

    @Override
    public void onPlayerJoinLobby(Player player) {}

    @Override
    public void onItemClick(Player player, ItemStack itemStack, int slot) {}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(vehicles.containsKey(event.getPlayer())) {
            vehicles.remove(event.getPlayer());
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
        for(Cruiser cruiser : vehicles.values()) {
            cruiser.despawn();
        }
        vehicles.clear();
        //CheckManager.getInstance().registerCheck(new SpiderCheck());
        //CheckManager.getInstance().registerCheck(new SpeedCheck());
        //CheckManager.getInstance().registerCheck(new GroundSpoofCheck());
        //CheckManager.getInstance().registerCheck(new StepCheck());
        //CheckManager.getInstance().registerCheck(new JumpSpeedCheck());
        //CheckManager.getInstance().registerCheck(new FlyCheck());
        //CheckManager.getInstance().registerCheck(new BlinkCheck());
    }
}