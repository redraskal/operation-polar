package com.frostedmc.nightfall.callback;

import org.bukkit.World;

/**
 * Copyright (c) Redraskal 2017.
 * <p>
 * Please do not copy the code below unless you
 * have permission to do so from me.
 */
public abstract class FortressWorldLoadCallback {

    public abstract void progress(String info);

    public abstract void done(World world);

    public abstract void error(String message);
}