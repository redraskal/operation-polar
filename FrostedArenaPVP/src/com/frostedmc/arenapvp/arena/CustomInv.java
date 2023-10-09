package com.frostedmc.arenapvp.arena;

import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public class CustomInv {

    private java.util.Map<Integer, ItemStack> inventory = new HashMap<Integer, ItemStack>();

    public CustomInv() {}

    public CustomInv add(ItemStack itemStack, int slot) {
        inventory.put(slot, itemStack);
        return this;
    }

    public java.util.Map<Integer, ItemStack> build() {
        return inventory;
    }
}