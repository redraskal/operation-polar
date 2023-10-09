package com.frostedmc.hub.module;

import com.frostedmc.core.module.Module;
import com.frostedmc.core.utils.MessageChannel;
import com.frostedmc.core.utils.NBTUtils;
import com.frostedmc.hub.Hub;
import com.frostedmc.hub.gui.GameServerViewGUI;
import com.frostedmc.hub.manager.CustomPubSub;
import com.frostedmc.hub.util.NPC;
import com.frostedmc.hub.util.NPCInteractEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Redraskal_2 on 1/29/2017.
 */
public class NPCModule extends Module implements Listener {

    private JavaPlugin javaPlugin;
    private List<NPC> customPlayers = new ArrayList<NPC>();
    private List<Entity> customMobs = new ArrayList<Entity>();
    private Map<NPC, ArmorStand> tagMap = new HashMap<NPC, ArmorStand>();
    private Map<Entity, ArmorStand> tagMap2 = new HashMap<Entity, ArmorStand>();

    public NPCModule(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public String name() {
        return "NPC";
    }

    @Override
    public void onEnable() {
        this.createGameNPC("Nightfall", "Nightfall",
                new Location(Bukkit.getWorld("world"), 21.5, 86.5, 69.5, -89.8f, -3.6f),
                "eyJ0aW1lc3RhbXAiOjE1MTAxOTYxMDMyOTAsInByb2ZpbGVJZCI6IjNlMjZiMDk3MWFjZDRjNmQ5MzVjNmFkYjE1YjYyMDNhIiwicHJvZmlsZU5hbWUiOiJOYWhlbGUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifSwidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hODJmNWFjZjVmYjY2ZDAxNzVkMzgzYjEzOTUzYmU5NDMwZGQ5YTczNGE2M2QwMTA3ZmRiMzYxOTI1YzIifX19",
                "N3dQqe/NBumWvkMMg0vK4gO4ByVdfB9jo3WxTWDUUBLxEwlliJkGvJxJ2y5kvg9m3xvYWgYAyrD/JC/j2pYEeMmWJaqRr+ATqV6gsiwE2icEQkyY5DWug++/H6k4Yj+yhFvSETjNnINM/cbpOHIbCTmrHW2ZwjRPASG7HguR3DsyOPuDnpXi9ltnjc7ul8YfIYI0K8zFfrhtN4Gy4hBD67/m7gao30Gz8pT/x2itzGOgeC2uVHXx06Z5JoE5byMmYe1oa5ine5hRYf/W1UBRuQ6MpyRd7fA4AigL6VNaIdYZEAI92Gi5Ac1DILDj30NEkZDLEmFDYAc9/TvbdIGRG8TNoqFzuuBz/W6EUgG4a/M0jVBKWFk562vmbYZMJBajBcuP5qcuxs04PB0WdIjemWq9RU6hH3bCd9oQAYfeHWtv1tFa8kjB5AY8xsHm8C6X451gRBOa5k9BNSHHnB91UyzC0xD82Xpf+g46+gn7OM+P9HagefD8huzq31zGP6RDq/STMp1/SDXCKg4e0qZh3ck5pCg1anlHQnRqZ3LBpIC5A8uhtGVx2EdPxLq9xZob5HLvDPsRtKV0oOKEDQGMuY5WMbZ5rOabf/tjyOzUztvNztmV8k/sdV6PX7xqk2Nl/SoPqQeR8lovX1rFslsL5vurEGKJpNJSpYM+4cjerG4=");
        this.createGameNPC("Aerial: Domination", "Aerial",
                new Location(Bukkit.getWorld("world"), 21.5, 86.5, 73.5, -89.8f, -3.6f),
                "eyJ0aW1lc3RhbXAiOjE1MTIwODUwMjkyNTQsInByb2ZpbGVJZCI6IjIzZjFhNTlmNDY5YjQzZGRiZGI1MzdiZmVjMTA0NzFmIiwicHJvZmlsZU5hbWUiOiIyODA3Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8xZWM1ZGFhNWM4NjM5YjI0MjIyMTIyMjlhMjk1MTY4MjU0NTJjYjUwMzc2YTVkOWYwNGFjMTYzYTU2ZGIxYzcifX19",
                "Ub+qmW2h3uq2QgCPNAu+0XPNrcHMaRjgDTFUG1rx54SABzK7h2THEXlDRrFiFwVAYkRT5AKNJlMoWAyvvWplvXgJf/ODO1Rbspt6C1Ugm+6MB7HjHyHSLcf8elEvdQwOOu6piO+7ePsGK0r31aRvpq6cYUnUIZoj5os0AQIwvmYIyIU57CxYvyMVTSB/PNRJpskrfkd2PYKfMkuU2rMk5a9iwyjMwkL4UOxiavmYcDmHhyrgIejxDpsU9nnBNpgfO1AVHj2UuXaibXyWDP7wGl8TY4+Wo1bSxUn1VP4YfbS6CS+l/L5zdgmYIm3fjcq1P5OA7ExgnKRF5pc6kwEdXOln9IBOv7K3WJo+iOV7j7aQMtgQmysMCSFaMBQby+FSA4LxoHcS6WiEcfdUgxkWGJaXPBZ8zVwVs2Nq4zZN7xH4qulO/tL7kkSTV8xG4YA+yR824Ve1lF4RBJyMfpFQaoLGrF0zuW47+1gwqxeIywxrFy/7ZRBXDnqN7y36uK0/9h6L3DxzoAmA8J12Uo3uZ8q/CGctAnF3bfbYbdnGDARRB95l5V5Vyh4aNlLphmh4Ja4PU/ucMIPfmQLU8W6PkEmQm8kXXSlG3CL3OLRqg+gkE82lLrsAdYWOy5Bh4VoPRO9C9j1Vt0AxYb8+ysDnOffw9/H87jBrw3ydQFhQ5vA=");
        this.createGameNPC("Rocket Royal", "RocketRoyal",
                new Location(Bukkit.getWorld("world"), 21.5, 86.5, 71.5, -89.8f, -3.6f),
                "eyJ0aW1lc3RhbXAiOjE1MTgzMTU5MzI5OTUsInByb2ZpbGVJZCI6ImMxZWQ5N2Q0ZDE2NzQyYzI5OGI1ODFiZmRiODhhMjFmIiwicHJvZmlsZU5hbWUiOiJ5b2xvX21hdGlzIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS80NjBjMjY2YTU2YmM0YWY0MDgyZGMzZGY2N2U3Yzc1OTE5NzQ1MTFjNjYwMmY1Yjg5MGM3YTFhMGZhMTgifX19",
                "D8bBB+w96/a9Y+XIljey4D8pMZtiKThkQIWQb0oNK+oIPsFnmSQ0sMUoBArLL8vBSQyGhrM5ItF09mUVvzTmhgzyGseBN5Wo5IzpDu/vMbKY3S4iFWUqOxlpl2gs0uLs9Ho7bIUI+3L9cPddnXFlXYdCvm+Yj4fyIMcqr1AqswmGJe+Mz1935sofwvE+S2PT5AzKb70uahYIRr7GqK4AWv6gxl3oxktHQqcqM1by9o7GxxIk/YQbIP80bG5zhyu1VkGLsXcng8uIVs+dV+IKKXJ0yPmzz5NvWsN1qbsKps+7qWPdcWoyF1gH5ALktVRwYs+cwTfPYPXKieC0qNeDTy86UtfcW16jvMZ/lQwpgTjaQVIWUapa/HMLF1+PyUI6bblzN11pe0bgjaJInQqN9jMadjGTTxaEH+xbnTyQDXdHTEyhQTBW1Ze2nIBVHa38RXML3bWDjhrE/LIl2kitqoNrhulY4doV/9sx7tx4f4C3UA3kbCNOP5JF2cEShHDAuVXTJvFr38balrCPLvo8KPpDymFlKw+VVpo/ry3H6kkpB2fGssam0GGz1F4qW69MBwPG6/KyOfsuujJpIw8gGWImTH+rNH/3RbXSmlsOTfY+M2AgWV9cqkREkhbi7xMNPwiCQvg0XfZMkzjIxfJlhr8Zk/FqvCXOfeB4camuexc=");
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
    }

    public static int players(String template) {
        if(template.equalsIgnoreCase("FGL")) {
            return CustomPubSub.getPlayers(template) + CustomPubSub.getPlayers("FG");
        } else {
            return CustomPubSub.getPlayers(template);
        }
    }

    @EventHandler
    public void onEntityInteract(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player)) return;
        if(event.getEntity().hasMetadata("connect-to-server")) {
            ((Player) event.getDamager()).closeInventory();
            new GameServerViewGUI(((Player) event.getDamager()),
                    event.getEntity().getMetadata("connect-to-server").get(0).asString(),
                    event.getEntity().getMetadata("connect-to-server").get(0).asString() + "-");
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().hasMetadata("connect-to-server")) {
            event.getPlayer().closeInventory();
            new GameServerViewGUI(event.getPlayer(),
                    event.getRightClicked().getMetadata("connect-to-server").get(0).asString(),
                    event.getRightClicked().getMetadata("connect-to-server").get(0).asString() + "-");
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().hasMetadata("connect-to-server")) {
            event.getPlayer().closeInventory();
            new GameServerViewGUI(event.getPlayer(),
                    event.getRightClicked().getMetadata("connect-to-server").get(0).asString(),
                    event.getRightClicked().getMetadata("connect-to-server").get(0).asString() + "-");
        }
    }

    private void createMobNPC(String game, String template, Location location, EntityType entityType) {
        if(location.getWorld().getDifficulty() == Difficulty.PEACEFUL) {
            location.getWorld().setDifficulty(Difficulty.EASY);
        }
        Entity npc = location.getWorld().spawnEntity(location, entityType);
        npc.setMetadata("connect-to-server", new FixedMetadataValue(Hub.getInstance(), template));
        NBTUtils.defaultNPCTags(npc);
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    this.cancel();
                    ArmorStand nameTag = Bukkit.getWorld("world").spawn(location.clone().add(0, 1.3, 0), ArmorStand.class);
                    nameTag.setSmall(true);
                    nameTag.setBasePlate(false);
                    nameTag.setVisible(false);
                    nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&b&l" + game));
                    nameTag.setCustomNameVisible(true);
                    nameTag.setGravity(false);
                    nameTag.setMetadata("custom-tag", new FixedMetadataValue(javaPlugin, true));
                    NBTUtils.defaultNPCTags(nameTag);
                }
            }
        }.runTaskTimer(javaPlugin, 0, 20L);
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    this.cancel();
                    ArmorStand nameTag = Bukkit.getWorld("world").spawn(location.clone().add(0, 0.9, 0), ArmorStand.class);
                    nameTag.setSmall(true);
                    nameTag.setBasePlate(false);
                    nameTag.setVisible(false);
                    nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e" + players(template) + " players"));
                    nameTag.setCustomNameVisible(true);
                    nameTag.setGravity(false);
                    nameTag.setMetadata("custom-tag", new FixedMetadataValue(javaPlugin, true));
                    nameTag.setMetadata("updating-players", new FixedMetadataValue(javaPlugin, true));
                    nameTag.setMetadata("template", new FixedMetadataValue(javaPlugin, template));
                    NBTUtils.defaultNPCTags(nameTag);
                    tagMap2.put(npc, nameTag);
                }
            }
        }.runTaskTimer(javaPlugin, 0, 20L);
    }

    private void createGameNPC(String game, String template, Location location, String skinTexture, String skinSignature) {
        NPC npc = this.summon(location,
                "&b&l" + game, "&e" + players(template) + " players",
                template, skinTexture, skinSignature, new NPCInteractEvent() {
                    @Override
                    public void on(Player player) {
                        if(template.equalsIgnoreCase("Nightfall")) {
                            MessageChannel.getInstance().Switch(player, player.getName(), template + "-");
                        } else {
                            new GameServerViewGUI(player, game + " Servers", template);
                        }
                    }
                });
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    this.cancel();
                    ArmorStand nameTag = Bukkit.getWorld("world").spawn(location.clone().add(0, 0.8, 0), ArmorStand.class);
                    nameTag.setSmall(true);
                    nameTag.setBasePlate(false);
                    nameTag.setVisible(false);
                    nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e" + players(template) + " players"));
                    nameTag.setCustomNameVisible(true);
                    nameTag.setGravity(false);
                    nameTag.setMetadata("custom-tag", new FixedMetadataValue(javaPlugin, true));
                    nameTag.setMetadata("updating-players", new FixedMetadataValue(javaPlugin, true));
                    nameTag.setMetadata("template", new FixedMetadataValue(javaPlugin, template));
                    NBTUtils.defaultNPCTags(nameTag);
                    tagMap.put(npc, nameTag);
                }
            }
        }.runTaskTimer(javaPlugin, 0, 20L);
    }

    private NPC summon(Location location, String footer, String header, String template, String skinTexture, String skinSignature, NPCInteractEvent event) {
        NPC customPlayer = new NPC(this.javaPlugin, ChatColor.translateAlternateColorCodes('&', "&b"),
                skinTexture,
                skinSignature,
                location,
                event);
        new BukkitRunnable() {
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    this.cancel();
                    ArmorStand nameTag = Bukkit.getWorld("world").spawn(location.clone().add(0, 1.2, 0), ArmorStand.class);
                    nameTag.setSmall(true);
                    nameTag.setBasePlate(false);
                    nameTag.setVisible(false);
                    nameTag.setCustomName(ChatColor.translateAlternateColorCodes('&', footer));
                    nameTag.setCustomNameVisible(true);
                    nameTag.setGravity(false);
                    nameTag.setMetadata("custom-tag", new FixedMetadataValue(javaPlugin, true));
                    NBTUtils.defaultNPCTags(nameTag);
                }
            }
        }.runTaskTimer(javaPlugin, 0, 20L);
        customPlayers.add(customPlayer);
        return customPlayer;
    }

    @Override
    public void onDisable() {
        for(NPC entry : customPlayers) {
            entry.despawn();
        }
        for(Entity entry : customMobs) {
            entry.remove();
        }
        for(World world : Bukkit.getWorlds()) {
            for(Entity e : world.getEntities()) {
                if(e.hasMetadata("custom-tag")) {
                    e.remove();
                }
            }
        }
        customPlayers.clear();
        HandlerList.unregisterAll(this);
    }
}