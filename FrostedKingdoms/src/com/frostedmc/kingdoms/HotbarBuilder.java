package com.frostedmc.kingdoms;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Redraskal_2 on 11/17/2016.
 */
public class HotbarBuilder {

    private Map<Integer, ItemStack> itemStackMap = new HashMap<Integer, ItemStack>();

    public HotbarBuilder add(ItemStack itemStack, int slot) {
        itemStackMap.put(slot, itemStack);
        return this;
    }

    public HotbarBuilder remove(int slot) {
        if(!itemStackMap.containsKey(slot))
            return this;
        itemStackMap.remove(slot);
        return this;
    }

    public boolean contains(int slot) {
        return itemStackMap.containsKey(slot);
    }

    public HotbarBuilder apply(Player player) {
        player.getInventory().clear();
        for(Map.Entry<Integer, ItemStack> entry : itemStackMap.entrySet()) {
            player.getInventory().setItem(entry.getKey(), entry.getValue());
        }
        return this;
    }
}