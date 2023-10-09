package com.frostedmc.core.cosmetics.particles;

import com.frostedmc.core.cosmetics.Particle;
import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public class SeaVortex extends Particle {

    private float step = 2.0F;

    public SeaVortex(Player player, JavaPlugin javaPlugin) {
        super(player, 1L, javaPlugin);
    }

    @Override
    public String name() {
        return "Sea Vortex";
    }

    @Override
    public void update(boolean moving) {
        for (float k = -3.7F; k < -2.0F; k += 0.3F) {
            Location location = this.location.clone();
            Vector v = new Vector(k * Math.sin(k * this.step) / 3.0D, -k - 2.3D, k * Math.cos(k * this.step) / 3.0D);
            location.add(v);
            ParticleEffect.CRIT_MAGIC.display(0, 0, 0, 0, 1, location, 15);
        }

        this.step += 0.1F;
        if (this.step >= 10.0F) {
            this.step = -10.0F;
        }
    }
}
