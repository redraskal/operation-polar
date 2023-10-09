package com.frostedmc.arenapvp.arena;

import com.frostedmc.arenapvp.ArmorUtil;
import com.frostedmc.arenapvp.PotionUtil;
import com.frostedmc.core.gui.ItemCreator;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionType;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public enum Kit {

    KOHI("Kohi", Material.DIAMOND_SWORD, new ItemStack[]{
            ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_SWORD, 1), Enchantment.DAMAGE_ALL, 1),
                    Enchantment.DURABILITY, 3),
            new ItemStack(Material.ENDER_PEARL, 16),
            new ItemStack(Material.COOKED_BEEF, 64),
            PotionUtil.create(PotionType.INSTANT_HEAL, true, 2),
            PotionUtil.create(PotionType.INSTANT_HEAL, true, 2),
            PotionUtil.create(PotionType.INSTANT_HEAL, true, 2),
            PotionUtil.create(PotionType.INSTANT_HEAL, true, 2),
            PotionUtil.create(PotionType.INSTANT_HEAL, true, 2),
            PotionUtil.create(PotionType.SPEED, false),
    }, new ItemStack[]{
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
    }, new CustomInv()
            .add(PotionUtil.create(PotionType.WEAKNESS, true), 9)
            .add(PotionUtil.create(PotionType.POISON, true), 10)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 11)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 12)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 13)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 14)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 15)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 16)
            .add(PotionUtil.create(PotionType.SPEED, false), 17)
            .add(PotionUtil.create(PotionType.WEAKNESS, true), 18)
            .add(PotionUtil.create(PotionType.POISON, true), 19)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 20)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 21)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 22)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 23)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 24)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 25)
            .add(PotionUtil.create(PotionType.SPEED, false), 26)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 27)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 28)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 29)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 30)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 31)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 32)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 33)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true, 2), 34)
            .add(PotionUtil.create(PotionType.SPEED, false), 35)
            .build()),
    COMBO("Combo", Material.DIAMOND_CHESTPLATE, new ItemStack[]{
            ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_SWORD, 1), Enchantment.DAMAGE_ALL, 2),
                    Enchantment.FIRE_ASPECT, 2), Enchantment.DURABILITY, 10),
            new ItemStack(Material.GOLDEN_APPLE, 64, (byte) 1),
            PotionUtil.create(PotionType.SPEED, false, 2),
            PotionUtil.create(PotionType.SPEED, false, 2),
            PotionUtil.create(PotionType.SPEED, false, 2),
            PotionUtil.create(PotionType.SPEED, false, 2),
    }, new ItemStack[]{
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10),
    }, new CustomInv()
            .add(ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10), 9)
            .add(ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10), 10)
            .add(ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10), 11)
            .add(ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 4), Enchantment.DURABILITY, 10), 12)
            .build()),
    DEBUFFLESS("Debuffless", Material.POTION, new ItemStack[]{
            ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_SWORD, 1), Enchantment.DAMAGE_ALL, 1),
                    Enchantment.DURABILITY, 3), Enchantment.FIRE_ASPECT, 2),
            new ItemStack(Material.ENDER_PEARL, 16),
            new ItemStack(Material.COOKED_BEEF, 64),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.FIRE_RESISTANCE, false),
            PotionUtil.create(PotionType.SPEED, false),
    }, new ItemStack[]{
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.DIAMOND_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 3),
    }, new CustomInv()
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 9)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 10)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 11)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 12)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 13)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 14)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 15)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 16)
            .add(PotionUtil.create(PotionType.SPEED, false), 17)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 18)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 19)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 20)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 21)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 22)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 23)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 24)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 25)
            .add(PotionUtil.create(PotionType.SPEED, false), 26)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 27)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 28)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 29)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 30)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 31)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 32)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 33)
            .add(PotionUtil.create(PotionType.INSTANT_HEAL, true), 34)
            .add(PotionUtil.create(PotionType.SPEED, false), 35)
            .build()),
    ROBINHOOD("Robinhood", Material.BOW, new ItemStack[]{
            ArmorUtil.addEnchantment(new ItemStack(Material.STONE_SWORD, 1), Enchantment.DAMAGE_ALL, 1),
            ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(new ItemStack(Material.BOW, 1), Enchantment.ARROW_INFINITE, 1), Enchantment.ARROW_DAMAGE, 2),
            new ItemStack(Material.ARROW, 1),
            new ItemStack(Material.GOLDEN_APPLE, 25, (byte) 1),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.SPEED, false, 2),
            PotionUtil.create(PotionType.SPEED, false, 2),
    }, new ItemStack[]{
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_HELMET), Color.GREEN),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.GREEN),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.GREEN),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_BOOTS), Color.GREEN),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
    }, new CustomInv().build()),
    NINJA("Ninja", Material.DRAGON_EGG, new ItemStack[]{
            ArmorUtil.addEnchantment(new ItemStack(Material.IRON_SWORD, 1), Enchantment.DAMAGE_ALL, 1),
            new ItemStack(Material.ENDER_PEARL, 16),
            new ItemStack(Material.COOKED_BEEF, 64),
            new ItemStack(Material.GOLDEN_APPLE, 5, (byte) 1),
            ItemCreator.getInstance().createItem(Material.SNOW_BALL, 16, 0, "&eSmoke Grenade"),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
    }, new ItemStack[]{
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_HELMET), Color.BLACK),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.BLACK),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.BLACK),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_BOOTS), Color.BLACK),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 5),
    }, new CustomInv().build()),
    ROCKETEER("Rocketeer", Material.IRON_CHESTPLATE, new ItemStack[]{
            ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(ItemCreator.getInstance().createItem(Material.GOLD_SWORD, 1, 0, "&eRocket Sword &7(Block to fly)"), Enchantment.DAMAGE_ALL, 1),
                    Enchantment.DURABILITY, 50),
            ArmorUtil.addEnchantment(ArmorUtil.addEnchantment(new ItemStack(Material.BOW, 1), Enchantment.ARROW_INFINITE, 1), Enchantment.ARROW_DAMAGE, 2),
            new ItemStack(Material.ARROW, 1),
            new ItemStack(Material.COOKED_BEEF, 64),
            new ItemStack(Material.GOLDEN_APPLE, 10, (byte) 1),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
            PotionUtil.create(PotionType.INSTANT_HEAL, true),
    }, new ItemStack[]{
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_HELMET), Color.YELLOW),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 1),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(new ItemStack(Material.IRON_CHESTPLATE),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 3), Enchantment.DURABILITY, Integer.MAX_VALUE),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.YELLOW),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 1),
            ArmorUtil.addEnchantment(
                    ArmorUtil.addEnchantment(ArmorUtil.setColor(new ItemStack(Material.LEATHER_BOOTS), Color.YELLOW),
                            Enchantment.PROTECTION_ENVIRONMENTAL, 1), Enchantment.DURABILITY, 1),
    }, new CustomInv().build());

    private String name;
    private Material displayItem;
    private ItemStack[] hotbar;
    private ItemStack[] armour;
    private java.util.Map<Integer, ItemStack> inventory;

    private Kit(String name, Material displayItem, ItemStack[] hotbar, ItemStack[] armour, java.util.Map<Integer, ItemStack> inventory) {
        this.name = name;
        this.displayItem = displayItem;
        this.hotbar = hotbar;
        this.armour = armour;
        this.inventory = inventory;
    }

    public String getName() {
        return this.name;
    }

    public Material getDisplayItem() {
        return this.displayItem;
    }

    public ItemStack[] getHotbar() {
        return this.hotbar;
    }

    public ItemStack[] getArmour() {
        return this.armour;
    }

    public java.util.Map<Integer, ItemStack> getInventory() {
        return this.inventory;
    }

    public static Kit fromName(String name) {
        for(Kit kit : Kit.values()) {
            if(kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }
        return null;
    }
}