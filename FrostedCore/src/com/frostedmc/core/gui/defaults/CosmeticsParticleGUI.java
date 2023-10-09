package com.frostedmc.core.gui.defaults;

import com.frostedmc.core.cosmetics.handler.*;
import com.frostedmc.core.gui.ChestGUI;
import com.frostedmc.core.gui.GUICallback;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public class CosmeticsParticleGUI {

    private Player player;
    private JavaPlugin javaPlugin;
    private int page;

    public CosmeticsParticleGUI(Player player, JavaPlugin javaPlugin) {
        this.player = player;
        this.javaPlugin = javaPlugin;
        this.setPage(1);
    }

    public void setPage(int page) {
        this.page = page;

        if(this.page == 1) {
            this.handlePage1();
        }
    }

    private void handlePage1() {
        new ChestGUI(player, 45, "Cosmetics | Particles » Page 1", false, new GUICallback() {
            @Override
            public void callback(ChestGUI gui, CallbackType callback, ItemStack item) {
                if(callback == CallbackType.INIT) {
                    gui.i.setItem(11, ItemCreator.getInstance().createItem(Material.SNOW_BALL, 1, 0, "&b&lFrostfield", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oFree",
                    })));

                    gui.i.setItem(12, ItemCreator.getInstance().createItem(Material.NETHER_BRICK_ITEM, 1, 0, "&b&lTwilight Comet", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oFree",
                    })));

                    gui.i.setItem(13, ItemCreator.getInstance().createItem(Material.LAVA_BUCKET, 1, 0, "&b&lInferno's Vengeance", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oFree",
                    })));

                    gui.i.setItem(14, ItemCreator.getInstance().createItem(Material.PRISMARINE_CRYSTALS, 1, 0, "&b&lSea Vortex", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oFree",
                    })));

                    gui.i.setItem(15, ItemCreator.getInstance().createItem(Material.REDSTONE, 1, 0, "&b&lSpectrum Aura", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oOwned",
                    })));

                    gui.i.setItem(20, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&7&l???", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oNot unlocked",
                    })));

                    gui.i.setItem(21, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&7&l???", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oNot unlocked",
                    })));

                    gui.i.setItem(22, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&7&l???", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oNot unlocked",
                    })));

                    gui.i.setItem(23, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&7&l???", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oNot unlocked",
                    })));

                    gui.i.setItem(24, ItemCreator.getInstance().createItem(Material.INK_SACK, 1, 8, "&7&l???", Utils.convert(new String[]{
                            "&eLore coming soon.",
                            "&b",
                            " &a&lLeft-Click to Equip",
                            "&7&oNot unlocked",
                    })));

                    gui.i.setItem(30, getBannerItem());
                    gui.i.setItem(31, ItemCreator.getInstance().createItem(Material.NETHER_STAR, 1, 0, "&a&lMain Menu"));
                    gui.i.setItem(32, ItemCreator.getInstance().createItem(Material.ARROW, 1, 0, "&b&lNext Page »"));
                }

                if(callback == CallbackType.CLICK) {
                    if(item != null) {
                        String d = ChatColor.stripColor(item.getItemMeta().getDisplayName());

                        if(!d.isEmpty()) {
                            if(d.equalsIgnoreCase("main menu")) {
                                player.closeInventory();
                                new CosmeticsMainMenuGUI(player, javaPlugin);
                            }

                            if(d.equalsIgnoreCase("frostfield")) {
                                new FrostfieldHandler(player, javaPlugin);
                            }

                            if(d.equalsIgnoreCase("twilight comet")) {
                                new TwilightCometHandler(player, javaPlugin);
                            }

                            if(d.equalsIgnoreCase("inferno's vengeance")) {
                                new InfernosVengeanceHandler(player, javaPlugin);
                            }

                            if(d.equalsIgnoreCase("sea vortex")) {
                                new SeaVortexHandler(player, javaPlugin);
                            }

                            if(d.equalsIgnoreCase("spectrum aura")) {
                                new SpectrumAuraHandler(player, javaPlugin);
                            }
                        }
                    }
                }
            }

            @Override
            public void onSecond(ChestGUI gui) {}
        }, javaPlugin);
    }

    private ItemStack getBannerItem() {
        ItemStack banner = ItemCreator.getInstance().createItem(Material.BANNER, 1, 0, "&c&l« Previous Page");
        BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();

        bannerMeta.setBaseColor(DyeColor.RED);
        banner.setItemMeta(bannerMeta);

        return banner;
    }
}