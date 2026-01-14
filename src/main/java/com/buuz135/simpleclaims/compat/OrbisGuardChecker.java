package com.buuz135.simpleclaims.compat;

import com.hypixel.hytale.math.vector.Vector3i;
import com.orbisguard.OrbisGuardPlugin;
import com.orbisguard.protection.RegionContainer;
import com.orbisguard.protection.managers.ApplicableRegionSet;
import com.orbisguard.protection.managers.RegionManager;
import com.orbisguard.protection.regions.GlobalProtectedRegion;
import com.orbisguard.protection.regions.ProtectedRegion;

import java.util.Collection;

class OrbisGuardChecker {

    static boolean isLocationProtected(String worldName, int x, int y, int z) {
        try {
            OrbisGuardPlugin plugin = OrbisGuardPlugin.getInstance();
            if (plugin == null) {
                return false;
            }

            RegionContainer container = plugin.getRegionContainer();
            if (container == null) {
                return false;
            }

            ApplicableRegionSet regions = container.getApplicableRegions(worldName, x, y, z);
            if (regions == null) {
                return false;
            }

            return regions.hasProtectiveRegions();
        } catch (Exception e) {
            return false;
        }
    }

    static boolean isChunkProtected(String worldName, int chunkX, int chunkZ) {
        try {
            OrbisGuardPlugin plugin = OrbisGuardPlugin.getInstance();
            if (plugin == null) {
                return false;
            }

            RegionContainer container = plugin.getRegionContainer();
            if (container == null) {
                return false;
            }

            RegionManager manager = container.get(worldName);
            if (manager == null) {
                return false;
            }

            Collection<ProtectedRegion> regions = manager.getRegions();
            if (regions == null || regions.isEmpty()) {
                return false;
            }

            int chunkMinX = chunkX << 5;
            int chunkMaxX = chunkMinX + 31;
            int chunkMinZ = chunkZ << 5;
            int chunkMaxZ = chunkMinZ + 31;

            for (ProtectedRegion region : regions) {
                if (region instanceof GlobalProtectedRegion) {
                    continue;
                }

                Vector3i min = region.getMinimumPoint();
                Vector3i max = region.getMaximumPoint();

                boolean intersects = max.x >= chunkMinX && min.x <= chunkMaxX &&
                    max.z >= chunkMinZ && min.z <= chunkMaxZ;

                if (intersects) {
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
