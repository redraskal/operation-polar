package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.CustomSkull;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/8/2016.
 */
public class CosmeticsMainMenuGUI {

    public CosmeticsMainMenuGUI(Player player, JavaPlugin javaPlugin) {
        new ChestGUI(player, 45, "Cosmetics | Main Menu", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.CHEST, 1, 0, "&b&lGadgets", Utils.convert(new String[]{
                            "&eArm yourself with multiple gadgets",
                            "&eand gizmos to become the most fabulous",
                            "&eplayer on the network!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));

                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 4, "&b&lParticle Effects", Utils.convert(new String[]{
                            "&eEquip a magical aura around you,",
                            "&eradiating from the inside of yourself,",
                            "&eseen on any server!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));

                    gui.i.setItem(20, ItemCreator.getInstance().createItem(Material.BONE, 1, 0, "&b&lPets", Utils.convert(new String[]{
                            "&eSummon the perfect creature for you,",
                            "&eadding just that little bit of happiness",
                            "&eto your day!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));

                    gui.i.setItem(24, CustomSkull.getInstance().create("&b&lHats", CustomSkull.getInstance().SLIME_TEXTURES, Utils.convert(new String[]{
                            "&eSummon the perfect creature for you,",
                            "&eadding just that little bit of happiness",
                            "&eto your day!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));

                    gui.i.setItem(30, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 1, "&b&lDeath Effects", Utils.convert(new String[]{
                            "&eConquer your enemies with these heroic",
                            "&edeath animations, spicing up that blood bath!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));

                    gui.i.setItem(31, CustomSkull.getInstance().create("&b&lWin Effects", CustomSkull.getInstance().COIN_TEXTURES, Utils.convert(new String[]{
                            "&eEver wanted that little bit of extravagant",
                            "&eanimations to your win? We can spice it up",
                            "&ea bit from the classic firework effect!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));

                    gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&b&lArrow Trails", Utils.convert(new String[]{
                            "&eThrow your enemies off guard with these",
                            "&ebeautiful arrow trails, flying in the sky!",
                            "&b",
                            " &a&lRight-Click",
                            "&7&o0/10 cosmetics unlocked",
                    })));
                }

                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                        if(!d.isEmpty()) {
                            //if(d.equalsIgnoreCase("gadgets")) {
                                //player.closeInventory();
                                //new CosmeticsGadgetsGUI(player, javaPlugin);
                            //}

                            if(d.equalsIgnoreCase("particle effects")) {
                                player.closeInventory();
                                new CosmeticsParticleGUI(player, javaPlugin);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }
}