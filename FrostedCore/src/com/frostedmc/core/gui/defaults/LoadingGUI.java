package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/1/2016.
 */
public class LoadingGUI {

    public LoadingGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "Loading...", false, new GUICallback() {
            int currentSlot = 28;

            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(29, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                    gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                    gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                    gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                    gui.i.setItem(33, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));

                    gui.i.setItem(13, ItemCreator.getInstance().createItem(Material.SIGN, 1, 0, "&b&lLoading available servers...", Utils.convert(new String[]{
                            "&eWe are fetching all the",
                            "&eHub servers for you..."
                    })));
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {
                if(currentSlot > 33) {
                    currentSlot = 29;
                } else {
                    currentSlot++;
                }

                gui.i.setItem(29, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                gui.i.setItem(33, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));

                if(currentSlot != 34) {
                    gui.i.setItem(currentSlot, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 12, "&b"));
                }

                if((currentSlot - 1) != 28) {
                    gui.i.setItem((currentSlot - 1), ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 6, "&b"));
                }
            }
        }, javaPlugin);
    }
}