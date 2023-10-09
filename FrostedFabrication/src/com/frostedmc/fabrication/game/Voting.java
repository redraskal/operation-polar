package com.frostedmc.fabrication.game;

import com.frostedmc.core.gui.CustomSkull;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.core.utils.ActionBar;
import com.frostedmc.core.utils.Title;
import com.frostedmc.fabrication.Fabrication;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public class Voting implements Listener {

    private Arena[] order;
    private int current;

    private Map<Arena, Integer> votes = new HashMap<Arena, Integer>();
    private Map<Player, Integer> voting = new HashMap<Player, Integer>();

    public Voting() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().clear();
            player.getInventory().setItem(0, ItemCreator.getInstance().createBanner(Material.BANNER, DyeColor.RED, 1, 0, "&e&l☆&6&l☆☆☆☆☆"));
            player.getInventory().setItem(1, ItemCreator.getInstance().createBanner(Material.BANNER, DyeColor.ORANGE, 1, 0, "&e&l☆☆&6&l☆☆☆☆"));
            player.getInventory().setItem(2, ItemCreator.getInstance().createBanner(Material.BANNER, DyeColor.YELLOW, 1, 0, "&e&l☆☆☆&6&l☆☆☆"));
            player.getInventory().setItem(3, ItemCreator.getInstance().createBanner(Material.BANNER, DyeColor.LIME, 1, 0, "&e&l☆☆☆☆&6&l☆☆"));
            player.getInventory().setItem(4, ItemCreator.getInstance().createBanner(Material.BANNER, DyeColor.LIGHT_BLUE, 1, 0, "&e&l☆☆☆☆☆&6&l☆"));
            player.getInventory().setItem(5, ItemCreator.getInstance().createBanner(Material.BANNER, DyeColor.MAGENTA, 1, 0, "&e&l☆☆☆☆☆☆"));
            player.getInventory().setItem(7, ItemCreator.getInstance().createItem(Material.NAME_TAG, 1, 0, "&c&lReport Build"));
        }

        GameStatus.JUDGING = true;
        this.order = this.order();
        this.current = -1;
        this.next();
        Fabrication.getInstance().getServer().getPluginManager().registerEvents(this, Fabrication.getInstance());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        inventoryClickEvent.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        playerInteractEvent.setCancelled(true);
        if(playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR || playerInteractEvent.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(playerInteractEvent.getItem() != null
                    && playerInteractEvent.getItem().getItemMeta().getDisplayName() != null) {
                String d = ChatColor.stripColor(playerInteractEvent.getItem().getItemMeta().getDisplayName());
                Arena arena = order[current];

                int slot = playerInteractEvent.getPlayer().getInventory().getHeldItemSlot();

                if(slot <= 5) {
                    voting.put(playerInteractEvent.getPlayer(), slot);
                    playerInteractEvent.getPlayer().getInventory().setHelmet(playerInteractEvent.getPlayer().getItemInHand());
                    playerInteractEvent.getPlayer().sendMessage(PredefinedMessage.VOTED.registerPlaceholder("%stars%",
                            playerInteractEvent.getPlayer().getInventory().getItemInHand().getItemMeta().getDisplayName()).build());
                }

                if(d.equalsIgnoreCase("Report Build")) {
                    if(playerInteractEvent.getPlayer().getName().equalsIgnoreCase(order[current].getPlayer().getName())) {
                        playerInteractEvent.getPlayer().sendMessage(PredefinedMessage.REPORT_BUILD_SELF.build());
                        return;
                    }
                    playerInteractEvent.getPlayer().sendMessage(PredefinedMessage.REPORT_BUILD.build());
                    if(order[current].explode()) {
                        order[current].getPlayer().kickPlayer("Your build was reported on Fabrication.");
                        GameStatus.VOTING_COUNTDOWN = 8;
                        Title title = new Title("We woo, we woo:", "This build has been compromised by the Report Police!", 0, 3, 1);
                        title.setTitleColor(ChatColor.RED);
                        title.setSubtitleColor(ChatColor.WHITE);
                        title.broadcast();
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.FIREWORK_LARGE_BLAST, 10, 1);
                            player.playSound(player.getLocation(), Sound.EXPLODE, 10, 1);
                        }
                    }
                }
            }
        }
    }

    private int total() {
        int count = 0;
        for(Map.Entry<Player, Integer> entry : voting.entrySet()) {
            count+=entry.getValue();
        }
        return count;
    }

    private Object[] getPlace(int place) {
        Object[] a = this.sort();
        if((place-1) < a.length) {
            return new Object[]{((Map.Entry<Arena, Integer>) a[(place-1)]).getKey(),
                    ((Map.Entry<Arena, Integer>) a[(place-1)]).getValue()};
        } else {
            return new Object[]{null, null};
        }
    }

    private Object[] sort() {
        Set<Map.Entry<Arena, Integer>> temp = votes.entrySet();
        Object[] a = temp.toArray();
        Arrays.sort(a, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<Arena, Integer>) o2).getValue()
                        .compareTo(((Map.Entry<Arena, Integer>) o1).getValue());
            }
        });
        return a;
    }

    public void next() {
        if((current+1) >= order.length && current != -1) {
            Arena firstPlace = (Arena) this.getPlace(1)[0];
            int current = 0;
            for(Object object : this.sort()) {
                Bukkit.broadcastMessage("[Debug] Place #" + (current+1) + ": " +
                        ((Map.Entry<Arena, Integer>) object).getKey().getPlayer().getName() +
                        " - " + ((Map.Entry<Arena, Integer>) object).getValue() + " points");
                current++;
            }
            firstPlace.emote(CustomSkull.getInstance().createSkull("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ=="));
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(random(firstPlace.getSpawnpoint()));
                player.setFlying(true);
                player.getInventory().setHelmet(new ItemStack(Material.AIR));
            }
        } else {
            this.current++;
            Arena arena = order[this.current];
            for(Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(random(arena.getSpawnpoint()));
                player.setFlying(true);
                player.getInventory().setHelmet(new ItemStack(Material.AIR));
            }
            Title title = new Title("", ChatColor.translateAlternateColorCodes('&', "&fBuilt by: &a" + arena.getPlayer().getName()), 1, 1, 1);
            title.broadcast();

            new BukkitRunnable() {
                public void run() {
                    if(GameStatus.VOTING_COUNTDOWN <= 1) {
                        this.cancel();
                        votes.put(order[current], total());
                        voting.clear();
                        GameStatus.VOTING_COUNTDOWN = GameStatus.DEFAULT_VOTING_COUNTDOWN;
                        next();
                    } else {
                        GameStatus.VOTING_COUNTDOWN--;
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            ActionBar.send(player, "&3Remaining » &a"
                                    + "00&7:&a" + getSeconds()); }
                    }
                }
            }.runTaskTimer(Fabrication.getInstance(), 0, 20L);
        }
    }

    public String getSeconds() {
        String sec = "" + GameStatus.VOTING_COUNTDOWN;
        if(GameStatus.VOTING_COUNTDOWN < 10) {
            sec = "0" + sec;
        }
        return sec;
    }

    private Location random(Spawnpoint spawnpoint) {
        return spawnpoint.get().add(new Random().nextInt(20), (7+new Random().nextInt(4)), new Random().nextInt(20));
    }

    private Arena[] order() {
        List<Arena> arenas = new ArrayList<Arena>();
        for(Arena arena : Arena.getArenas()) { arenas.add(arena); }
        Collections.shuffle(arenas);
        return arenas.toArray(new Arena[arenas.size()]);
    }
}