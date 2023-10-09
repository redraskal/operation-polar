package com.frostedmc.frostedgames.game.effect;

import com.frostedmc.core.gui.CustomSkull;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.frostedgames.FrostedGames;
import com.frostedmc.frostedgames.game.CustomDamage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;
import java.util.UUID;

/**
 * Created by Redraskal_2 on 2/1/2017.
 */
public class OwenApocalypse implements Listener {

    private ArmorStand armorStand;
    private Random random = new Random();
    private Vector targetDirection = new Vector(1, 0, 0);
    private Location currentLocation, targetLocation;
    public double noMoveTime = 0, movementSpeed = 0.4d;
    public int ticks = 0;
    public int gseconds = 5;

    public OwenApocalypse(Player target) {
        armorStand = target.getWorld().spawn(target.getLocation(), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setBasePlate(false);
        armorStand.setGravity(false);
        armorStand.setSmall(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setHelmet(CustomSkull.getInstance().create("Skull",
                "eyJ0aW1lc3RhbXAiOjE0ODYwMDAwNTIwMDgsInByb2ZpbGVJZCI6IjU0MjVlMzExMWI3MDRjZDNhM2E0MWVlNjhlYjExZWFlIiwicHJvZmlsZU5hbWUiOiJPd2VuXyIsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8yY2UwMWQ1YjI5OTcxNmVhNmYxYzRiNjc5NmVkNDMyYTJhODlkOWQyOTkyZWRhZmI3ZDE1OTFlZjg3NjhmYjUifX19"));
        armorStand.setCustomName("Owen #" + UUID.randomUUID().toString().substring(0, 4));
        armorStand.setCustomNameVisible(true);
        armorStand.setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
        armorStand.setItemInHand(new ItemStack(Material.DIAMOND_SWORD));

        ParticleEffect.SMOKE_NORMAL.display(1, 1, 1, 0, 5, armorStand.getLocation().clone().add(0, 2, 0), 15);
        ParticleEffect.CLOUD.display(1, 1, 1, 0, 5, armorStand.getLocation().clone().add(0, 2, 0), 15);
        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0, 7, armorStand.getLocation().clone().add(0, 2, 0), 15);

        currentLocation = armorStand.getLocation();
        targetLocation = generateNewTarget(target);

        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BAT_TAKEOFF, .3f, 1f);
        FrostedGames.getInstance().getServer().getPluginManager().registerEvents(this, FrostedGames.getInstance());

        new BukkitRunnable() {
            int ticks = 0;
            public void run() {
                if(ticks >= (10*20)) {
                    this.cancel();
                    disable();
                } else {
                    update(target);
                }
                if(ticks % 20 == 0) {
                    if(target.getLocation().distance(armorStand.getLocation()) <= 4.0) {
                        if((target.getHealth()-1) <= 0) {
                            CustomDamage.handleDeath(target, null, "Owen Apocalypse");
                        } else {
                            target.damage(1);
                        }
                    }
                }
                ticks++;
            }
        }.runTaskTimer(FrostedGames.getInstance(), 0, 1L);
    }

    private void disable() {
        ParticleEffect.SMOKE_NORMAL.display(1, 1, 1, 0, 5, armorStand.getLocation().clone().add(0, 2, 0), 15);
        ParticleEffect.CLOUD.display(1, 1, 1, 0, 5, armorStand.getLocation().clone().add(0, 2, 0), 15);
        ParticleEffect.FLAME.display(1, 1, 1, 0, 7, armorStand.getLocation().clone().add(0, 2, 0), 15);
        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.FIZZ, .3f, 1f);
        armorStand.remove();
        HandlerList.unregisterAll(this);
    }

    public void update(Player player) {
        if (player.getWorld() != currentLocation.getWorld()
                || player.getWorld() != targetLocation.getWorld()) {
            currentLocation = player.getLocation();
            targetLocation = generateNewTarget(player);
        }

        double distanceBtw = player.getEyeLocation().distance(currentLocation);
        double distTarget = currentLocation.distance(targetLocation);

        if (distTarget < 1d || distanceBtw > 3)
            targetLocation = generateNewTarget(player);

        distTarget = currentLocation.distance(targetLocation);

        if (random.nextDouble() > 0.98)
            noMoveTime = System.currentTimeMillis() + randomDouble(0, 2000);

        if (player.getEyeLocation().distance(currentLocation) < 3)
            movementSpeed = noMoveTime > System.currentTimeMillis() ? Math.max(0, movementSpeed - 0.0075)
                    : Math.min(0.1, movementSpeed + 0.0075);
        else {
            noMoveTime = 0;
            movementSpeed = Math.min(0.15 + distanceBtw * 0.05, movementSpeed + 0.02);
        }

        targetDirection.add(targetLocation.toVector().subtract(currentLocation.toVector()).multiply(0.2));

        if (targetDirection.length() < 1)
            movementSpeed = targetDirection.length() * movementSpeed;

        targetDirection = targetDirection.normalize();

        if (distTarget > 0.1)
            currentLocation.add(targetDirection.clone().multiply(movementSpeed));

        Vector dirBetweenLocations = armorStand.getLocation().toVector().subtract(player.getLocation().toVector());
        dirBetweenLocations.multiply(-1);
        currentLocation.setDirection(dirBetweenLocations);

        armorStand.teleport(currentLocation);

        int seconds = (ticks / 20);

        if(seconds >= gseconds) {
            ticks = 0;
            gseconds = randomRangeInt(3, 15);

            if(random.nextBoolean()) {
                armorStand.getWorld().playSound(armorStand.getLocation(), Sound.ENDERMAN_IDLE, .2f, 1f);
            } else {
                armorStand.getWorld().playSound(armorStand.getLocation(), Sound.ENDERDRAGON_GROWL, .2f, 1f);
            }
        } else {
            ticks++;
        }
    }

    private Location generateNewTarget(Player player) {
        Location loc = player.getEyeLocation()
                .add(Math.random() * 6 - 3,
                        (Math.random() * 1.5 / 2),
                        Math.random() * 6 - 3);

        if((armorStand.getLocation().getY() - player.getLocation().getY()) >= 1) {
            loc.setY((loc.getY() - 1));
        }

        return loc;
    }

    public double randomDouble(double min, double max) {
        return Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min);
    }

    public int randomRangeInt(int min, int max) {
        return (int) (Math.random() < 0.5 ? ((1 - Math.random()) * (max - min) + min) : (Math.random() * (max - min) + min));
    }

    @EventHandler
    public void onEntityManipulate(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().getUniqueId().equals(armorStand.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity().getUniqueId().equals(armorStand.getUniqueId())) {
            event.setCancelled(true);
        }
    }
}