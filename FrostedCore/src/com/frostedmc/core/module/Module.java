package com.frostedmc.core.module;

/**
 * Created by Redraskal_2 on 8/24/2016.
 */
public abstract class Module {

    public abstract String name();

    public abstract void onEnable();

    public abstract void onDisable();
}
