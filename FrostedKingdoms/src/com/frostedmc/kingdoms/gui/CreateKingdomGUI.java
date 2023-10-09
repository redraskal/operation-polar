package com.frostedmc.kingdoms.gui;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 11/19/2016.
 */
public class CreateKingdomGUI {

    public CreateKingdomGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(12, null);
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }
}
