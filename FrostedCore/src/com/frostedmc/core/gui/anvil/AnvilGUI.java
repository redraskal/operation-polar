package com.frostedmc.core.gui.anvil;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutOpenWindow;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class AnvilGUI implements Listener {

    private Player player;
    private Inventory inventory;
    private ItemStack itemStack;
    private AnvilEvent anvilEvent;
    private JavaPlugin javaPlugin;

    public AnvilGUI(Player player, ItemStack itemStack, AnvilEvent anvilEvent, JavaPlugin javaPlugin) {
        this.player = player;
        this.itemStack = itemStack;
        this.anvilEvent = anvilEvent;
        this.javaPlugin = javaPlugin;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        FakeAnvil fakeAnvil = new FakeAnvil(entityPlayer);
        int containerId = entityPlayer.nextContainerCounter();
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("Repairing", new Object[]{}), 0));

        entityPlayer.activeContainer = fakeAnvil;
        entityPlayer.activeContainer.windowId = containerId;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        entityPlayer.activeContainer = fakeAnvil;
        entityPlayer.activeContainer.windowId = containerId;

        this.inventory = fakeAnvil.getBukkitView().getTopInventory();
        this.inventory.setItem(0, this.itemStack);
        this.javaPlugin.getServer().getPluginManager().registerEvents(this, this.javaPlugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        HumanEntity entity = inventoryClickEvent.getWhoClicked();

        if(entity instanceof Player) {
            Player player = (Player) entity;
            Inventory tempInventory = inventoryClickEvent.getInventory();

            if(!player.getName().equalsIgnoreCase(this.player.getName())) {
                return;
            }

            if(tempInventory instanceof AnvilInventory) {
                InventoryView inventoryView = inventoryClickEvent.getView();
                int rawSlot = inventoryClickEvent.getRawSlot();

                if(rawSlot == inventoryView.convertSlot(rawSlot)) {
                    if(rawSlot == 2) {
                        ItemStack item = inventoryClickEvent.getCurrentItem();

                        if(item != null
                                && item.getItemMeta() != null) {
                            if(item.getItemMeta().getDisplayName() != null) {
                                inventoryClickEvent.setCancelled(true);
                                tempInventory.clear();
                                player.closeInventory();
                                this.anvilEvent.onItemClick(player, item.getItemMeta().getDisplayName());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        if(inventoryCloseEvent.getPlayer().getName().equalsIgnoreCase(this.player.getName())) {
            this.inventory.clear();
            HandlerList.unregisterAll(this);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent playerQuitEvent) {
        if(playerQuitEvent.getPlayer().getName().equalsIgnoreCase(this.player.getName())) {
            this.inventory.clear();
            HandlerList.unregisterAll(this);
        }
    }
}