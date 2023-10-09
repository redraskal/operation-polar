package com.frostedmc.frostedgames.game.chest;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public enum ChestType {

    TIER_1 {
        @Override
        public int[] getChance() {
            return new int[]{95, 100};
        }

        @Override
        public Multimap<Object, Object> allItems(){
            Multimap<Object, Object> items = ArrayListMultimap.create();

            items.put(new ItemStack(Material.STONE_SWORD), Integer.valueOf(25));
            items.put(new ItemStack(Material.LEATHER_HELMET), Integer.valueOf(27));
            items.put(new ItemStack(Material.LEATHER_CHESTPLATE), Integer.valueOf(25));
            items.put(new ItemStack(Material.LEATHER_LEGGINGS), Integer.valueOf(29));
            items.put(new ItemStack(Material.LEATHER_BOOTS), Integer.valueOf(30));
            items.put(new ItemStack(Material.CHAINMAIL_CHESTPLATE), Integer.valueOf(30));
            items.put(new ItemStack(Material.CHAINMAIL_LEGGINGS), Integer.valueOf(35));
            items.put(new ItemStack(Material.IRON_INGOT, (new Random()).nextInt(1) + 1), Integer.valueOf(35));
            items.put(new ItemStack(Material.GOLDEN_CARROT, (new Random()).nextInt(4) + 2), Integer.valueOf(40));
            items.put(new ItemStack(Material.FLINT_AND_STEEL), Integer.valueOf(45));
            items.put(new ItemStack(Material.BOW), Integer.valueOf(45));
            items.put(new ItemStack(Material.STICK, (new Random()).nextInt(20) + 7), Integer.valueOf(45));
            items.put(new ItemStack(Material.ARROW, (new Random()).nextInt(16) + 16), Integer.valueOf(45));
            items.put(new ItemStack(Material.IRON_INGOT, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.COOKED_BEEF, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.COOKED_BEEF, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.COOKED_BEEF, (new Random()).nextInt(2) + 4), Integer.valueOf(35));

            return items;
        }

        @Override
        public int[] maxItems() {
            return new int[]{16, 20};
        }
    }, TIER_2 {
        @Override
        public int[] getChance() {
            return new int[]{95, 100};
        }

        @Override
        public Multimap<Object, Object> allItems(){
            Multimap<Object, Object> items = ArrayListMultimap.create();

            items.put(new ItemStack(Material.DIAMOND_BOOTS), Integer.valueOf(10));
            items.put(new ItemStack(Material.DIAMOND_HELMET), Integer.valueOf(2));
            items.put(new ItemStack(Material.IRON_SWORD), Integer.valueOf(25));
            items.put(new ItemStack(Material.IRON_HELMET), Integer.valueOf(27));
            items.put(new ItemStack(Material.IRON_CHESTPLATE), Integer.valueOf(25));
            items.put(new ItemStack(Material.IRON_LEGGINGS), Integer.valueOf(29));
            items.put(new ItemStack(Material.IRON_BOOTS), Integer.valueOf(30));
            items.put(new ItemStack(Material.CHAINMAIL_CHESTPLATE), Integer.valueOf(30));
            items.put(new ItemStack(Material.CHAINMAIL_LEGGINGS), Integer.valueOf(35));
            items.put(new ItemStack(Material.DIAMOND, (new Random()).nextInt(1) + 1), Integer.valueOf(35));
            items.put(new ItemStack(Material.GOLDEN_CARROT, (new Random()).nextInt(4) + 2), Integer.valueOf(40));
            items.put(new ItemStack(Material.GOLDEN_APPLE, (new Random()).nextInt(2) + 1), Integer.valueOf(38));
            items.put(new ItemStack(Material.FLINT_AND_STEEL), Integer.valueOf(45));
            items.put(new ItemStack(Material.BOW), Integer.valueOf(45));
            items.put(new ItemStack(Material.STICK, (new Random()).nextInt(20) + 7), Integer.valueOf(45));
            items.put(new ItemStack(Material.ARROW, (new Random()).nextInt(16) + 16), Integer.valueOf(45));
            items.put(new ItemStack(Material.IRON_INGOT, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.COOKED_BEEF, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.COOKED_BEEF, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.COOKED_BEEF, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.TNT, (new Random()).nextInt(2) + 4), Integer.valueOf(35));
            items.put(new ItemStack(Material.TNT, (new Random()).nextInt(2) + 4), Integer.valueOf(35));

            return items;
        }

        @Override
        public int[] maxItems() {
            return new int[]{16, 20};
        }
    };

    public abstract int[] getChance();
    public abstract Multimap<Object, Object> allItems();
    public abstract int[] maxItems();
}