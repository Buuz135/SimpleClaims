package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockComponentSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.joml.Vector3ic;

import javax.annotation.Nullable;

public final class WorldBlockUtil {
    private WorldBlockUtil() {
    }

    @Nullable
    public static BlockType getBlockType(World world, Vector3ic position) {
        return getBlockType(world, position.x(), position.y(), position.z());
    }

    @Nullable
    public static BlockType getBlockType(World world, int x, int y, int z) {
        var chunkStore = world.getChunkStore();
        var sectionRef = chunkStore.getChunkSectionReferenceAtBlock(x, y, z);
        if (sectionRef == null || !sectionRef.isValid()) return null;

        var blockSection = chunkStore.getStore().getComponent(sectionRef, BlockSection.getComponentType());
        if (blockSection == null) return null;

        return BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
    }

    @Nullable
    public static <T extends Component<ChunkStore>> T getBlockComponent(World world, int x, int y, int z, ComponentType<ChunkStore, T> componentType) {
        var chunkStore = world.getChunkStore();
        var sectionRef = chunkStore.getChunkSectionReferenceAtBlock(x, y, z);
        if (sectionRef == null || !sectionRef.isValid()) return null;

        var blockComponents = chunkStore.getStore().getComponent(sectionRef, BlockComponentSection.getComponentType());
        if (blockComponents == null) return null;

        int blockIndex = ChunkUtil.indexBlock(x, y, z);
        var blockRef = blockComponents.getBlockReference(blockIndex);
        if (blockRef != null && blockRef.isValid()) {
            return blockRef.getStore().getComponent(blockRef, componentType);
        }

        var holder = blockComponents.getBlockHolder(blockIndex);
        return holder == null ? null : holder.getComponent(componentType);
    }
}
