package com.buuz135.simpleclaims.systems.tick;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.EmptyItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class QueuedCraftClaimFilterSystem extends EntityTickingSystem<EntityStore> {

    private static final Field CM_X;
    private static final Field CM_Y;
    private static final Field CM_Z;
    private static final Field CM_BLOCKTYPE;
    private static final Field CM_QUEUE;

    private static final Class<?> JOB_CLASS;
    private static final Field JOB_INPUT_CONTAINER;
    private static final Field JOB_WINDOW;
    private static final Field JOB_INPUT_REMOVAL_TYPE;

    static {
        try {
            CM_X = CraftingManager.class.getDeclaredField("x");
            CM_Y = CraftingManager.class.getDeclaredField("y");
            CM_Z = CraftingManager.class.getDeclaredField("z");
            CM_BLOCKTYPE = CraftingManager.class.getDeclaredField("blockType");
            CM_QUEUE = CraftingManager.class.getDeclaredField("queuedCraftingJobs");
            CM_X.setAccessible(true);
            CM_Y.setAccessible(true);
            CM_Z.setAccessible(true);
            CM_BLOCKTYPE.setAccessible(true);
            CM_QUEUE.setAccessible(true);

            JOB_CLASS = Class.forName("com.hypixel.hytale.builtin.crafting.component.CraftingManager$CraftingJob");
            JOB_INPUT_CONTAINER = JOB_CLASS.getDeclaredField("inputItemContainer");
            JOB_WINDOW = JOB_CLASS.getDeclaredField("window");
            JOB_INPUT_REMOVAL_TYPE = JOB_CLASS.getDeclaredField("inputRemovalType");
            JOB_INPUT_CONTAINER.setAccessible(true);
            JOB_WINDOW.setAccessible(true);
            JOB_INPUT_REMOVAL_TYPE.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to init reflection for crafting job filtering", e);
        }
    }

    @Override
    public void tick(float dt, int index,
                     @NonNullDecl ArchetypeChunk<EntityStore> chunk,
                     @NonNullDecl Store<EntityStore> store,
                     @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

        Ref<EntityStore> ref = chunk.getReferenceTo(index);
        if (!ref.isValid()) return;

        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        CraftingManager cm = store.getComponent(ref, CraftingManager.getComponentType());
        if (player == null || playerRef == null || cm == null) return;

        // If no bench is set, nothing to filter (fieldcraft, etc.)
        Object blockType;
        int bx, by, bz;
        try {
            blockType = CM_BLOCKTYPE.get(cm);
            if (blockType == null) return;

            bx = (int) CM_X.get(cm);
            by = (int) CM_Y.get(cm);
            bz = (int) CM_Z.get(cm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final BlockingQueue<?> q;
        try {
            q = (BlockingQueue<?>) CM_QUEUE.get(cm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (q.isEmpty()) return;

        World world = player.getWorld();

        ItemContainer allowedChestCombined = buildAllowedChestCombined(world, playerRef, bx, by, bz);
        ItemContainer inv = player.getInventory().getCombinedBackpackStorageHotbar();
        ItemContainer allowedInput = new CombinedItemContainer(inv, allowedChestCombined);

        for (Object job : q) {
            if (!JOB_CLASS.isInstance(job)) continue;
            try {
                Object windowObj = JOB_WINDOW.get(job);
                if (!(windowObj instanceof com.hypixel.hytale.builtin.crafting.window.SimpleCraftingWindow)) {
                    continue;
                }
                Object removalType = JOB_INPUT_REMOVAL_TYPE.get(job);
                if (removalType != null && !removalType.toString().equals("NORMAL")) {
                    continue;
                }
                JOB_INPUT_CONTAINER.set(job, allowedInput);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to read/replace CraftingJob fields", e);
            }
        }
    }

    private static ItemContainer buildAllowedChestCombined(World world, PlayerRef playerRef, int bx, int by, int bz) {
        var cfg = world.getGameplayConfig().getCraftingConfig();
        int limit = cfg.getBenchMaterialChestLimit();
        int h = cfg.getBenchMaterialHorizontalChestSearchRadius();
        int v = cfg.getBenchMaterialVerticalChestSearchRadius();

        UUID uuid = playerRef.getUuid();
        String worldName = world.getName();

        Set<ItemContainer> unique = Collections.newSetFromMap(new IdentityHashMap<>());
        List<ItemContainer> allowed = new ArrayList<>(limit);

        for (int y = by - v; y <= by + v && allowed.size() < limit; y++) {
            for (int x = bx - h; x <= bx + h && allowed.size() < limit; x++) {
                for (int z = bz - h; z <= bz + h && allowed.size() < limit; z++) {

                    var state = world.getState(x, y, z, true);
                    if (!(state instanceof ItemContainerState chest)) continue;

                    boolean ok = ClaimManager.getInstance().isAllowedToInteract(
                            uuid, worldName, x, z, PartyInfo::isChestInteractEnabled
                    );
                    if (!ok) continue;

                    ItemContainer c = chest.getItemContainer();
                    if (unique.add(c)) allowed.add(c);
                }
            }
        }

        if (allowed.isEmpty()) return EmptyItemContainer.INSTANCE;
        return new CombinedItemContainer(allowed.toArray(ItemContainer[]::new));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}