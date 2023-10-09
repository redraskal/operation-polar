package com.frostedmc.fabrication.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.fabrication.Fabrication;
import com.frostedmc.fabrication.api.SkullResult;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class HeadGUI implements Listener {

    private Player player;
    private SkullResult[] skullResults;
    private Toolbox instance;
    private int page;
    private int maxPage;

    public HeadGUI(Player player, SkullResult[] skullResults, Toolbox instance) {
        this.player = player;
        this.skullResults = skullResults;
        this.instance = instance;
        this.maxPage = 0;

        int current = 0;
        for(int i=0; i<skullResults.length; i++) {
            if(current >= 14) {
                this.maxPage++;
                current = 0;
            } else {
                current++;
            }
        }

        this.setPage(1);
    }

    public void next() {
        this.setPage((this.page+1));
    }

    public void previous() {
        this.setPage((this.page-1));
    }

    private ItemStack[] getItems() {
        int start = (14*(this.page-1));
        List<SkullResult> skullResultList = new ArrayList<SkullResult>();
        List<ItemStack> itemStackList = new ArrayList<ItemStack>();

        if(skullResults.length >= (start+1)) {
            for(int i=start; i<skullResults.length; i++) {
                if(skullResultList.size() < 14) {
                    skullResultList.add(skullResults[i]);
                }
            }
        }

        for(SkullResult skullResult : skullResultList) {
            itemStackList.add(skullResult.create("&e&l" + skullResult.getName()));
        }

        return itemStackList.toArray(new ItemStack[itemStackList.size()]);
    }

    public void setPage(int page) {
        this.page = page;
        player.closeInventory();
        new ChestGUI(player, 45, "Skull Selection » Page " + this.page, false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    if(skullResults.length == 0) {
                        gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.STAINED_GLASS_PANE, 1, 14, "&cNo results found."));
                    } else {
                        ItemStack[] items = getItems();
                        int current = 0;
                        for(int i=10; i<=25; i++) {
                            if(i != 17 && i != 18) {
                                if(current < items.length) {
                                    gui.i.setItem(i, items[current]);
                                }
                                current++;
                            }
                        }
                    }

                    gui.i.setItem(39, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&l« Previous Page"));
                    gui.i.setItem(40, ItemCreator.getInstance().createItem(Material.NETHER_STAR, 1, 0, "&b&lMain Menu"));
                    gui.i.setItem(41, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&a&lNext Page »"));

                    if(page == 1) {
                        gui.i.setItem(39, ItemCreator.getInstance().createItem(Material.ARROW, 0, 0, "&c&l« Previous Page"));
                    }

                    if(page >= maxPage) {
                        gui.i.setItem(41, ItemCreator.getInstance().createItem(Material.ARROW, 0, 0, "&c&lNext Page »"));
                    }
                }

                if(callback == CallbackType.CLICK) {
                    if(item != null
                            && item.getType() != Material.AIR
                            && item.getItemMeta() != null
                            && item.getItemMeta().getDisplayName() != null) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                        if(item.getType() == Material.ARROW) {
                            if(item.getAmount() == 1) {
                                if(d.contains("Next")) {
                                    next();
                                } else {
                                    previous();
                                }
                            }
                        }

                        if(item.getType() == Material.SKULL_ITEM) {
                            player.setItemOnCursor(item);
                        }

                        if(d.equalsIgnoreCase("Main Menu")) {
                            player.closeInventory();
                            player.openInventory(instance.inventory);
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, Fabrication.getInstance());
    }
}