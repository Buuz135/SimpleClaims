package com.buuz135.simpleclaims.compat;

import com.hypixel.hytale.math.vector.Vector3d;

public class OrbisGuardCompat {

    private static Boolean orbisGuardAvailable = null;

    public static boolean isOrbisGuardAvailable() {
        if (orbisGuardAvailable == null) {
            try {
                Class.forName("com.orbisguard.OrbisGuardPlugin");
                orbisGuardAvailable = true;
            } catch (ClassNotFoundException e) {
                orbisGuardAvailable = false;
            }
        }
        return orbisGuardAvailable;
    }

    public static boolean isProtected(String worldName, Vector3d position) {
        if (!isOrbisGuardAvailable()) {
            return false;
        }
        return OrbisGuardChecker.isLocationProtected(
            worldName,
            (int) Math.floor(position.x),
            (int) Math.floor(position.y),
            (int) Math.floor(position.z)
        );
    }

    public static boolean isChunkProtected(String worldName, int chunkX, int chunkZ) {
        if (!isOrbisGuardAvailable()) {
            return false;
        }
        return OrbisGuardChecker.isChunkProtected(worldName, chunkX, chunkZ);
    }
}
