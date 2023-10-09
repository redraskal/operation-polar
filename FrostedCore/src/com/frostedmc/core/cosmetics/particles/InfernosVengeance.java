package com.frostedmc.core.cosmetics.particles;

import com.frostedmc.core.cosmetics.Particle;
import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public class InfernosVengeance extends Particle {

    protected int i;
    private double height = 0.3D;
    private boolean up = false;

    public InfernosVengeance(Player player, JavaPlugin javaPlugin) {
        super(player, 1L, javaPlugin);
    }

    @Override
    public String name() {
        return "Inferno's Vengeance";
    }

    @Override
    public void update(boolean moving) {
        Location location = player.getEyeLocation().clone();
        double angle = 6.283185307179586D * this.i / 70;
        double x = Math.cos(angle) * 1.245F;
        double z = Math.sin(angle) * 1.245F;
        location.add(x, height, z);
        ParticleEffect.FLAME.display(0, 0, 0, 0, 1, location, 15);
        ParticleEffect.DRIP_LAVA.display(0, 0, 0, 0, 1, location, 15);

        if(i % 5 == 0) {
            ParticleEffect.LAVA.display(0, 0, 0, 0, 1, location, 15);
        }

        location.subtract(x, 0.0D, z);

        i += 3;
        if (height < -1.49D) {
            up = true;
        } else if (height > 0.6D) {
            up = false;
        }
        if (up) {
            height += 0.05D;
        } else {
            height -= 0.05D;
        }
    }
}
