package com.buuz135.simpleclaims.systems.events;

import com.buuz135.simpleclaims.Main;
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
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
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
        var blockName = event.getBlockType().getId().toLowerCase(Locale.ROOT);
        var ignored = false;

        for (String blocksThatIgnoreInteractRestriction : Main.CONFIG.get().getBlocksThatIgnoreInteractRestrictions()) {
            if (blockName.contains(blocksThatIgnoreInteractRestriction.toLowerCase(Locale.ROOT))) ignored = true;
        }

        if (blockName.contains("chest")) defaultInteract = PartyInfo::isChestInteractEnabled;
        else if (blockName.contains("bench") && !blockName.contains("furniture"))
            defaultInteract = PartyInfo::isBenchInteractEnabled;
        else if (blockName.contains("door")) defaultInteract = PartyInfo::isDoorInteractEnabled;
        else if (blockName.contains("chair") || blockName.contains("stool") || (blockName.contains("bench") && blockName.contains("furniture")))
            defaultInteract = PartyInfo::isChairInteractEnabled;
        else if (blockName.contains("portal") || blockName.contains("teleporter"))
            defaultInteract = PartyInfo::isPortalInteractEnabled;
        if (!ignored && (playerRef != null && !ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), event.getTargetBlock().getX(), event.getTargetBlock().getZ(), defaultInteract))) {
            event.setCancelled(true);
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
