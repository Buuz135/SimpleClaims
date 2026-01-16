package com.buuz135.simpleclaims.claim.player_name;

import com.buuz135.simpleclaims.claim.chunk.ChunkInfo;
import com.buuz135.simpleclaims.claim.tracking.ModifiedTracking;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class PlayerNameTracker {

    private HashMap<UUID, PlayerName> names;

    public PlayerNameTracker() {
        this.names = new HashMap<>();
    }

    public PlayerName[] getNames() {
        return names.values().toArray(new PlayerName[0]);
    }

    public void setNames(PlayerName[] names) {
        this.names = new HashMap<>();
        for (PlayerName name : names) {
            this.names.put(name.uuid, name);
        }
    }

    public String getPlayerName(UUID uuid){
        if (names.containsKey(uuid)) return names.get(uuid).name;
        return "Unknown";
    }

    @Nullable
    public UUID getPlayerUUID(String name) {
        for (UUID uuid : names.keySet()) {
            if (names.get(uuid).name.equalsIgnoreCase(name)) return uuid;
        }
        return null;
    }

    public void setPlayerName(UUID uuid, String name){
        names.put(uuid, new PlayerName(uuid, name));
    }

    public static class PlayerName{

        public static final BuilderCodec<PlayerName> CODEC = BuilderCodec.builder(PlayerName.class, PlayerName::new)
                .append(new KeyedCodec<>("UUID", Codec.UUID_STRING),
                        (modifiedTracking, id, extraInfo) -> modifiedTracking.uuid = id,
                        (modifiedTracking, extraInfo) -> modifiedTracking.uuid).add()
                .append(new KeyedCodec<>("Name", Codec.STRING),
                        (modifiedTracking, string, extraInfo) -> modifiedTracking.name = string,
                        (modifiedTracking, extraInfo) -> modifiedTracking.name).add()
                .build();

        public static ArrayCodec<PlayerName> CODEC_ARRAY = new ArrayCodec<>(CODEC, PlayerName[]::new);

        private UUID uuid;
        private String name;

        public PlayerName(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public PlayerName() {
            this(UUID.randomUUID(), "");
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }
    }
}
