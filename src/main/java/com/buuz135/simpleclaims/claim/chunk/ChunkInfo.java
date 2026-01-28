package com.buuz135.simpleclaims.claim.chunk;

import com.buuz135.simpleclaims.claim.tracking.ModifiedTracking;
import dev.unnm3d.codeclib.config.FieldName;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChunkInfo {

    public static String formatCoordinates(int chunkX, int chunkZ) {
        return chunkX + ":" + chunkZ;
    }

    @FieldName("UUID")
    private UUID partyOwner;
    @FieldName("ChunkX")
    private int chunkX;
    @FieldName("ChunkY")
    private int chunkZ;
    @FieldName("CreatedTracker")
    private ModifiedTracking createdTracked;

    public ChunkInfo(UUID partyOwner, int chunkX, int chunkZ) {
        this.partyOwner = partyOwner;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.createdTracked = new ModifiedTracking(UUID.randomUUID(), "-", LocalDateTime.now().toString());
    }

    public ChunkInfo() {
        this(UUID.randomUUID(), 0, 0);
    }

    public UUID getPartyOwner() {
        return partyOwner;
    }

    public void setPartyOwner(UUID partyOwner) {
        this.partyOwner = partyOwner;
    }

    public int getChunkX() {
        return chunkX;
    }

    public void setChunkX(int chunkX) {
        this.chunkX = chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public void setChunkZ(int chunkZ) {
        this.chunkZ = chunkZ;
    }

    public ModifiedTracking getCreatedTracked() {
        return createdTracked;
    }

    public void setCreatedTracked(ModifiedTracking createdTracked) {
        this.createdTracked = createdTracked;
    }

    public String getCoordinates() {
        return formatCoordinates(chunkX, chunkZ);
    }

    public static final class DimensionStorage {


        @FieldName("Dimensions")
        private ChunkInfoStorage[] chunkInfoStorages;

        public DimensionStorage(ChunkInfoStorage[] chunkInfoStorages) {
            this.chunkInfoStorages = chunkInfoStorages;
        }

        public DimensionStorage() {
            this(new ChunkInfoStorage[0]);
        }

        public ChunkInfoStorage[] getChunkInfoStorages() {
            return chunkInfoStorages;
        }

        public void setChunkInfoStorages(ChunkInfoStorage[] chunkInfoStorages) {
            this.chunkInfoStorages = chunkInfoStorages;
        }
    }

    public static class ChunkInfoStorage {

        @FieldName("Dimension")
        public String dimension;
        @FieldName("ChunkInfo")
        private ChunkInfo[] chunkInfos;

        public ChunkInfoStorage() {
            this("", new ChunkInfo[0]);
        }

        public ChunkInfoStorage(String dimension, ChunkInfo[] infos) {
            this.dimension = dimension;
            this.chunkInfos = infos;
        }

        public String getDimension() {
            return dimension;
        }

        public void setDimension(String dimension) {
            this.dimension = dimension;
        }

        public ChunkInfo[] getChunkInfos() {
            return chunkInfos;
        }

        public void setChunkInfos(ChunkInfo[] chunkInfos) {
            this.chunkInfos = chunkInfos;
        }
    }
}
