package com.buuz135.simpleclaims.systems.tick;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EntryTickingSystem extends EntityTickingSystem<EntityStore> {

    private static final Message TELEPORT_MESSAGE = CommandMessages.ENTRY_DENIED;
    private final Map<UUID, List<Transform>> playerLastSafePositions;

    public EntryTickingSystem() {
        this.playerLastSafePositions = new ConcurrentHashMap<>();
    }

    @Override
    public void tick(float v, int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        if (!ref.isValid()) return;
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());

        var chunkInfo = ClaimManager.getInstance().getChunkRawCoords(
                player.getWorld().getName(),
                (int) Math.floor(playerRef.getTransform().getPosition().getX()),
                (int) Math.floor(playerRef.getTransform().getPosition().getZ())
        );

        if (chunkInfo != null) {
            if (!ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), (int) playerRef.getTransform().getPosition().getX(), (int) playerRef.getTransform().getPosition().getZ(), PartyInfo::isAllowEntryEnabled, "")) {
                if (playerLastSafePositions.containsKey(playerRef.getUuid())) {
                    var safePositions = playerLastSafePositions.get(playerRef.getUuid());
                    if (safePositions.size() > 2) {
                        var lastSafePosition = safePositions.get(0); // Get the oldest one
                        player.getWorld().execute(() -> {
                            var lastSafePos = lastSafePosition.getPosition();


                            store.putComponent(ref, Teleport.getComponentType(), new Teleport(player.getWorld(),
                                    new Vector3d(lastSafePos.getX(), lastSafePos.getY(), lastSafePos.getZ()),
                                    lastSafePosition.getRotation()));
                            playerRef.sendMessage(TELEPORT_MESSAGE);
                        });
                        return;
                    }
                }
            }
        }
        var safePositions = playerLastSafePositions.computeIfAbsent(playerRef.getUuid(), uuid -> new LinkedList<>());
        var currentTransform = playerRef.getTransform();
        if (safePositions.isEmpty()) {
            safePositions.add(currentTransform.clone());
        } else {
            var lastPos = safePositions.getLast().getPosition();
            var currentPos = currentTransform.getPosition();
            double dx = lastPos.getX() - currentPos.getX();
            double dy = lastPos.getY() - currentPos.getY();
            double dz = lastPos.getZ() - currentPos.getZ();
            if (Math.sqrt(dx * dx + dy * dy + dz * dz) > 1.5) {
                safePositions.add(currentTransform.clone());
                if (safePositions.size() > 3) {
                    safePositions.remove(0);
                }
            }
        }
    }


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
