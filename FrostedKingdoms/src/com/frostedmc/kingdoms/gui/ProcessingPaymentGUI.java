package com.frostedmc.kingdoms.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 11/19/2016.
 */
public class ProcessingPaymentGUI {

    public ProcessingPaymentGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 27, "Processing payment...", false, new GUICallback() {
            int start = 7;

            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    this.update(gui);
                }
            }

            private void update(ChestGUI gui) {
                if(start >= 16)
                    start = 7;
                start++;
                int count = 0;
                for(int i=10; i<=16; i++) {
                    if(i < 17 && i > 9) {
                        if(can(i) && count < 3) {
                            gui.i.setItem(i, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 12, "&b"));
                            count++;
                        } else {
                            gui.i.setItem(i, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&b"));
                        }
                    }
                }
            }

            private boolean can(int i) {
                if(i == start
                        || i == (start+1)
                        || i == (start+2))
                    return true;
                return false;
            }

            @Override
            public void onSecond(ChestGUI gui) {
                this.update(gui);
            }
        }, javaPlugin);
    }
}