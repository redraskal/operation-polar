package com.frostedmc.core.cosmetics.particles;

import com.frostedmc.core.cosmetics.Particle;
import com.frostedmc.core.utils.ParticleEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Redraskal_2 on 9/9/2016.
 */
public class TwilightComet extends Particle {

    private float LineNumber = 3.0F;
    private float offset = 0.0F;
    private float radius = 0.3F;
    private float height = 0.01F;
    private boolean direction = true;

    public TwilightComet(Player player, JavaPlugin javaPlugin) {
        super(player, 1L, javaPlugin);
    }

    @Override
    public String name() {
        return "Twilight Comet";
    }

    @Override
    public void update(boolean moving) {
        Location location_1 = location.clone();

        for (int i = 0; i < LineNumber; i++) {
            location_1.add(Math.cos(offset) * radius, offset * height,
                    Math.sin(offset) * radius);
            if(i / 2 == 0) {
                ParticleEffect.SPELL_WITCH.display(0, 0, 0, 0, 1, location_1, 15);
                ParticleEffect.TOWN_AURA.display(0, 0, 0, 0, 1, location_1, 15);
            }
        }

        if(direction) {
            offset += 0.3;

            if (offset >= 50) {
                direction = false;
            }
        } else {
            offset -= 0.3;

            if (offset <= 0) {
                direction = true;
            }
        }
    }
}