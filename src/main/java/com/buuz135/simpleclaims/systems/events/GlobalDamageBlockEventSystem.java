package com.buuz135.simpleclaims.systems.events;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.system.WorldEventSystem;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * Global event system that catches DamageBlockEvent invocations without entity context.
 * This handles explosions and other non-player block damage.
 * Since we can't trace these back to a player, we block them in all claimed chunks.
 */
public class GlobalDamageBlockEventSystem extends WorldEventSystem<EntityStore, DamageBlockEvent> {

    public GlobalDamageBlockEventSystem() {
        super(DamageBlockEvent.class);
    }

    @Override
    public void handle(@Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull DamageBlockEvent event) {
        var world = store.getExternalData().getWorld();
        if (world == null) return;

        String worldName = world.getName();
        if (worldName == null) return;

        int x = event.getTargetBlock().getX();
        int z = event.getTargetBlock().getZ();

        var blockName = event.getBlockType().getId().toLowerCase(Locale.ROOT);

        for (String blocksThatIgnoreInteractRestriction : Main.CONFIG.get().getBlocksThatIgnoreInteractRestrictions()) {
            if (blockName.contains(blocksThatIgnoreInteractRestriction.toLowerCase(Locale.ROOT))) return;
        }

        // Block all global damage events in claimed chunks
        if (!ClaimManager.getInstance().isAllowedToInteract(null, worldName, x, z, PartyInfo::isBlockBreakEnabled)) {
            event.setCancelled(true);
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Collections.singleton(RootDependency.first());
    }
}
