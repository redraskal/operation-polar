package com.frostedmc.gamemanager.gui;

import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.SpectatorMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class SpectateGUI implements Listener {

	public Player p;
    public Inventory i;
    public int pA;
	public boolean o;
	
	public SpectateGUI(Player player, int page) {
		this.p = player;
		this.o = true;
		
		setPage(page);
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer().getName().equalsIgnoreCase(p.getName())) {
			HandlerList.unregisterAll(this);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked().getName().equalsIgnoreCase(p.getName())) {
			try {
				String d = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				
				if(d.equalsIgnoreCase("< Back")) {
					if(event.getCurrentItem().getAmount() == 1) {
						setPage((this.pA - 1));
					}
				}
				
				if(d.equalsIgnoreCase("Next >")) {
					if(event.getCurrentItem().getAmount() == 1) {
						setPage((this.pA + 1));
					}
				}
				
				if(event.getCurrentItem().getType() == Material.SKULL_ITEM) {
					if(event.getCurrentItem().getAmount() == 1) {
						p.closeInventory();
						
						SkullMeta s = (SkullMeta) event.getCurrentItem().getItemMeta();
						p.teleport(Bukkit.getPlayer(s.getOwner()).getLocation().add(0, 3, 0));
						p.setFlying(true);
						
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bGame> &7You are now spectating &e"
                                + s.getOwner() + "&7."));
					}
				}
				
				event.setCancelled(true);
			} catch (Exception e) {}
		}
	}
	
	public void setPage(int page) {
		this.pA = page;
		this.i = Bukkit.createInventory(null, 27, "Spectate | Page " + page);
		
		for(int ii=18; ii<27; ii++) {
			i.setItem(ii, new ItemStack(Material.AIR));
		}
		
		int start = (18*(page-1));
		
		for(int ii=0; ii<18; ii++) {
			if(getPlayers().size() >= ((ii+start)+1)) {
				Player pl = getPlayers().toArray(new Player[getPlayers().size()])[(ii+start)];
				
				if(SpectatorMode.getInstance().contains(pl)) {} else {
					i.setItem(ii, ItemCreator.getInstance().createSkull(1, pl.getName(), "&e&lSpectate &a&l"
                            + pl.getName() + " &7"));
				}
			}
		}
		
		int back = 1;
		int next = 1;
		
		String backC = "&a&l";
		String nextC = "&a&l";
		
		if(page == 1) {
			back = 0;
			backC = "&c&l";
		}
		
		int players = getPlayers().size();
		
		if(players < (18*page)) {
			next = 0;
			nextC = "&c&l";
		}
		
		i.setItem(21, ItemCreator.getInstance().createItem(Material.ARROW, back, 0, backC + "< Back &7"));
		i.setItem(22, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 14, "&b"));
		i.setItem(23, ItemCreator.getInstance().createItem(Material.ARROW, next, 0, nextC + "Next > &7"));
		
		this.p.closeInventory();
		GameManager.getInstance().getServer().getPluginManager().registerEvents(this, GameManager.getInstance());
		this.p.openInventory(this.i);
	}

    public List<Player> getPlayers() {
        List<Player> temp = new ArrayList<Player>();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(!SpectatorMode.getInstance().contains(player)) {
                temp.add(player);
            }
        }
        return temp;
    }
}