package com.buuz135.simpleclaims.gui;

import com.buuz135.simpleclaims.Main;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.ToClientPacket;
import com.hypixel.hytale.protocol.packets.setup.AssetFinalize;
import com.hypixel.hytale.protocol.packets.setup.AssetInitialize;
import com.hypixel.hytale.protocol.packets.setup.AssetPart;
import com.hypixel.hytale.protocol.packets.setup.RequestCommonAssetsRebuild;
import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
import com.hypixel.hytale.server.core.asset.common.CommonAsset;
import com.hypixel.hytale.server.core.asset.common.CommonAssetRegistry;
import com.hypixel.hytale.server.core.io.PacketHandler;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.chunk.palette.BitFieldArr;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.chunk.ChunkWorldMap;
import it.unimi.dsi.fastutil.longs.LongArraySet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/*
This will generate a map image for the chunks requested, combining them into a single PNG image.
This can be then sent to the client to override the default map image used in the Chunk Info GUI.

Note: The same hash is used for all generated images, as the client only uses this hash to cache it locally,
so using the same hash means the client will only store one image and not keep them for the 30-day cache time.
 */
public class ChunkInfoMapAsset extends CommonAsset {

    // "Buuz135_SimpleClaims" in hex padded with 2 zeros at the start and then padded to 64 characters at the end
    private static final String HASH = "004275757a3133355f53696d706c65436c61696d730000000000000000000000";
    private static final String PATH = "UI/Custom/SimpleClaims/Map.png";
    private static final ConcurrentHashMap<String, CachedMapAsset> CACHE = new ConcurrentHashMap<>();

    private final byte[] data;

    private ChunkInfoMapAsset(byte[] data) {
        super(PATH, HASH, data);
        this.data = data;
    }

    @Override
    protected CompletableFuture<byte[]> getBlob0() {
        return CompletableFuture.completedFuture(data);
    }

    public static CommonAsset empty() {
        return CommonAssetRegistry.getByName(PATH);
    }

    public static CompletableFuture<ChunkInfoMapAsset> generate(PlayerRef player, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
        var worldId = player.getWorldUuid();
        if (worldId == null) return null;
        var world = Universe.get().getWorld(worldId);
        if (world == null) return null;
        var manager = world.getWorldMapManager();
        var basePartSize = MathUtil.fastFloor(32.0F * manager.getWorldMapSettings().getImageScale());
        var resolutionScale = Math.max(0.1F, Math.min(1.0F, Main.CONFIG.get().getClaimUIMapResolutionScale()));
        var partSize = Math.max(1, MathUtil.fastFloor(basePartSize * resolutionScale));
        var cacheTtlSeconds = Math.max(0, Main.CONFIG.get().getClaimUIMapCacheTtlSeconds());
        var cacheTtlMs = cacheTtlSeconds * 1000L;
        var cacheKey = worldId + ":" + minChunkX + ":" + minChunkZ + ":" + maxChunkX + ":" + maxChunkZ + ":" + partSize;
        var now = System.currentTimeMillis();
        if (cacheTtlMs > 0L) {
            var cached = CACHE.get(cacheKey);
            if (cached != null && (now - cached.createdAtMs) <= cacheTtlMs) {
                return cached.asset;
            }
        }
        var chunks = new LongArraySet();
        for (int x = minChunkX; x <= maxChunkX; x++) {
            for (int z = minChunkZ; z <= maxChunkZ; z++) {
                chunks.add(ChunkUtil.indexChunk(x, z));
            }
        }

        var generated = ChunkWorldMap.INSTANCE.generate(world, partSize, partSize, chunks).thenApply(map -> {
            int imageWidth = partSize * (maxChunkX - minChunkX + 1);
            int imageHeight = partSize * (maxChunkZ - minChunkZ + 1);
            var image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            var finalPixels = new int[imageWidth * imageHeight];

            for (int x = minChunkX; x <= maxChunkX; x++) {
                for (int z = minChunkZ; z <= maxChunkZ; z++) {
                    var index = ChunkUtil.indexChunk(x, z);
                    var chunkImage = map.getChunks().get(index);
                    if (chunkImage != null) {
                        var pixels = getPixels(chunkImage);
                        var width = chunkImage.width;
                        var height = chunkImage.height;

                        if (pixels == null) {
                            System.out.println("Chunk image data is null for chunk: " + x + ", " + z);
                            continue;
                        }
                        if (width != partSize || height != partSize) {
                            System.out.println("Chunk image size mismatch: " + width + "x" + height);
                            continue;
                        }

                        int imageX = (x - minChunkX) * partSize;
                        int imageZ = (z - minChunkZ) * partSize;

                        int srcIndex = 0;
                        for (int pixelY = 0; pixelY < height; pixelY++) {
                            int dstRowIndex = (imageZ + pixelY) * imageWidth + imageX;
                            for (int pixelX = 0; pixelX < width; pixelX++) {
                                var pixel = pixels[srcIndex++];
                                finalPixels[dstRowIndex + pixelX] = pixel << 24 | (pixel >> 8 & 0x00FFFFFF);
                            }
                        }
                    }
                }
            }
            image.setRGB(0, 0, imageWidth, imageHeight, finalPixels, 0, imageWidth);

            try {
                var baos = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", baos);
                return new ChunkInfoMapAsset(baos.toByteArray());
            } catch (IOException e) {
                return null;
            }
        });
        if (cacheTtlMs > 0L) {
            CACHE.put(cacheKey, new CachedMapAsset(now, generated));
            generated.whenComplete((asset, throwable) -> {
                if (throwable != null || asset == null) {
                    CACHE.remove(cacheKey);
                }
            });
            pruneExpiredEntries(now, cacheTtlMs);
        }
        return generated;
    }

    public static int[] getPixels(MapImage image) {
        if (image.bitsPerIndex == 0) {
            return image.palette;
        }
        int pixelCount = image.width * image.height;
        int[] pixels = new int[pixelCount];
        BitFieldArr indices = new BitFieldArr(image.bitsPerIndex, pixelCount);
        indices.set(image.packedIndices);
        for (int i = 0; i < pixelCount; ++i) {
            pixels[i] = image.palette[indices.get(i)];
        }
        return pixels;
    }

    // Copy of CommonAssetModule#sendAssets but adapted to only send 1 asset to a single player
    public static void sendToPlayer(PacketHandler handler, CommonAsset asset) {
        byte[] allBytes = asset.getBlob().join();
        byte[][] parts = ArrayUtil.split(allBytes, 2621440);
        ToClientPacket[] packets = new ToClientPacket[2 + parts.length];
        packets[0] = new AssetInitialize(asset.toPacket(), allBytes.length);

        for(int partIndex = 0; partIndex < parts.length; ++partIndex) {
            packets[1 + partIndex] = new AssetPart(parts[partIndex]);
        }

        packets[packets.length - 1] = new AssetFinalize();
        handler.write(packets);
        handler.writeNoCache(new RequestCommonAssetsRebuild());
    }

    private static void pruneExpiredEntries(long now, long ttlMs) {
        if (CACHE.size() < 128) return;
        CACHE.entrySet().removeIf(entry -> (now - entry.getValue().createdAtMs) > ttlMs);
    }
    private record CachedMapAsset(long createdAtMs, CompletableFuture<ChunkInfoMapAsset> asset) {
    }
}
