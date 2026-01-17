package com.buuz135.simpleclaims.systems.events;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;

import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.event.events.ecs.DamageBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;


public class BreakBlockEventSystem extends EntityEventSystem<EntityStore, DamageBlockEvent> {

    public BreakBlockEventSystem() {
        super(DamageBlockEvent.class);
    }

    @Override
    public void handle(final int index, @Nonnull final ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull final Store<EntityStore> store, @Nonnull final CommandBuffer<EntityStore> commandBuffer, @Nonnull final DamageBlockEvent event) {
       Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
       Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null) {
            var result = ClaimManager.getInstance().checkInteraction(playerRef.getUuid(), player.getWorld().getName(), event.getTargetBlock().getX(), event.getTargetBlock().getZ(), event.getTargetBlock().getY(), PartyInfo::isBlockBreakEnabled);
            if (!result.isAllowed()) {
                event.setCancelled(true);
                if (result.isBlockedByHeight()) {
                    player.sendMessage(com.buuz135.simpleclaims.commands.CommandMessages.BLOCKED_BY_HEIGHT.param("blockY", result.getBlockY()).param("minHeight", result.getMinHeight()));
                }
            }
       }
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
