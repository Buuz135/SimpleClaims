package com.buuz135.simpleclaims.commands.subcommand.party;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.buuz135.simpleclaims.gui.PartyInfoEditGui;
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

public class PartyInviteCommand extends AbstractAsyncCommand {

    private RequiredArg<PlayerRef> name;

    public PartyInviteCommand() {
        super("invite", "Invites a player to your party");
        this.name = this.withRequiredArg("player", "The player name", ArgTypes.PLAYER_REF);
        this.requirePermission(CommandMessages.BASE_PERM + "create-invite");
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
                        if (party == null) {
                            player.sendMessage(CommandMessages.NOT_IN_A_PARTY);
                            return;
                        }
                        PlayerRef invitedPlayer = commandContext.get(this.name);
                        if (invitedPlayer == null) {
                            player.sendMessage(CommandMessages.PLAYER_NOT_FOUND);
                            return;
                        }
                        Player invitedPlayerPlayer = store.getComponent(invitedPlayer.getReference(), Player.getComponentType());
                        PlayerRef invintedPlayerPlayerRef = store.getComponent(invitedPlayer.getReference(), PlayerRef.getComponentType());
                        if (invitedPlayerPlayer == null) {
                            player.sendMessage(CommandMessages.PLAYER_NOT_FOUND);
                            return;
                        }
                        if (party.isOwnerOrMember(invitedPlayer.getUuid())) {
                            player.sendMessage(CommandMessages.PARTY_INVITE_SELF);
                            return;
                        }
                        if (Main.CONFIG.get().getMaxPartyMembers() != -1 && (party.getMembers().length + ClaimManager.getInstance().getPartyInvites().values().stream().filter(partyInvite -> partyInvite.party().equals(party.getId())).count()) >= Main.CONFIG.get().getMaxPartyMembers()) {
                            player.sendMessage(CommandMessages.PARTY_MEMBER_LIMIT_REACHED);
                            return;
                        }
                        ClaimManager.getInstance().invitePlayerToParty(invintedPlayerPlayerRef, party, playerRef);
                        player.sendMessage(CommandMessages.PARTY_INVITE_SENT.param("username", invitedPlayerPlayer.getDisplayName()));
                        invitedPlayer.sendMessage(CommandMessages.PARTY_INVITE_RECEIVED.param("party_name", party.getName()).param("username", player.getDisplayName()));
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