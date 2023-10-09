package com.frostedmc.core.gui.anvil;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.ContainerAnvil;
import net.minecraft.server.v1_8_R3.EntityHuman;

/**
 * Created by Redraskal_2 on 11/1/2016.
 */
public class FakeAnvil extends ContainerAnvil {

    public FakeAnvil(EntityHuman entityHuman) {
        super(entityHuman.inventory, entityHuman.world, new BlockPosition(0, 0, 0), entityHuman);
    }

    @Override
    public boolean a(EntityHuman entityHuman) {
        return true;
    }
}
