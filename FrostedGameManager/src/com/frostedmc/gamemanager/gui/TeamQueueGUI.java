package com.frostedmc.gamemanager.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.gamemanager.GameManager;
import com.frostedmc.gamemanager.api.Team;
import com.frostedmc.gamemanager.manager.TeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public class TeamQueueGUI {

    private Map<String, Team> teams = new HashMap<>();
    private Map<Integer, Team> teamMap = new HashMap<>();
    private Map<Integer, ItemStack> itemStackMap = new HashMap<>();

    public TeamQueueGUI(Player player, TeamManager teamManager) {
        int slot = 0;
        for(Team team : teamManager.getTeams()) {
            String displayName;
            if(team.getTeamColor() != null) {
                displayName = ChatColor.translateAlternateColorCodes('&',
                        team.getTeamColor().toString() + "&l" + team.getName());
            } else {
                displayName = ChatColor.translateAlternateColorCodes('&',
                        "&f&l" + team.getName());
            }
            teams.put(displayName, team);
            itemStackMap.put(slot, ItemCreator.getInstance().createItem(team.getIcon().getItemType(),
                    1, team.getIcon().getData(), displayName, Arrays.asList(new String[]{
                            "&ePlayers » &f" + team.getPlayers().size(),
                            "&6In Queue » &f" + teamManager.getPlayersInQueue(team),
                            " &a&lJoin the queue"
                    })));
            teamMap.put(slot, team);
            slot++;
        }

        int guiSize = 9;

        while(teamManager.getTeams().size() > guiSize) {
            guiSize+=9;
        }

        new ChestGUI(player, guiSize, "Team Selection", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    for(Map.Entry<Integer, ItemStack> entry : itemStackMap.entrySet()) {
                        gui.i.setItem(entry.getKey(), entry.getValue());
                    }
                }
                if(callback == CallbackType.CLICK) {
                    if(item.getItemMeta().hasDisplayName()) {
                        if(teams.containsKey(item.getItemMeta().getDisplayName())) {
                            player.closeInventory();
                            teamManager.queuePlayer(player, teams.get(item.getItemMeta().getDisplayName()));
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {
                for(Map.Entry<Integer, Team> entry : teamMap.entrySet()) {
                    ItemStack itemStack = itemStackMap.get(entry.getKey());
                    ItemMeta itemMeta = itemStack.getItemMeta();

                    itemMeta.setLore(ItemCreator.getInstance().colorizeList(Arrays.asList(new String[]{
                            "&ePlayers » &f" + entry.getValue().getPlayers().size(),
                            "&6In Queue » &f" + teamManager.getPlayersInQueue(entry.getValue()),
                            " &a&lJoin the queue"
                    })));
                    itemStack.setItemMeta(itemMeta);

                    gui.i.setItem(entry.getKey(), itemStack);
                }
            }
        }, GameManager.getInstance());
    }
}