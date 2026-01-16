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
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.Transform;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

public class CustomDamageEventSystem extends DamageEventSystem {

    @Override
    public void handle(int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());
        Vector3d transform = store.getComponent(ref, TransformComponent.getComponentType()).getPosition();
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef != null && damage.getSource() instanceof Damage.EntitySource damageEntitySource) {
            Ref<EntityStore> attackerRef = damageEntitySource.getRef();
            if (attackerRef.isValid()) {
                Player attackerPlayerComponent = (Player) commandBuffer.getComponent(attackerRef, Player.getComponentType());
                if (attackerPlayerComponent != null) { //The source is a player
                    // && !ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), (int) transform.getX(), (int) transform.getZ(), PartyInfo::isPVPEnabled)) {
                    var chunk = ClaimManager.getInstance().getChunkRawCoords(player.getWorld().getName(), (int) transform.getX(), (int) transform.getZ());
                    if (chunk == null) return;
                    var partyInfo = ClaimManager.getInstance().getPartyById(chunk.getPartyOwner());
                    if (partyInfo != null && !partyInfo.isPVPEnabled()) {
                        damage.setCancelled(true);
                    }
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
