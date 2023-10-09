package com.frostedmc.core.cosmetics.particles;

import com.frostedmc.core.cosmetics.Particle;
import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public class Frostfield extends Particle {

    private double i = 0;

    public Frostfield(Player player, JavaPlugin javaPlugin) {
        super(player, 1L, javaPlugin);
    }

    @Override
    public String name() {
        return "Frostfield";
    }

    @Override
    public void update(boolean moving) {
        if(moving) {
            ParticleEffect.SNOW_SHOVEL.display(1, 1, 1, 0, 7, location.clone(), 15);
            ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.ICE, (byte) 0), 1, 1, 1, 0, 7, location.clone(), 15);
            return;
        }

        Location spiral_1 = location.clone();
        Location spiral_2 = location.clone();
        double radius_1 = 2.3D;
        double radius_2 = 2.3D;

        for(int step = 0; step < 70; step += 4) {
            double difference = (2 * Math.PI) / 30;
            double angle = (step * difference) + i;
            Vector vector = new Vector(Math.cos(angle) * radius_1, 0,
                    Math.sin(angle) * radius_1);
            Location tempLocation = spiral_1.add(vector);
            ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 1, tempLocation, 15);
            spiral_1.subtract(vector);
            spiral_1.add(0, 0.12d, 0);
            radius_1 -= 0.044f;
        }

        for(int step = 0; step < 70; step += 4) {
            double difference = (2 * Math.PI) / 30;
            double angle = (step * difference) + i + 3.5;
            Vector vector = new Vector(Math.cos(angle) * radius_2, 0,
                    Math.sin(angle) * radius_2);
            Location tempLocation = spiral_2.add(vector);
            ParticleEffect.SNOW_SHOVEL.display(0, 0, 0, 0, 1, tempLocation, 15);
            spiral_2.subtract(vector);
            spiral_2.add(0, 0.12d, 0);
            radius_2 -= 0.044f;
        }

        ParticleEffect.BLOCK_CRACK.display(new ParticleEffect.BlockData(Material.ICE, (byte) 0), 1, 1, 1, 0, 7, location.clone(), 15);

        i += 0.05;
    }
}