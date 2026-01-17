package com.buuz135.simpleclaims.commands.subcommand.party;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
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

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class PartyAcceptCommand extends AbstractAsyncCommand {

    public PartyAcceptCommand() {
        super("invite-accept", "Accepts your most recent party invite");
        this.requirePermission(CommandMessages.BASE_PERM + "accept-invite");
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
                        var party = ClaimManager.getInstance().getPartyFromPlayer(playerRef.getUuid());
                        if (party != null) {
                            player.sendMessage(CommandMessages.IN_A_PARTY);
                            return;
                        }
                        var invite = ClaimManager.getInstance().acceptInvite(playerRef);
                        if (invite != null) {
                            var partyInvite = ClaimManager.getInstance().getPartyById(invite.party());
                            if (partyInvite != null) {
                                player.sendMessage(CommandMessages.PARTY_INVITE_JOIN.param("party_name", partyInvite.getName()).param("username", player.getDisplayName()));
                                var playerSender = player.getWorld().getEntity(invite.sender());
                                if (playerSender instanceof Player playerSenderPlayer) {
                                    playerSenderPlayer.sendMessage(CommandMessages.PARTY_INVITE_JOIN.param("party_name", partyInvite.getName()).param("username", player.getDisplayName()));
                                }
                            }
                        } else {
                            player.sendMessage(Message.raw("You don't have any pending invites").bold(true).color(Color.RED));
                        }
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