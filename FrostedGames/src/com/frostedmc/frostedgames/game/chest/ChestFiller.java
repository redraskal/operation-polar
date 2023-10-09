package com.frostedmc.frostedgames.game.chest;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ChestFiller {

    public static List<Inventory> allChests = new ArrayList<>();

    public static void scanChests() {
        List<Inventory> chests = new ArrayList<>();
        Bukkit.getWorlds().stream().filter(world -> world.getEnvironment()
                .equals(World.Environment.NORMAL)).forEach(world -> {
            for (Chunk chunk : world.getLoadedChunks()) {
                for (BlockState state : chunk.getTileEntities()) {
                    if (state instanceof Chest || state instanceof DoubleChest) {
                        Chest chest = (Chest) state;
                        chests.add(chest.getInventory());
                    }
                }
            }
        });
        fillChests(chests);
    }

    public static void fillChests(List<Inventory> chests) {
        chests.stream().forEach(c -> {
            Random r = new Random();
            int i = r.nextInt(100);
            int i2 = r.nextInt(100);

            ChestType type = ChestType.TIER_2;

            for (ChestType chestType : ChestType.values()) {
                if (i >= chestType.getChance()[0]&& i2 <= chestType.getChance()[1] ) {
                    type = chestType;
                    break;
                }
            }


            fillByType(c, type);
        });
    }

    @SuppressWarnings("unchecked")
    public static void fillByType(Inventory chest, ChestType type) {
        if(!allChests.contains(chest)) {
            chest.clear();

            long itemamount = Arrays
                    .asList(chest.getContents()).stream().filter(items -> items != null
                            && items.getType() != Material.AIR).count();
            long items[] = {itemamount};

            int count[] = {0};
            int min = type.maxItems()[0];
            int max = type.maxItems()[1];
            count[0] = 0;

            if (!type.allItems().isEmpty() && type.allItems() != null) {
                type.allItems().entries().forEach(entry -> {
                    int r = new Random().nextInt(100);
                    int r2 = new Random().nextInt(100);
                    int chance = (int) entry.getValue();
                    ItemStack item = (ItemStack) entry.getKey();
                    if (chest.getSize() > count[0]) {
                        if (r <= chance && r2 >= chance) {

                            if(items[0] < max) {
                                chest.setItem(count[0], item);
                                count[0] += new Random().nextInt(2) + 1;
                                items[0] += 1;
                            }
                        } else {
                            count[0] += new Random().nextInt(2) + 1;
                        }
                    } else {
                        return;
                    }
                });
            }

            count[0] = 0;

            if(chest.getContents().length < min){
                count[0] += new Random().nextInt(2) + 1;
                @SuppressWarnings("rawtypes")
				List<ItemStack> randomized = new ArrayList(type.allItems().keySet());
                Collections.shuffle(randomized);
                for(int i = 0; i <= min; i++){
                    chest.setItem(count[0], (ItemStack) randomized.get(i));
                    count[0] += new Random().nextInt(2) + 1;
                }
            }
            allChests.add(chest);
        }
    }
}