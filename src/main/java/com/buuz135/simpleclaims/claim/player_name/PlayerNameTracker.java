package com.buuz135.simpleclaims.claim.player_name;

import dev.unnm3d.codeclib.config.FieldName;

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

    public String getPlayerName(UUID uuid) {
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

    public void setPlayerName(UUID uuid, String name, long lastSeen) {
        names.put(uuid, new PlayerName(uuid, name, lastSeen));
    }

    public HashMap<UUID, PlayerName> getNamesMap() {
        return names;
    }

    public static class PlayerName {

        @FieldName("UUID")
        private UUID uuid;
        @FieldName("Name")
        private String name;
        @FieldName("LastSeen")
        private long lastSeen;

        public PlayerName(UUID uuid, String name, long lastSeen) {
            this.uuid = uuid;
            this.name = name;
            this.lastSeen = lastSeen;
        }

        public PlayerName() {
            this(UUID.randomUUID(), "", 0);
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public long getLastSeen() {
            return lastSeen;
        }
    }
}
