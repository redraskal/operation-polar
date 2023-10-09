package com.frostedmc.core.utils;

import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

/**
 * Created by Redraskal_2 on 3/5/2017.
 */
public class NBTUtils {

    public static void setNoDespawn(Entity entity, boolean toggle) {
        NBTTagCompound nbtTagCompound = construct(entity);
        nbtTagCompound.set("PersistenceRequired", new NBTTagByte((byte) convert(toggle)));
        apply(entity, nbtTagCompound);
    }

    public static void setAI(Entity entity, boolean toggle) {
        NBTTagCompound nbtTagCompound = construct(entity);
        nbtTagCompound.setInt("NoAI", convert(!toggle));
        apply(entity, nbtTagCompound);
    }

    public static void setSilent(Entity entity, boolean toggle) {
        NBTTagCompound nbtTagCompound = construct(entity);
        nbtTagCompound.setInt("Silent", convert(toggle));
        apply(entity, nbtTagCompound);
    }

    public static void defaultNPCTags(Entity entity) {
        NBTUtils.setNoDespawn(entity, true);
        if(!(entity instanceof ArmorStand)) {
            NBTUtils.setAI(entity, false);
            NBTUtils.setSilent(entity, true);
        }
    }

    public static NBTTagCompound construct(Entity entity) {
        Object nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound tag = ((net.minecraft.server.v1_8_R3.Entity)nmsEntity).getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        ((net.minecraft.server.v1_8_R3.Entity)nmsEntity).c(tag);
        return tag;
    }

    public static void apply(Entity entity, NBTTagCompound nbtTagCompound) {
        Object nmsEntity = ((CraftEntity) entity).getHandle();
        ((net.minecraft.server.v1_8_R3.Entity)nmsEntity).f(nbtTagCompound);
    }

    public static int convert(boolean toggle) {
        if(toggle) return 1;
        return 0;
    }
}