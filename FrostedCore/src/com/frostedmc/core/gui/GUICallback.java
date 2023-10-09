package com.frostedmc.core.gui;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Redraskal_2 on 9/1/2016.
 */
public abstract class GUICallback {

    public static enum CallbackType {
        INIT,
        CLICK,
        CLOSE,
    }

    public abstract void callback(ChestGUI gui, CallbackType callback, ItemStack item);

    public abstract void onSecond(ChestGUI gui);
}