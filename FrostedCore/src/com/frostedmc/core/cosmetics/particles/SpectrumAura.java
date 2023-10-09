package com.frostedmc.core.cosmetics.particles;

import com.frostedmc.core.cosmetics.Particle;
import com.frostedmc.core.utils.ParticleEffect;
import com.frostedmc.core.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

/**
 * Created by Redraskal_2 on 9/14/2016.
 */
public class SpectrumAura extends Particle {

    private java.util.List<Color> rainbow = Utils.rainbowRGB();
    private int current = 0;
    private double ang = 1D;
    private double yInc = -1D;
    private double size = 1D;

    public SpectrumAura(Player player, JavaPlugin javaPlugin) {
        super(player, 1L, javaPlugin);
    }

    @Override
    public String name() {
        return "Spectrum Aura";
    }

    @Override
    public void update(boolean moving) {
        double tempY = yInc;
        for(int i=0; i<360; i+=360/4) {
            double angle = i * Math.PI / ang;
            double x = size * Math.cos(angle);
            double z = size * Math.sin(angle);
            if((current+1) >= rainbow.size()) {
                current = 0;
            } else {
                current++;
            }
            if(ang >= 180D) {
                ang = 1D;
            } else {
                ang+=1D;
            }
            int rgb = rainbow.get(current).getRGB();
            int[] arr = Utils.separateRGB(rgb);
            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(arr[0], arr[1], arr[2]),
                    player.getLocation().clone().add(x, (1D+tempY), z), 15);
            tempY+=.05D;
        }
        if(yInc >= 1) {
            yInc = -1D;
            size = 1D;
        } else {
            if(yInc >= 0.7) {
                size-=.05D;
            }
            yInc+=.05D;
        }
    }
}
