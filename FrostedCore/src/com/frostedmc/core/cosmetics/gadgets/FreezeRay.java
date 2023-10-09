package com.frostedmc.core.cosmetics.gadgets;

import com.frostedmc.core.cosmetics.Gadget;
import com.frostedmc.core.gui.ItemCreator;
import com.frostedmc.core.messages.PredefinedMessage;
import com.frostedmc.core.utils.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

/**
 * Created by Redraskal_2 on 9/8/2016.
 */
public class FreezeRay extends Gadget {

    private int cooldownDelay = 10;
    private long startTime;

    public FreezeRay(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public String name() {
        return "Firework";
    }

    @Override
    public ItemStack item() {
        return ItemCreator.getInstance().createItem(Material.FIREWORK, 1, 0, "&b&lFirework", Utils.convert(new String[]{
                "&eIt's pretty simple,",
                "&eright-click and watch them explode :D",
                "&b",
                " &a&lLeft-Click to Remove",
                " &a&lRight-Click to Use",
        }));
    }

    @Override
    public long delay() {
        return 1;
    }

    @Override
    public void onUpdate(Player player) {
        double seconds = Utils.convertToSeconds(startTime);
        if(seconds > cooldownDelay) {
            this.disableUpdater();
        }
    }

    @Override
    public void onGadgetUse(Player player) {
        this.startTime = System.currentTimeMillis();
        this.randomFirework(player.getLocation());
    }

    private void randomFirework(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
                .withColor(Color.fromRGB(new Random().nextInt(255), new Random().nextInt(255), new Random().nextInt(255)))
                .withFade(Color.WHITE)
                .flicker(true)
                .trail(true)
                .build());
        firework.setFireworkMeta(meta);
    }

    @Override
    public void onGadgetUseWhileEnabled(Player player) {
        double seconds = Utils.convertToSeconds(this.startTime);
        if(seconds < this.cooldownDelay) {
            player.sendMessage(PredefinedMessage.COSMETICS_COOLDOWN
                    .registerPlaceholder("%seconds%", Utils.secondsLeft(cooldownDelay, seconds)).build());
        }
    }

    @Override
    public void onEnable(Player player) {}

    @Override
    public void onDisable(Player player) {}
}