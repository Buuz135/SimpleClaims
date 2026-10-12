package com.buuz135.simpleclaims.util;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.party.PartyOverrides;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.block.components.ItemContainerBlock;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class BenchChestCache {
    public static final long DEFAULT_TTL_MS = 3000L;

    private BenchChestCache() {}

    private record Key(UUID playerId, String worldName, int bx, int by, int bz) {}

    private record Entry(long expiresAtMs, List<ItemContainer> chests) {}

    private static final Map<Key, Entry> CACHE = new ConcurrentHashMap<>();

    public static List<ItemContainer> getAllowedChests(World world, PlayerRef playerRef, int bx, int by, int bz) {
        return getAllowedChests(world, playerRef, bx, by, bz, DEFAULT_TTL_MS);
    }

    public static List<ItemContainer> getAllowedChests(World world, PlayerRef playerRef, int bx, int by, int bz, long ttlMs) {
        long now = System.currentTimeMillis();
        Key key = new Key(playerRef.getUuid(), world.getName(), bx, by, bz);
        Entry e = CACHE.get(key);
        if (e != null && now < e.expiresAtMs) return e.chests;

        List<ItemContainer> scanned = scanAllowedChests(world, playerRef, bx, by, bz);
        List<ItemContainer> view = Collections.unmodifiableList(scanned);

        CACHE.put(key, new Entry(now + ttlMs, view));
        return view;
    }

    private static boolean[] computeAllowedColumns(UUID playerId, String worldName, int bx, int bz, int h) {
        int size = 2 * h + 1;
        boolean[] allowed = new boolean[size * size];

        int i = 0;
        for (int x = bx - h; x <= bx + h; x++) {
            for (int z = bz - h; z <= bz + h; z++) {
                allowed[i++] = ClaimManager.getInstance().isAllowedToInteract(playerId, worldName, x, z, PartyInfo::isChestInteractEnabled, PartyOverrides.PARTY_PROTECTION_INTERACT_CHEST);
            }
        }
        return allowed;
    }

    private static List<ItemContainer> scanAllowedChests(World world, PlayerRef playerRef, int bx, int by, int bz) {
        var cfg = world.getGameplayConfig().getCraftingConfig();
        int limit = cfg.getBenchMaterialChestLimit();
        int h = cfg.getBenchMaterialHorizontalChestSearchRadius();
        int v = cfg.getBenchMaterialVerticalChestSearchRadius();

        UUID uuid = playerRef.getUuid();
        String worldName = world.getName();

        int size = 2 * h + 1;
        boolean[] allowedCols = computeAllowedColumns(uuid, worldName, bx, bz, h);

        Set<ItemContainer> unique = Collections.newSetFromMap(new IdentityHashMap<>());
        List<ItemContainer> allowed = new ArrayList<>(limit);

        int x0 = bx - h;
        int z0 = bz - h;

        for (int x = x0; x <= bx + h; x++) {
            int xi = x - x0;
            for (int z = z0; z <= bz + h; z++) {
                int zi = z - z0;

                if (!allowedCols[xi * size + zi]) continue;

                for (int y = by - v; y <= by + v; y++) {
                    var component = WorldBlockUtil.getBlockComponent(world, x, y, z, ItemContainerBlock.getComponentType());
                    if (component == null) continue;

                    ItemContainer c = component.getItemContainer();
                    if (unique.add(c)) {
                        allowed.add(c);
                        if (allowed.size() >= limit) return allowed;
                    }
                }
            }
        }

        return allowed;
    }

    public static void pruneExpired() {
        long now = System.currentTimeMillis();
        CACHE.entrySet().removeIf(e -> now >= e.getValue().expiresAtMs);
    }

    public static void clearPlayer(UUID playerId) {
        CACHE.keySet().removeIf(k -> k.playerId.equals(playerId));
    }
}
