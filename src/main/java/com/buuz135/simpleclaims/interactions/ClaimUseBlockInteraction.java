package com.buuz135.simpleclaims.interactions;


import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.UseBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.function.Predicate;

public class ClaimUseBlockInteraction extends UseBlockInteraction {

    @Nonnull
    public static final BuilderCodec<ClaimUseBlockInteraction> CUSTOM_CODEC = BuilderCodec.builder(ClaimUseBlockInteraction.class, ClaimUseBlockInteraction::new, SimpleBlockInteraction.CODEC).documentation("Attempts to use the target block, executing interactions on it if any.").build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl Vector3i targetBlock, @NonNullDecl CooldownHandler cooldownHandler) {
        if (type == InteractionType.Primary || type == InteractionType.Secondary) {
            super.interactWithBlock(world, commandBuffer, type, context, itemInHand, targetBlock, cooldownHandler);
            return;
        }

        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Predicate<PartyInfo> defaultInteract = PartyInfo::isBlockInteractEnabled;
        var blockName = world.getBlockType(targetBlock).getId().toLowerCase(Locale.ROOT);
        var ignored = false;

        for (String blocksThatIgnoreInteractRestriction : Main.CONFIG.get().getBlocksThatIgnoreInteractRestrictions()) {
            if (blockName.contains(blocksThatIgnoreInteractRestriction.toLowerCase(Locale.ROOT))) ignored = true;
        }

        if (blockName.contains("chest")) defaultInteract = PartyInfo::isChestInteractEnabled;
        else if (blockName.contains("bench") && !blockName.contains("furniture"))
            defaultInteract = PartyInfo::isBenchInteractEnabled;
        else if (blockName.contains("door")) defaultInteract = PartyInfo::isDoorInteractEnabled;
        else if (blockName.contains("chair") || blockName.contains("stool") || (blockName.contains("bench") && blockName.contains("furniture")))
            defaultInteract = PartyInfo::isChairInteractEnabled;
        else if (blockName.contains("portal") || blockName.contains("teleporter"))
            defaultInteract = PartyInfo::isPortalInteractEnabled;
        if (ignored || (playerRef != null && ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), targetBlock.getX(), targetBlock.getZ(), defaultInteract))) {
            super.interactWithBlock(world, commandBuffer, type, context, itemInHand, targetBlock, cooldownHandler);
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl World world, @NonNullDecl Vector3i targetBlock) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        Predicate<PartyInfo> defaultInteract = PartyInfo::isBlockInteractEnabled;
        var blockName = world.getBlockType(targetBlock).getId().toLowerCase(Locale.ROOT);
        var ignored = false;

        for (String blocksThatIgnoreInteractRestriction : Main.CONFIG.get().getBlocksThatIgnoreInteractRestrictions()) {
            if (blockName.contains(blocksThatIgnoreInteractRestriction.toLowerCase(Locale.ROOT))) ignored = true;
        }

        if (blockName.contains("chest")) defaultInteract = PartyInfo::isChestInteractEnabled;
        else if (blockName.contains("bench") && !blockName.contains("furniture"))
            defaultInteract = PartyInfo::isBenchInteractEnabled;
        else if (blockName.contains("door")) defaultInteract = PartyInfo::isDoorInteractEnabled;
        else if (blockName.contains("chair") || blockName.contains("stool") || (blockName.contains("bench") && blockName.contains("furniture")))
            defaultInteract = PartyInfo::isChairInteractEnabled;
        else if (blockName.contains("portal") || blockName.contains("teleporter"))
            defaultInteract = PartyInfo::isPortalInteractEnabled;
        if (ignored || (playerRef != null && ClaimManager.getInstance().isAllowedToInteract(playerRef.getUuid(), player.getWorld().getName(), targetBlock.getX(), targetBlock.getZ(), defaultInteract))) {
            super.simulateInteractWithBlock(type, context, itemInHand, world, targetBlock);
        }
    }
}
