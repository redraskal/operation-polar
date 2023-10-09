package com.frostedmc.gamemanager.listener;

import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.GameStatus;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Redraskal_2 on 2/25/2017.
 */
public class Block implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() == Action.PHYSICAL) return;
        if(event.getItem() != null) {
            if(event.getItem().getType().toString().contains("SWORD")
                    || event.getItem().getType() == Material.BOW
                    || event.getItem().getType() == Material.FISHING_ROD) {
                return;
            }
        }
        if(GameManager.getInstance().gameStatus != GameStatus.INGAME) {
            event.setCancelled(true);
        } else {
            if(!GameManager.getInstance().getCurrentGame().gameFlags.allowInteract) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        boolean empty = true;
        for(ItemStack itemStack : event.getInventory().getContents()) {
            if(itemStack != null && itemStack.getType() != Material.AIR) {
                empty = false;
                break;
            }
        }
        if(empty) {
            if(event.getInventory().getHolder() instanceof Chest) {
                Chest chest = (Chest) event.getInventory().getHolder();
                chest.getBlock().setType(Material.AIR);
                chest.getLocation().getWorld().playSound(chest.getLocation(), Sound.DIG_WOOD, 10, 1);
                ParticleEffect.BLOCK_CRACK.display(
                        new ParticleEffect.BlockData(Material.CHEST, (byte) 0), 1, 1, 1, 0, 30,
                        chest.getLocation().clone().add(0, 0.3, 0), 60);
            }
        }
    }
}
