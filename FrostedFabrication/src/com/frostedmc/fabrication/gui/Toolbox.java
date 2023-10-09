package com.frostedmc.fabrication.gui;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.fabrication.Fabrication;
import com.frostedmc.fabrication.game.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Created by Redraskal_2 on 10/23/2016.
 */
public class Toolbox implements Listener {

    public Inventory inventory;
    private Player player;
    private Arena arena;

    public Toolbox(Player player, Arena arena) {
        this.player = player;
        this.arena = arena;
        this.inventory = Bukkit.createInventory(null, 27, "Building Toolbox");
        this.inventory.setItem(5, ItemCreator.getInstance().createItem(Material.STAINED_CLAY, 1, 1, "&b&lSet Floor", Arrays.asList(new String[]{
                "&ePlace a block here to replace",
                "&ethe floor with ease.",
                "&b",
                " &a&lPlace-Block"
        })));
        this.inventory.setItem(3, ItemCreator.getInstance().createItem(Material.SKULL_ITEM, 1, 3, "&b&lSkull Database", Arrays.asList(new String[]{
                "&eFind the perfect skulls",
                "&efor your builds with this",
                "&eamazing database!",
                "&b",
                " &a&lLeft-Click"
        })));

        player.getInventory().setItem(8, ItemCreator.getInstance().createItem(Material.NETHER_STAR, 1, 0, "&b&lBuilding Toolbox", Arrays.asList(new String[]{
                "&eAccess many build tools in here",
                "&eto enhance the experience!",
                "&b",
                " &a&lRight-Click"
        })));
        Fabrication.getInstance().getServer().getPluginManager().registerEvents(this, Fabrication.getInstance());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        if(inventoryClickEvent.getWhoClicked().getName().equalsIgnoreCase(player.getName())) {
            if(inventoryClickEvent.getCurrentItem() == null
                    || inventoryClickEvent.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            if(inventoryClickEvent.getClickedInventory().getTitle() != null
                    && inventoryClickEvent.getClickedInventory().getTitle().equalsIgnoreCase("Building Toolbox")) {
                inventoryClickEvent.setCancelled(true);
            } else {
                return;
            }

            if(inventoryClickEvent.getCurrentItem() != null
                    && inventoryClickEvent.getCurrentItem().getType() != Material.AIR
                    && inventoryClickEvent.getCurrentItem().getItemMeta() != null
                    && inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName() != null) {
                String d = ChatColor.stripColor(inventoryClickEvent.getCurrentItem().getItemMeta().getDisplayName());

                if(d.equalsIgnoreCase("Skull Database")) {
                    new HeadQueryGUI((Player) inventoryClickEvent.getWhoClicked(), this);
                }
            }

            if(inventoryClickEvent.getSlot() == 5
                    && inventoryClickEvent.getCursor() != null
                    && inventoryClickEvent.getCursor().getType() != Material.AIR
                    && inventoryClickEvent.getCursor().getType().isBlock()
                    && inventoryClickEvent.getCursor().getType() != Material.SAPLING
                    && inventoryClickEvent.getCursor().getType() != Material.TRAP_DOOR
                    && inventoryClickEvent.getCursor().getType() != Material.WATER_LILY
                    && inventoryClickEvent.getCursor().getType() != Material.CACTUS
                    && inventoryClickEvent.getCursor().getType() != Material.SNOW
                    && inventoryClickEvent.getCursor().getType() != Material.DEAD_BUSH
                    || inventoryClickEvent.getCursor().getType() == Material.WATER_BUCKET
                    || inventoryClickEvent.getCursor().getType() == Material.LAVA_BUCKET) {
                for(Block b : arena.getFloor()) {
                    if(inventoryClickEvent.getCursor().getType() == Material.WATER_BUCKET
                            || inventoryClickEvent.getCursor().getType() == Material.LAVA_BUCKET) {
                        if(inventoryClickEvent.getCursor().getType() == Material.WATER_BUCKET) {
                            b.setType(Material.STATIONARY_WATER);
                        } else {
                            b.setType(Material.STATIONARY_LAVA);
                        }
                    } else {
                        b.setType(inventoryClickEvent.getCursor().getType());
                        b.setData(inventoryClickEvent.getCursor().getData().getData());
                    }
                }
                inventoryClickEvent.setCursor(new ItemStack(Material.AIR));
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        if(playerInteractEvent.getPlayer().getName().equalsIgnoreCase(player.getName())
                && playerInteractEvent.getItem() != null
                && playerInteractEvent.getItem().getItemMeta().getDisplayName() != null) {
            String d = ChatColor.stripColor(playerInteractEvent.getItem().getItemMeta().getDisplayName());

            if(d.equalsIgnoreCase("Building Toolbox")) {
                playerInteractEvent.setCancelled(true);
                player.openInventory(inventory);
            }
        }
    }
}