package com.buuz135.simpleclaims.systems.events;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.party.PartyOverrides;
import com.buuz135.simpleclaims.systems.tick.CraftingUiQuantitiesSystem;
import com.buuz135.simpleclaims.util.BenchChestCache;
import com.buuz135.simpleclaims.util.WindowExtraResourcesState;
import com.buuz135.simpleclaims.util.WorldBlockUtil;
import com.hypixel.hytale.builtin.crafting.component.BenchBlock;
import com.hypixel.hytale.builtin.crafting.component.ProcessingBenchBlock;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.protocol.ExtraResources;
import com.hypixel.hytale.protocol.ItemQuantity;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.bench.Bench;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.windows.MaterialExtraResourcesSection;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;


public class InteractEventSystem extends EntityEventSystem<EntityStore, UseBlockEvent.Pre> {

    public InteractEventSystem() {
        super(UseBlockEvent.Pre.class);
    }

    @Override
    public void handle(final int index, @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull final Store<EntityStore> store, @Nonnull final CommandBuffer<EntityStore> commandBuffer, @Nonnull final UseBlockEvent.Pre event) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Predicate<PartyInfo> defaultInteract = PartyInfo::isBlockInteractEnabled;
        String permission = PartyOverrides.PARTY_PROTECTION_INTERACT;
        var blockName = event.getBlockType().getId().toLowerCase(Locale.ROOT);
        var ignored = false;

        for (String blocksThatIgnoreInteractRestriction : Main.CONFIG.get().getBlocksThatIgnoreInteractRestrictions()) {
            if (blockName.contains(blocksThatIgnoreInteractRestriction.toLowerCase(Locale.ROOT))) ignored = true;
        }

        if (blockName.contains("chest")) {
            defaultInteract = PartyInfo::isChestInteractEnabled;
            permission = PartyOverrides.PARTY_PROTECTION_INTERACT_CHEST;
        } else if (blockName.contains("bench") && !blockName.contains("furniture")) {
            defaultInteract = PartyInfo::isBenchInteractEnabled;
            permission = PartyOverrides.PARTY_PROTECTION_INTERACT_BENCH;

            if (playerRef != null && !ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), event.getTargetBlock().x(), event.getTargetBlock().z(), defaultInteract, permission)) {
                event.setCancelled(true);
                var connection = playerRef.getPacketHandler().getChannel();
                var ch = WindowExtraResourcesState.getNettyChannel(connection);
                if (ch != null) ch.attr(WindowExtraResourcesState.NEXT_OPEN_EXTRA).set(null);
                return;
            }

            PacketHandler ph = playerRef.getPacketHandler();
            var connection = ph.getChannel();
            var ch = WindowExtraResourcesState.getNettyChannel(connection);
            World world = player.getWorld();

            var targetBlock = event.getTargetBlock();
            ExtraResources next = buildExtraResourcesForBench(world, playerRef, targetBlock.x(), targetBlock.y(), targetBlock.z());
            if (next != null && ch != null) {
                ch.attr(WindowExtraResourcesState.NEXT_OPEN_EXTRA).set(next);
                WindowExtraResourcesState.getOrCreateBenchSet(connection).add(0); // provisional id
            }
            return;
        } else if (blockName.contains("door")) {
            defaultInteract = PartyInfo::isDoorInteractEnabled;
            permission = PartyOverrides.PARTY_PROTECTION_INTERACT_DOOR;
        } else if (blockName.contains("chair") || blockName.contains("stool") || (blockName.contains("bench") && blockName.contains("furniture"))) {
            defaultInteract = PartyInfo::isChairInteractEnabled;
            permission = PartyOverrides.PARTY_PROTECTION_INTERACT_CHAIR;
        } else if (blockName.contains("portal") || blockName.contains("teleporter")) {
            defaultInteract = PartyInfo::isPortalInteractEnabled;
            permission = PartyOverrides.PARTY_PROTECTION_INTERACT_PORTAL;
        }
        if (!ignored && (playerRef != null && !ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), event.getTargetBlock().x(), event.getTargetBlock().z(), defaultInteract, permission))) {
            event.setCancelled(true);
        }
    }

    private static ExtraResources buildExtraResourcesForBench(World world, PlayerRef playerRef, int bx, int by, int bz) {
        var chests = BenchChestCache.getAllowedChests(world, playerRef, bx, by, bz);

        var block = WorldBlockUtil.getBlockType(world, bx, by, bz);
        if (block == null) return null;
        var bench = block.getBench();
        if (bench == null) return null;
        var benchBlock = WorldBlockUtil.getBlockComponent(world, bx, by, bz, BenchBlock.getComponentType());
        if (benchBlock == null) return null;

        ItemQuantity[] counts = CraftingUiQuantitiesSystem.computeCounts(bench, benchBlock.getTierLevel(), chests);
        ItemContainer uiContainer = CraftingUiQuantitiesSystem.buildUiContainer(chests);

        MaterialExtraResourcesSection section = new MaterialExtraResourcesSection();
        section.setItemContainer(uiContainer);
        section.setExtraMaterials(counts);
        section.setValid(true);
        return section.toPacket();
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
