package com.frostedmc.arenapvp.arena;

import com.frostedmc.arenapvp.api.CustomLocation;

/**
 * Created by Redraskal_2 on 12/22/2016.
 */
public enum MapInfo {

    THE_GREAT_PLAINS("The Great Plains", "arenapvp_main",
            new CustomLocation(-74, 67, 25, (float) -45.8, (float) -0.8),
            new CustomLocation(6, 67, 103, (float) 133.9, (float) -0.2)),
    CVS_FORTRESS("CV_'s Fortress", "arenapvp_main",
            new CustomLocation(-74, 67, -349, (float) -134.7, (float) -0.3),
            new CustomLocation(6, 67, -427, (float) 45.0, (float) -0.1)),
    DESERT_LANDIA("Desert-Landia", "arenapvp_main",
            new CustomLocation(6, 68, -198, (float) 134.2, (float) 1.0),
            new CustomLocation(-74, 68, -276, (float) -45.9, (float) -0.3)),
    THE_BADLANDS("The Badlands", "arenapvp_main",
            new CustomLocation(6, 69, -47, (float) 134.2, (float) 1.0),
            new CustomLocation(-74, 69, -125, (float) -45.9, (float) -0.3)),
    CRYOGEN("Cryogen", "arenapvp_gavo",
            new CustomLocation(2360, 24, 934, (float) 128, (float) -5),
            new CustomLocation(2318, 24, 897, (float) -38.8, (float) -2.6));

    private String name;
    private String folder;
    private CustomLocation spawnpointA;
    private CustomLocation spawnpointB;

    private MapInfo(String name, String folder,
                    CustomLocation spawnpointA, CustomLocation spawnpointB) {
        this.name = name;
        this.folder = folder;
        this.spawnpointA = spawnpointA;
        this.spawnpointB = spawnpointB;
    }

    public String getName() {
        return this.name;
    }

    public String getFolder() { return this.folder; }

    public CustomLocation getSpawnpointA() {
        return this.spawnpointA;
    }

    public CustomLocation getSpawnpointB() {
        return this.spawnpointB;
    }
}