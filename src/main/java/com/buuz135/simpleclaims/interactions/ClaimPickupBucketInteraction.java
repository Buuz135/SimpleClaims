package com.buuz135.simpleclaims.interactions;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.PlaceFluidInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.RefillContainerInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.function.Predicate;

public class ClaimPickupBucketInteraction extends RefillContainerInteraction {

    public static final BuilderCodec<ClaimPickupBucketInteraction> CUSTOM_CODEC = BuilderCodec.builder(ClaimPickupBucketInteraction.class, ClaimPickupBucketInteraction::new, RefillContainerInteraction.CODEC).build();

    @Override
    protected void firstRun(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Predicate<PartyInfo> defaultInteract = PartyInfo::isBlockInteractEnabled;
        var targetBlock = context.getTargetBlock();
        if (playerRef != null && targetBlock == null) {
            Vector3d playerPos = playerRef.getTransform().getPosition();
            if (ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), (int) playerPos.x, (int) playerPos.z, defaultInteract)) {
                super.firstRun(type, context, cooldownHandler);
            }
        } else if (playerRef != null && ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), targetBlock.x, targetBlock.z, defaultInteract)) {
            super.firstRun(type, context, cooldownHandler);
        }
    }

}
