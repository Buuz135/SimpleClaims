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

 */
public class ChunkInfoMapAsset extends CommonAsset {

    private static final String PATH = "UI/Custom/SimpleClaims/Map.png";
    private static final ConcurrentHashMap<String, CachedMapAsset> CACHE = new ConcurrentHashMap<>();

    private final byte[] data;

    private ChunkInfoMapAsset(byte[] data) {
        super(PATH, data);
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
        var resolutionScale = Math.max(0.1F, Math.min(1.0F, 1f));
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
            var image = new BufferedImage(
                    partSize * (maxChunkX - minChunkX + 1),
                    partSize * (maxChunkZ - minChunkZ + 1),
                    BufferedImage.TYPE_INT_ARGB
            );

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

                        for (var i = 0; i < pixels.length; i++) {
                            var pixel = pixels[i];
                            var abgrToArgb = pixel << 24 | (pixel >> 8 & 0x00FFFFFF);

                            var pixelX = i % width;
                            var pixelY = i / width;
                            image.setRGB(imageX + pixelX, imageZ + pixelY, abgrToArgb);
                        }
                    }
                }
            }

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

    private static void pruneExpiredEntries(long now, long ttlMs) {
        if (CACHE.size() < 128) return;
        CACHE.entrySet().removeIf(entry -> (now - entry.getValue().createdAtMs) > ttlMs);
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

    private record CachedMapAsset(long createdAtMs, CompletableFuture<ChunkInfoMapAsset> asset) {
    }
}
