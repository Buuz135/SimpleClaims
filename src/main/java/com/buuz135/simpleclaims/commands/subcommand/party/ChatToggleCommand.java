package com.buuz135.simpleclaims.commands.subcommand.party;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.chat.PartyChatManager;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class ChatToggleCommand extends AbstractAsyncCommand {

    public ChatToggleCommand() {
        super("chat-toggle", "Toggles the party chat");
        this.addAliases("ct", "pc", "party-chat");
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(@Nonnull final CommandContext commandContext) {
        final CommandSender sender = commandContext.sender();
        if (sender instanceof final Player player) {
            final Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                final Store<EntityStore> store = ref.getStore();
                final World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    final PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
                    if (playerRef != null) {
                        final var party = ClaimManager.getInstance().getPartyFromPlayer(playerRef.getUuid());
                        if (party == null) {
                            player.sendMessage(CommandMessages.NOT_IN_A_PARTY);
                            return;
                        }

                        final var result = PartyChatManager.getInstance().togglePartyChat(playerRef.getUuid());
                        switch (result) {
                            case ACTIVATED -> player.sendMessage(CommandMessages.PARTY_CHAT_ACTIVATED);
                            case DEACTIVATED -> player.sendMessage(CommandMessages.PARTY_CHAT_DEACTIVATED);
                            case NOT_IN_A_PARTY -> {
                                player.sendMessage(CommandMessages.NOT_IN_A_PARTY);
                                return;
                            }
                        }

                        if (Main.CONFIG.get().isNotifyPartyChatToggling()) {
                            this.notifyPartyMembers(party, result, player.getDisplayName());
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

    private void notifyPartyMembers(final PartyInfo playerParty, final PartyChatManager.ToggleResult result, final String playerName) {
        final var message = (result == PartyChatManager.ToggleResult.ACTIVATED
                ? CommandMessages.PLAYER_PARTY_CHAT_ACTIVATED
                : CommandMessages.PLAYER_PARTY_CHAT_DEACTIVATED).param("player", playerName);

        final var partyMembers = playerParty.getMembers();
        for (final UUID uuid : partyMembers) {
            final var player = Universe.get().getPlayer(uuid);
            if (player != null && player.isValid()) {
                player.sendMessage(message);
            }
        }
    }
}
