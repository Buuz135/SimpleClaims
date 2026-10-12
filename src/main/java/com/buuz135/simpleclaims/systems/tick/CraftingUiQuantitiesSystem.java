package com.buuz135.simpleclaims.systems.tick;

import com.buuz135.simpleclaims.util.BenchChestCache;
import com.buuz135.simpleclaims.util.WindowExtraResourcesState;
import com.buuz135.simpleclaims.util.WindowReflection;
import com.buuz135.simpleclaims.util.WorldBlockUtil;

import com.hypixel.hytale.builtin.crafting.component.BenchBlock;
import com.hypixel.hytale.builtin.crafting.component.ProcessingBenchBlock;
import com.hypixel.hytale.builtin.crafting.window.SimpleCraftingWindow;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.ItemQuantity;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialExtraResourcesSection;
import com.hypixel.hytale.server.core.entity.entities.player.windows.Window;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.DelegateItemContainer;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.filter.FilterType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.netty.util.internal.ConcurrentSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.hypixel.hytale.builtin.crafting.CraftingPlugin.isValidCraftingMaterialForBench;
import static com.hypixel.hytale.builtin.crafting.CraftingPlugin.isValidUpgradeMaterialForBench;

public class CraftingUiQuantitiesSystem extends EntityTickingSystem<EntityStore> {

    private final Map<SimpleCraftingWindow, Long> nextAllowedMs = new ConcurrentHashMap<>();
    private final Map<SimpleCraftingWindow, Integer> lastHash = new ConcurrentHashMap<>();
    private final Set<SimpleCraftingWindow> initialized = new ConcurrentSet<>();


    @Override
    public void tick(float dt, int index, ArchetypeChunk<EntityStore> chunk, Store<EntityStore> store, CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        if (!ref.isValid()) return;

        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || playerRef == null) return;

        List<Window> windows = player.getWindowManager().getWindows();
        if (windows.isEmpty()) return;

        World world = player.getWorld();
        long now = System.currentTimeMillis();

        var connection = playerRef.getPacketHandler().getChannel();
        var map = WindowExtraResourcesState.getOrCreateMap(connection);
        if (map == null) return;

        for (Window w : windows) {
            if (!(w instanceof SimpleCraftingWindow scw)) continue;

            boolean firstTime = !initialized.contains(scw);
            if (!firstTime) {
                long allowedAt = nextAllowedMs.getOrDefault(scw, 0L);
                if (now < allowedAt) continue;
                nextAllowedMs.put(scw, now + 500L);
            }

            BenchBlock benchBlock = WindowReflection.getBenchBlock(scw);

            int bx = scw.getX();
            int by = scw.getY();
            int bz = scw.getZ();

            var block = WorldBlockUtil.getBlockType(world, bx, by, bz);
            if (block == null) continue;
            var bench = block.getBench();
            if (bench == null) continue;

            List<ItemContainer> chests = BenchChestCache.getAllowedChests(world, playerRef, bx, by, bz);
            ItemQuantity[] counts = computeCounts(bench, benchBlock.getTierLevel(), chests);
            int hash = fingerprintCounts(counts);

            Integer prev = lastHash.get(scw);
            if (prev != null && prev == hash) continue;

            MaterialExtraResourcesSection section = WindowReflection.getExtraSection(scw);
            section.setItemContainer(buildUiContainer(chests));
            section.setExtraMaterials(counts);
            section.setValid(true);

            map.put(scw.getId(), section.toPacket());
            lastHash.put(scw, hash);
            WindowReflection.invalidate(scw);
            if (initialized.add(scw)) {
                WindowReflection.invalidate(scw);
                // start throttle window after first init
                nextAllowedMs.put(scw, now + 500L);
            }
        }

        nextAllowedMs.keySet().removeIf(win -> !windows.contains(win));
        lastHash.keySet().removeIf(win -> !windows.contains(win));
        initialized.removeIf(win -> !windows.contains(win));
    }

    public static ItemQuantity[] computeCounts(Bench bench, int tierLevel, List<ItemContainer> chests) {
        var materials = new Object2ObjectOpenHashMap<String, ItemQuantity>();

        for (ItemContainer chest : chests) {
            chest.forEach((slot, stack) -> {
                if (stack == null || stack.isEmpty()) return;
                if (isValidUpgradeMaterialForBench(bench, tierLevel, stack) || isValidCraftingMaterialForBench(bench, stack)) {
                    ItemQuantity q = materials.computeIfAbsent(stack.getItemId(), k -> new ItemQuantity(stack.getItemId(), 0));
                    q.quantity += stack.getQuantity();
                }
            });
        }

        return materials.values().toArray(new ItemQuantity[0]);
    }

    public static int fingerprintCounts(ItemQuantity[] counts) {
        int h = 0;
        for (ItemQuantity q : counts) {
            int itemH = q.itemId.hashCode();
            int qtyH = q.quantity;
            h ^= (itemH * 31) ^ qtyH;
        }
        return h;
    }

    public static ItemContainer buildUiContainer(List<ItemContainer> chests) {
        if (chests.isEmpty()) return EmptyItemContainer.INSTANCE;

        ItemContainer[] delegates = chests.stream().map(container -> {
            DelegateItemContainer<ItemContainer> d = new DelegateItemContainer<>(container);
            d.setGlobalFilter(FilterType.ALLOW_OUTPUT_ONLY);
            return (ItemContainer) d;
        }).toArray(ItemContainer[]::new);

        return new CombinedItemContainer(delegates);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
