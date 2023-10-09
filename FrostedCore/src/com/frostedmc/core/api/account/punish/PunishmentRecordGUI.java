package com.frostedmc.core.api.account.punish;

import com.frostedmc.core.Core;
import com.frostedmc.core.commands.defaults.PunishCommand;
import com.frostedmc.core.gui.ItemCreator;
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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PunishmentRecordGUI implements Listener {

	public Player p;
    public JavaPlugin javaPlugin;
    public Inventory i;
	public String r;
	public int pA;
	public boolean o;
	public boolean g;
	public String rE;
	public PunishmentRecord[] pR;
	
	public PunishmentRecordGUI(Player player, String record, UUID uuid, int page, boolean gui, JavaPlugin javaPlugin) {
		this.p = player;
        this.javaPlugin = javaPlugin;
		this.r = record;
		this.o = true;
		this.g = gui;
		this.pR = Core.getInstance().getPunishmentManager().parsePunishmentRecords(uuid);
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
				
				if(d.equalsIgnoreCase("< Back (Right-Click)")) {
					if(event.getCurrentItem().getAmount() == 1) {
						setPage((this.pA - 1));
					}
				}
				
				if(d.equalsIgnoreCase("Next > (Right-Click)")) {
					if(event.getCurrentItem().getAmount() == 1) {
						setPage((this.pA + 1));
					}
				}
				
				if(d.equalsIgnoreCase("Back to main menu")) {
					p.closeInventory();
					
					List<String> a = new ArrayList<String>();
					a.add(r);
					
					for(String r : rE.split(" ")) {
						a.add(r);
					}
					
					new PunishCommand(javaPlugin).onCommand(p, a.toArray(new String[a.size()]));
				}
				
				event.setCancelled(true);
			} catch (Exception e) {}
		}
	}
	
	public void setPage(int page) {
		this.pA = page;
		this.i = Bukkit.createInventory(null, 27, r + "'s Record | Page " + page);
		
		for(int ii=18; ii<27; ii++) {
			i.setItem(ii, new ItemStack(Material.AIR));
		}
		
		int start = (18*(page-1));
		
		for(int ii=0; ii<18; ii++) {
			if(this.pR.length >= ((ii+start)+1)) {
				PunishmentRecord record = this.pR[(ii+start)];
				
				if(record.getPunishmentType() == PunishmentType.WARN) {
					i.setItem(ii, ItemCreator.getInstance().createItem(Material.PAPER, 1, 0, "&e&lWarning by "
                            + Core.getInstance().getAccountManager().parseDetails(record.getPunisher()).getUsername(), Arrays.asList(new String[]{
                            "&6Reason » &7&o" + record.getReason(), "&b",
                            "&7&o" + record.getIssued().toString()
                    })));
				}
				
				if(record.getPunishmentType() == PunishmentType.PERM_BAN) {
					i.setItem(ii, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 1, "&e&lPermanent ban by "
                            + Core.getInstance().getAccountManager().parseDetails(record.getPunisher()).getUsername(), Arrays.asList(new String[]{
                            "&6Reason » &7&o" + record.getReason(), "&b",
                            "&7&o" + record.getIssued().toString()
                    })));
				}
				
				if(record.getPunishmentType() == PunishmentType.PERM_MUTE) {
					i.setItem(ii, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 1, "&e&lPermanent mute by "
                            + Core.getInstance().getAccountManager().parseDetails(record.getPunisher()).getUsername(), Arrays.asList(new String[]{
                            "&6Reason » &7&o" + record.getReason(), "&b",
                            "&7&o" + record.getIssued().toString()
                    })));
				}
				
				if(record.getPunishmentType() == PunishmentType.TEMP_BAN) {
					i.setItem(ii, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, getInkSack(record.getPunishmentSeverity()), "&e&lTemp ban by "
                            + Core.getInstance().getAccountManager().parseDetails(record.getPunisher()).getUsername(), Arrays.asList(new String[]{
                            "&6Reason » &7&o" + record.getReason(),
                            "&6Lasts until » &7&o" + record.getEnd().toString(),
                            "&b", "&7&o" + record.getIssued().toString()
                    })));
				}
				
				if(record.getPunishmentType() == PunishmentType.TEMP_MUTE) {
					i.setItem(ii, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, getInkSack(record.getPunishmentSeverity()), "&e&lTemp mute by "
                            + Core.getInstance().getAccountManager().parseDetails(record.getPunisher()).getUsername(), Arrays.asList(new String[]{
                            "&6Reason » &7&o" + record.getReason(),
                            "&6Lasts until » &7&o" + record.getEnd().toString(),
                            "&b", "&7&o" + record.getIssued().toString()
                    })));
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
		
		int players = this.pR.length;
		
		if(players < (18*page)) {
			next = 0;
			nextC = "&c&l";
		}
		
		i.setItem(21, ItemCreator.getInstance().createItem(Material.ARROW, back, 0, backC + "< Back &7(Right-Click)"));
		
		if(g == true) {
			i.setItem(22, ItemCreator.getInstance().createItem(Material.BARRIER, 1, 0, "&c&lBack to Main Menu"));
		}
		
		i.setItem(23, ItemCreator.getInstance().createItem(Material.ARROW, next, 0, nextC + "Next > &7(Right-Click)"));
		
		this.p.closeInventory();
        javaPlugin.getServer().getPluginManager().registerEvents(this, javaPlugin);
		this.p.openInventory(this.i);
	}

    private int getInkSack(PunishmentSeverity punishmentSeverity) {
        if(punishmentSeverity == PunishmentSeverity.SEVERITY_1) {
            return 3;
        }
        if(punishmentSeverity == PunishmentSeverity.SEVERITY_2) {
            return 4;
        }
        if(punishmentSeverity == PunishmentSeverity.SEVERITY_3) {
            return 11;
        }
        if(punishmentSeverity == PunishmentSeverity.SEVERITY_4) {
            return 14;
        }
        if(punishmentSeverity == PunishmentSeverity.SEVERITY_5) {
            return 1;
        }
        return 1;
    }
}
