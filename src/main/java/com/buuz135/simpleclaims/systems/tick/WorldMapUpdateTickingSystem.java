package com.buuz135.simpleclaims.systems.tick;


import com.buuz135.simpleclaims.claim.ClaimManager;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WorldMapUpdateTickingSystem extends DelayedSystem<ChunkStore> {


    public WorldMapUpdateTickingSystem() {
        super(3);
    }

    @Override
    public void delayedTick(float v, int i, @NonNullDecl Store<ChunkStore> store) {
        World world = store.getExternalData().getWorld();
        if (ClaimManager.getInstance().getMapUpdateQueue().containsKey(world.getName())) {
            final var chunks = ClaimManager.getInstance().getMapUpdateQueue().get(world.getName());
            world.execute(() -> {
                world.getWorldMapManager().clearImagesInChunks(chunks);
                for (PlayerRef playerRef : world.getPlayerRefs()) {
                    var player = world.getEntityStore().getStore().getComponent(playerRef.getReference(), Player.getComponentType());
                    player.getWorldMapTracker().clearChunks(chunks);
                }
            });
            ClaimManager.getInstance().getMapUpdateQueue().remove(world.getName());
            ClaimManager.getInstance().getWorldsNeedingUpdates().remove(world.getName());
        }
    }

}
