package com.buuz135.simpleclaims.systems.tick;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TitleTickingSystem extends EntityTickingSystem<EntityStore> {

    private final Message wildernessMessage;
    private final String wildernessText;
    private final Message simpleClaimsMessage;
    private final Map<UUID, String> playerLastTitle;

    public TitleTickingSystem(String topLine, String wildernessName) {
        this.playerLastTitle = new ConcurrentHashMap<>();
        this.simpleClaimsMessage = Message.raw(topLine);
        this.wildernessText = wildernessName;
        this.wildernessMessage = Message.raw(this.wildernessText).color(Color.GREEN);
    }

    @Override
    public void tick(float v, int index, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Player player = store.getComponent(ref, Player.getComponentType());
        if (playerRef == null || player == null) return;
        if (player.getWorld() == null) return;
        if (Arrays.stream(Main.CONFIG.get().getWorldNameBlacklistForClaiming()).anyMatch(s -> s.equals(player.getWorld().getName())))
            return;

        Message titleMessage = this.wildernessMessage;
        String titleText = this.wildernessText;

        var chunkInfo = ClaimManager.getInstance().getChunkRawCoords(
                player.getWorld().getName(),
                (int) Math.floor(playerRef.getTransform().getPosition().getX()),
                (int) Math.floor(playerRef.getTransform().getPosition().getZ())
        );

        if (chunkInfo != null) {
            var party = ClaimManager.getInstance().getPartyById(chunkInfo.getPartyOwner());
            if (party != null) {
                titleText = party.getName();
                titleMessage = Message.raw(titleText).color(Color.WHITE);
            }
        }

        String previousTitle = playerLastTitle.get(playerRef.getUuid());
        if (!titleText.equals(previousTitle)) {
            playerLastTitle.put(playerRef.getUuid(), titleText);
            EventTitleUtil.showEventTitleToPlayer(playerRef, titleMessage, simpleClaimsMessage, false, null, 2, 0.5f, 0.5f);
        }
    }

    public void removePlayer(UUID playerId) {
        playerLastTitle.remove(playerId);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }
}
