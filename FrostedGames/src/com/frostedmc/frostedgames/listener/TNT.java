package com.frostedmc.frostedgames.listener;

import com.frostedmc.core.utils.Utils;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.MathUtils;
import com.frostedmc.frostedgames.game.InternalGameSettings;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TNT implements Listener {

    @EventHandler
	public void onTNTUse(PlayerInteractEvent event) {
		try {
			if(event.getAction() == Action.RIGHT_CLICK_AIR && event.getItem().getType() == Material.TNT || event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem().getType() == Material.TNT) {
				if(event.getPlayer().getInventory().getItemInHand().getAmount() == 1) {
					event.getPlayer().getInventory().setItem(event.getPlayer().getInventory().getHeldItemSlot(), new ItemStack(Material.AIR, 1));
				} else {
					event.getPlayer().getInventory().getItemInHand().setAmount(event.getPlayer().getInventory().getItemInHand().getAmount() - 1);
				}
				
				event.setCancelled(true);
				
				TNTPrimed tnt = event.getPlayer().getWorld().spawn(event.getPlayer().getLocation().add(0, 2, 0), TNTPrimed.class);
		        tnt.setFuseTicks(60);
		        tnt.setVelocity(event.getPlayer().getLocation().getDirection().multiply(0.854321));
		        tnt.setMetadata("fakeTNT", new FixedMetadataValue(FrostedGames.getInstance(), true));
		        
		        if(ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Super TNT")) {
		        	tnt.setMetadata("superTNT", new FixedMetadataValue(FrostedGames.getInstance(), true));
		        }
			}
		} catch (Exception e) {}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	  public void onBlockChangeState(EntityChangeBlockEvent event)
	  {
	    if (event.getEntity().hasMetadata("customExplosion"))
	    {
	      event.setCancelled(true);
	      FallingBlock fb = (FallingBlock) event.getEntity();
	      fb.getWorld().spigot().playEffect(fb.getLocation(), Effect.STEP_SOUND, fb.getBlockId(), fb.getBlockData(), 0.0F, 0.0F, 0.0F, 0.0F, 1, 32);
	      event.getEntity().remove();
	    }
	  }

	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setYield(0);
        Map<org.bukkit.block.Block, Object[]> finalList = new HashMap<org.bukkit.block.Block, Object[]>();
        if(event.getEntity() instanceof Fireball) {
            for(org.bukkit.block.Block block : Utils.getBlocksInRadius(event.getEntity().getLocation(), 4, false)) {
                if(block.getType().isSolid()) {
                    if(!block.isLiquid()) {
                        FallingBlock fallingBlock = block.getWorld()
                                .spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                        fallingBlock.setDropItem(false);
                        fallingBlock.setMetadata("customExplosion", new FixedMetadataValue(FrostedGames.getInstance(), true));
                        double dX = (Math.random()*2);
                        double dY = (Math.random()*3);
                        double dZ = (Math.random()*2);
                        fallingBlock.setVelocity(new Vector(dX, dY, dZ));
                        finalList.put(block, new Object[]{block.getType(), block.getData()});
                        block.setType(Material.AIR);
                    }
                }
            }
        } else {
            for(org.bukkit.block.Block block : event.blockList()) {
                if(block.getType().isSolid()) {
                    if(!block.isLiquid()) {
                        FallingBlock fallingBlock = block.getWorld()
                                .spawnFallingBlock(block.getLocation(), block.getType(), block.getData());
                        fallingBlock.setDropItem(false);
                        fallingBlock.setMetadata("customExplosion", new FixedMetadataValue(FrostedGames.getInstance(), true));
                        double dX = (Math.random()*2);
                        double dY = (Math.random()*3);
                        double dZ = (Math.random()*2);
                        fallingBlock.setVelocity(new Vector(dX, dY, dZ));
                        finalList.put(block, new Object[]{block.getType(), block.getData()});
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        for (Entity ent : event.getEntity().getNearbyEntities(5, 5, 5)) {
            if (ent instanceof Player) {
                if(InternalGameSettings.districtMap.containsKey(((Player) ent))) {
                    double dX = event.getEntity().getLocation().getX() - ent.getLocation().getX();
                    double dY = event.getEntity().getLocation().getY() - ent.getLocation().getY();
                    double dZ = event.getEntity().getLocation().getZ() - ent.getLocation().getZ();
                    double yaw = Math.atan2(dZ, dX);
                    double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
                    double X = Math.sin(pitch) * Math.cos(yaw);
                    double Y = Math.sin(pitch) * Math.sin(yaw);
                    double Z = Math.cos(pitch);
                    Vector vector = new Vector(X, Z, Y);
                    MathUtils.applyVelocity(ent, vector.multiply(1.3D).add(new Vector(0, 1.4D, 0)));
                }
            }
        }

        for(Map.Entry<org.bukkit.block.Block, Object[]> block : finalList.entrySet()) {
            new BukkitRunnable() {
                public void run() {
                    block.getKey().setType((Material) block.getValue()[0]);
                    block.getKey().setData((Byte) block.getValue()[1]);
                }
            }.runTaskLater(FrostedGames.getInstance(), 120+(2*new Random().nextInt(6)));
        }
    }
}