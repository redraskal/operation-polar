package com.frostedmc.gamemanager.game.rocketroyal;

import com.frostedmc.core.Core;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.module.defaults.NametagModule;
import com.frostedmc.core.utils.SimpleScoreboard;
import com.frostedmc.gamemanager.Cuboid;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.Utils;
import com.frostedmc.gamemanager.api.CustomLocation;
import com.frostedmc.gamemanager.api.Game;
import com.frostedmc.gamemanager.api.GameStatus;
import com.frostedmc.gamemanager.api.Map;
import com.frostedmc.gamemanager.event.CustomDeathEvent;
import com.frostedmc.gamemanager.event.GameMechanicRespawnEvent;
import com.frostedmc.gamemanager.game.rocketroyal.runnable.GameStartRunnable;
import com.frostedmc.gamemanager.listener.Move;
import com.frostedmc.gamemanager.manager.ScoreboardManager;
import com.frostedmc.gamemanager.mechanic.MapBoundaryMechanic;
import com.frostedmc.gamemanager.mechanic.ReloadMechanic;
import com.frostedmc.gamemanager.mechanic.RespawnMechanic;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class RocketRoyal extends Game {

    @Override
    public String gameName() {
        return "ROCKET ROYAL";
    }

    private MapInfo mapInfo;
    @Getter private List<Powerup> powerups = new ArrayList<>();
    @Getter private java.util.Map<Player, Integer> reloadDelays = new HashMap<>();
    @Getter private java.util.Map<Player, Integer> kills = new HashMap<>();
    @Getter private java.util.Map<Player, Integer> killStreaks = new HashMap<>();
    private List<Player> usingGrapplingHook = new ArrayList<>();

    @Getter private ItemStack rocketLauncherItemStack;

    private ReloadMechanic reloadMechanic;

    public RocketRoyal() {
        this.gameFlags.minPlayers = 2;
        this.gameFlags.maxPlayers = 8;
        this.gameFlags.enablePVE = false;
        this.gameFlags.enablePVP = false;
        this.gameFlags.enablePVM = false;
        this.gameFlags.dropArmorOnDeath = false;
        this.gameFlags.dropItemsOnDeath = false;
        this.gameFlags.instaKillExplosions = true;
    }

    public void setDefaultLines(SimpleScoreboard board) {
        board.reset();
        board.add("&4", 11);
        board.add("&fTime Limit: &a00&f:&a00", 10);
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
        this.mapInfo = new MapInfo();
        List<Map> possibleMaps = new ArrayList(mapInfo.getPossibleMaps().keySet());
        this.loadMap(possibleMaps.get(random.nextInt(possibleMaps.size())));

        this.rocketLauncherItemStack = ItemCreator.getInstance().createItem(Material.BLAZE_ROD,
                1, 0, "&6&lRocket Launcher", Arrays.asList(new String[]{
                        "&e&lLeft-Click &7to shoot",
                        "&e&lRight-Click &7to grapple"
                }));
    }

    @Override
    public void onGameUnload() {}

    @Override
    public void onMapLoad(Map map) {
        map.getInstance().setDifficulty(Difficulty.EASY);
    }

    @Override
    public void onMapUnload(Map map) {}

    @Override
    public void onGameStart() {
        this.spawnPowerupCrystals();
        this.teleportPlayers();

        new GameStartRunnable(this);
    }

    public void registerGameMechanics() {
        this.reloadMechanic = new ReloadMechanic(true);
        new RespawnMechanic(3,
                this.mapInfo.getPossibleMaps().get(this.getLoadedMaps().get(0))
                        .getSpawnpoints());

        List<CustomLocation> mapBoundaryLocations = this.mapInfo.getPossibleMaps()
                .get(this.getLoadedMaps().get(0)).getMapBounds();
        World mapWorld = this.getLoadedMaps().get(0).getInstance();
        Cuboid mapBoundary = new Cuboid(
                mapBoundaryLocations.get(0).convert(mapWorld),
                mapBoundaryLocations.get(1).convert(mapWorld));
        new MapBoundaryMechanic(3, mapBoundary);
    }

    public void spawnPowerupCrystals() {
        List<CustomLocation> enderCrystals = this.mapInfo.getPossibleMaps()
                .get(this.getLoadedMaps().get(0)).getEnderCrystals();

        for(CustomLocation enderCrystalSpawnpoint : enderCrystals) {
            Location location = enderCrystalSpawnpoint.convert(this.getLoadedMaps().get(0).getInstance());
            location.getChunk().load(false);
            EnderCrystal enderCrystal = location.getWorld().spawn(location, EnderCrystal.class);
            enderCrystal.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b&lPOWER UP"));
            enderCrystal.setCustomNameVisible(true);
            powerups.add(new Powerup(location.getBlock().getRelative(BlockFace.DOWN), enderCrystal));
        }
    }

    public void setInventoryLayout(Player player) {
        player.getInventory().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false, false));
    }

    public void teleportPlayers() {
        this.mapInfo.getPossibleMaps().get(this.getLoadedMaps().get(0)).scrambleSpawnPoints();
        List<CustomLocation> spawnpoints = this.mapInfo.getPossibleMaps()
                .get(this.getLoadedMaps().get(0)).getSpawnpoints();

        int current = 0;
        NametagModule nametagModule = (NametagModule) Core.getInstance().getModule(NametagModule.class);

        for(Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(spawnpoints.get(current).convert(this.getLoadedMaps().get(0).getInstance()));
            if(ScoreboardManager.getInstance().hasScoreboard(player)) {
                this.setDefaultLines(ScoreboardManager.getInstance().playerBoards.get(player));
                ScoreboardManager.getInstance().playerBoards.get(player).update();
                nametagModule.updateTag(player, "&e&l 0 kills");
            }

            this.setInventoryLayout(player);

            Move.dontAllow.add(player);
            if(current >= spawnpoints.size()) {
                current = 0;
            } else {
                current++;
            }
        }
    }

    public void shootGrapplingHook(Player player) {
        if(usingGrapplingHook.contains(player)) return;
        usingGrapplingHook.add(player);
        Slime slime = player.getWorld().spawn(player.getLocation().add(0, 1D, 0), Slime.class);
        slime.setSize(1);
        slime.setLeashHolder(player);
        slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1), true);
        slime.setVelocity(player.getLocation().getDirection().multiply(3D));
        slime.setMetadata("grappling",
                new FixedMetadataValue(GameManager.getInstance(), player.getUniqueId().toString()));
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(slime.isDead()) {
                    this.cancel();
                    usingGrapplingHook.remove(player);
                    return;
                }
                if(ticks >= 60) {
                    this.cancel();
                    slime.remove();
                    usingGrapplingHook.remove(player);
                } else {
                    if(slime.getLocation().getBlock().getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    if(slime.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    if(slime.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    if(slime.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    if(slime.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    if(slime.getLocation().getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    if(slime.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                        onEntityDamage(new EntityDamageEvent(slime, EntityDamageEvent.DamageCause.CUSTOM, 0D));
                    }
                    slime.getWorld().spigot().playEffect(slime.getLocation(), Effect.CRIT,
                            0, 0, 0, 0, 0,
                            0, 1, 30);
                    ticks++;
                }
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    public void shootRocket(Player player) {
        if(!this.reloadMechanic.isReloaded(player)) return;
        if(reloadDelays.containsKey(player)) {
            this.reloadMechanic.reload(player, reloadDelays.get(player));
        } else {
            this.reloadMechanic.reload(player, 15);
        }
        Fireball rocket = player.launchProjectile(Fireball.class);
        rocket.setShooter(player);
        rocket.setYield(0F);
        rocket.setIsIncendiary(false);
        rocket.setBounce(false);
        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(rocket.isDead()) {
                    this.cancel();
                    return;
                }
                if(ticks >= 60) {
                    this.cancel();
                    rocket.remove();
                } else {
                    if(ticks % 5 == 0) {
                        rocket.getWorld().spigot().playEffect(rocket.getLocation(), Effect.FLAME, 0, 0,
                                0, 0, 0,
                                0, 3, 60);
                        rocket.getWorld().spigot().playEffect(rocket.getLocation(), Effect.SMOKE, 0, 0,
                                0, 0, 0,
                                0, 3, 60);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(GameManager.getInstance(), 0, 1L);
    }

    @Override
    public void onPlayerJoinLobby(Player player) {}

    @Override
    public void onItemClick(Player player, ItemStack itemStack, int slot) {}

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_AIR
                && event.getItem() != null
                && event.getItem().getType() == Material.BLAZE_ROD) {
            this.shootRocket(event.getPlayer());
        }
        if(event.getAction() == Action.RIGHT_CLICK_AIR
                && event.getItem() != null
                && event.getItem().getType() == Material.BLAZE_ROD) {
            this.shootGrapplingHook(event.getPlayer());
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent playerToggleFlightEvent) {
        if(playerToggleFlightEvent.getPlayer().getGameMode() == GameMode.SURVIVAL
                || playerToggleFlightEvent.getPlayer().getGameMode() == GameMode.ADVENTURE) {
            playerToggleFlightEvent.setCancelled(true);
            playerToggleFlightEvent.getPlayer().setAllowFlight(false);
            playerToggleFlightEvent.getPlayer().playSound(playerToggleFlightEvent.getPlayer().getLocation(), Sound.IRONGOLEM_THROW, 1f, 0.9f);
            playerToggleFlightEvent.getPlayer().getWorld().spigot().playEffect(playerToggleFlightEvent.getPlayer().getLocation(), Effect.CLOUD,
                    0, 0,
                    1, 1, 1,
                    0, 5, 30);
            playerToggleFlightEvent.getPlayer().setVelocity(playerToggleFlightEvent.getPlayer().getLocation().getDirection()
                    .multiply((1.6D)).setY((1.4D)));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.getFrom().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
                && event.getTo().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
                && GameManager.getInstance().gameStatus == GameStatus.INGAME
                && !Move.dontAllow.contains(event.getPlayer())) {
            event.getPlayer().setAllowFlight(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof EnderCrystal) {
            event.setCancelled(true);
        }
        if(event.getEntity() instanceof Fireball) {
            if(event.getDamager() instanceof EnderCrystal) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeashItemDrop(EntitySpawnEvent event) {
        if(event.getEntity() instanceof org.bukkit.entity.Item) {
            if(((org.bukkit.entity.Item) event.getEntity()).getItemStack().getType() == Material.LEASH) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Slime) {
            event.setCancelled(true);
            if(event.getEntity().hasMetadata("grappling")) {
                UUID uuid = UUID.fromString(event.getEntity().getMetadata("grappling").get(0).asString());
                Player player = Bukkit.getPlayer(uuid);
                if(player != null) {
                    player.setVelocity(event.getEntity().getLocation().toVector()
                            .subtract(player.getLocation().toVector()).multiply(3D));
                }
                event.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onLeashBreak(HangingBreakEvent event) {
        if (event.getEntity() instanceof LeashHitch) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(CustomDeathEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();

        if(killStreaks.containsKey(event.getEntity())) {
            if(killStreaks.get(event.getEntity()) > 2) {
                event.getEntity().playSound(event.getEntity().getLocation(), Sound.GHAST_SCREAM, 5, 1);
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                        "&bGame> &e" + event.getEntity().getName() + " &7lost their kill-streak of &e"
                                + killStreaks.get(event.getEntity()) + "&7."));
            }
            killStreaks.remove(event.getEntity());
        }

        if(getKills().containsKey(damager)) {
            getKills().put(damager, getKills().get(damager)+1);
        } else {
            getKills().put(damager, 1);
        }

        if(killStreaks.containsKey(damager)) {
            killStreaks.put(damager, (killStreaks.get(damager)+1));
        } else {
            killStreaks.put(damager, 1);
        }
        if(killStreaks.get(damager) == 3) {
            event.getEntity().playSound(damager.getLocation(), Sound.FIREWORK_BLAST, 5, 1);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bGame> &e" + damager.getName() + " &7is on the roll with a kill-streak of &e"
                            + killStreaks.get(damager) + "&7."));
        }
        if(killStreaks.get(damager) > 3 && killStreaks.get(damager) % 3 == 0) {
            event.getEntity().playSound(damager.getLocation(), Sound.FIREWORK_BLAST2, 5, 1);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    "&bGame> &e" + damager.getName() + " &7now has a kill-streak of &e"
                            + killStreaks.get(damager) + "&7."));
        }

        NametagModule nametagModule = (NametagModule) Core.getInstance().getModule(NametagModule.class);
        if(getKills().get(damager) == 1) {
            nametagModule.updateTag(damager, "&e&l 1 kill");
        } else {
            nametagModule.updateTag(damager, "&e&l " + getKills().get(damager) + " kills");
        }

        if(ScoreboardManager.getInstance().hasScoreboard(damager)) {
            ScoreboardManager.getInstance().playerBoards.get(damager).add("&fKills: &a" + getKills().get(damager), 9);
            ScoreboardManager.getInstance().playerBoards.get(damager).update();
        }

        java.util.Map<Player, Integer> sortedScores = Utils.sortByValue(this.getKills());
        for(Player player : Bukkit.getOnlinePlayers()) {
            if (ScoreboardManager.getInstance().hasScoreboard(player)) {
                if (sortedScores.size() >= 1) {
                    int value = ((Integer) new ArrayList(sortedScores.values()).get(0));
                    String plural = "";
                    if (value > 1) {
                        plural = "s";
                    }
                    ScoreboardManager.getInstance().playerBoards.get(player).add("&7"
                            + Utils.shortenName(((Player) new ArrayList(sortedScores.keySet()).get(0)).getName()) + " &a"
                            + value + " kill" + plural + "&1", 7);
                }
                if (sortedScores.size() >= 2) {
                    int value = ((Integer) new ArrayList(sortedScores.values()).get(1));
                    String plural = "";
                    if (value > 1) {
                        plural = "s";
                    }
                    ScoreboardManager.getInstance().playerBoards.get(player).add("&7"
                            + Utils.shortenName(((Player) new ArrayList(sortedScores.keySet()).get(1)).getName()) + " &a"
                            + value + " kill" + plural + "&1", 6);
                }
                if (sortedScores.size() >= 3) {
                    int value = ((Integer) new ArrayList(sortedScores.values()).get(2));
                    String plural = "";
                    if (value > 1) {
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

    @EventHandler
    public void onPlayerRespawn(GameMechanicRespawnEvent event) {
        setInventoryLayout(event.getPlayer());
        event.getPlayer().getInventory().addItem(getRocketLauncherItemStack());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().setAllowFlight(false);
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
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().clear());
    }
}