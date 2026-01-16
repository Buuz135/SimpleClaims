package com.buuz135.simpleclaims.commands.subcommand.chunk;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class ClaimChunkCommand extends AbstractAsyncCommand {

    public ClaimChunkCommand() {
        super("claim", "Claims the chunk where you are");
        this.requirePermission(CommandMessages.BASE_PERM + "claim");
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();
        if (sender instanceof Player player) {
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRef = ref.getStore().getComponent(ref, PlayerRef.getComponentType());
                    if (playerRef == null) return;
                    if (!ClaimManager.getInstance().canClaimInDimension(world)) {
                        player.sendMessage(CommandMessages.CANT_CLAIM_IN_THIS_DIMENSION);
                        return;
                    }
                    var party = ClaimManager.getInstance().getPartyFromPlayer(playerRef.getUuid());
                    if (party == null) {
                        party = ClaimManager.getInstance().createParty(player, playerRef, false);
                        player.sendMessage(CommandMessages.PARTY_CREATED);
                    }
                    var chunk = ClaimManager.getInstance().getChunkRawCoords(player.getWorld().getName(), (int) playerRef.getTransform().getPosition().getX(), (int) playerRef.getTransform().getPosition().getZ());
                    if (chunk != null) {
                        player.sendMessage(chunk.getPartyOwner().equals(party.getId()) ? CommandMessages.ALREADY_CLAIMED_BY_YOU : CommandMessages.ALREADY_CLAIMED_BY_ANOTHER_PLAYER);
                        return;
                    }
                    if (!ClaimManager.getInstance().hasEnoughClaimsLeft(party)) {
                        player.sendMessage(CommandMessages.NOT_ENOUGH_CHUNKS);
                        return;
                    }
                    var chunkInfo = ClaimManager.getInstance().claimChunkByRawCoords(player.getWorld().getName(), (int) playerRef.getTransform().getPosition().getX(), (int) playerRef.getTransform().getPosition().getZ(), party, player, playerRef);
                    ClaimManager.getInstance().queueMapUpdate(player.getWorld(), chunkInfo.getChunkX(), chunkInfo.getChunkZ());
                    player.sendMessage(CommandMessages.CLAIMED);
                }, world);
            } else {
                commandContext.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return CompletableFuture.completedFuture(null);
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}
