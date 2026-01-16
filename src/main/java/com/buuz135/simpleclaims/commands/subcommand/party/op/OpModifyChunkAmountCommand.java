package com.buuz135.simpleclaims.commands.subcommand.party.op;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyOverride;
import com.buuz135.simpleclaims.claim.party.PartyOverrides;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class OpModifyChunkAmountCommand extends AbstractAsyncCommand {

    private RequiredArg<Integer> amount;

    public OpModifyChunkAmountCommand() {
        super("admin-modify-chunk", "Changes the chunk amount limit of a party, must have selected a party first using the /scp admin-party-list command");
        this.amount = this.withRequiredArg("amount", "The amount of chunks the party can claim", ArgTypes.INTEGER);
        this.requirePermission(CommandMessages.ADMIN_PERM + "admin-modify-chunk");
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
                    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRef != null) {
                        var selectedPartyID = ClaimManager.getInstance().getAdminUsageParty().get(playerRef.getUuid());
                        if (selectedPartyID == null) {
                            player.sendMessage(CommandMessages.ADMIN_PARTY_NOT_SELECTED);
                            return;
                        }
                        var party = ClaimManager.getInstance().getPartyById(selectedPartyID);
                        if (party == null) {
                            player.sendMessage(CommandMessages.PARTY_NOT_FOUND);
                            return;
                        }
                        var selectedAmount = amount.get(commandContext);
                        party.setOverride(new PartyOverride(PartyOverrides.CLAIM_CHUNK_AMOUNT, new PartyOverride.PartyOverrideValue("integer", selectedAmount)));
                        ClaimManager.getInstance().markDirty();
                        player.sendMessage(CommandMessages.MODIFIED_MAX_CHUNK_AMOUNT.param("party_name", party.getName()).param("amount", selectedAmount));
                    }
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
