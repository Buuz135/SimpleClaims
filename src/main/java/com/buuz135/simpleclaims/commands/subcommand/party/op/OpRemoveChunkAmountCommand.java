package com.buuz135.simpleclaims.commands.subcommand.party.op;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyOverride;
import com.buuz135.simpleclaims.claim.party.PartyOverrides;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

public class OpRemoveChunkAmountCommand extends AbstractAsyncCommand {

    private final RequiredArg<Integer> amount;
    private final RequiredArg<String> name;

    public OpRemoveChunkAmountCommand() {
        super("remove-chunk-amount", "Remove the specified amount of chunks from the party of the specified player name");
        this.name = this.withRequiredArg("player-name", "The player name", ArgTypes.STRING);
        this.amount = this.withRequiredArg("amount", "The amount of bonus chunks to remove from the party", ArgTypes.INTEGER);
        this.requirePermission(CommandMessages.ADMIN_PERM + "remove-chunk-amount");
    }

    @NonNullDecl
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        var selectedPlayer = commandContext.get(this.name);
        var selectedAmount = amount.get(commandContext);
        var uuidSelectedPlayer = ClaimManager.getInstance().getPlayerNameTracker().getPlayerUUID(selectedPlayer);
        if (uuidSelectedPlayer == null) {
            sender.sendMessage(CommandMessages.PLAYER_NOT_FOUND);
            return CompletableFuture.completedFuture(null);
        }
        var party = ClaimManager.getInstance().getPartyFromPlayer(uuidSelectedPlayer);
        if (party == null) {
            sender.sendMessage(CommandMessages.PARTY_NOT_FOUND);
            return CompletableFuture.completedFuture(null);
        }

        int currentBonuses = party.getBonusChunks();

        int newBonuses = Math.max(0, currentBonuses - selectedAmount);

        party.setOverride(new PartyOverride(PartyOverrides.BONUS_CLAIM_CHUNKS, new PartyOverride.PartyOverrideValue("integer", newBonuses)));
        ClaimManager.getInstance().saveParty(party);
        sender.sendMessage(CommandMessages.MODIFIED_MAX_CHUNK_AMOUNT.param("party_name", party.getName()).param("amount", party.getMaxClaimAmount()));
        return CompletableFuture.completedFuture(null);
    }
}