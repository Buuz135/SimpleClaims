package com.buuz135.simpleclaims.commands.subcommand.party.op;

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
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
import com.hypixel.hytale.server.core.command.system.arguments.types.SingleArgumentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class OpCreatePartyCommand extends AbstractAsyncCommand {

    private RequiredArg<String> name;

    public OpCreatePartyCommand() {
        super("admin-create", "Creates a new party");
        this.name = this.withRequiredArg("party-name", "The name of the party to create (Can be changed later)", ArgTypes.STRING);
        this.requirePermission(CommandMessages.ADMIN_PERM + "admin-create-party");
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
                    var commandName = commandContext.get(this.name);
                    if (playerRef != null) {
                        var party = ClaimManager.getInstance().getPartyFromPlayer(playerRef.getUuid());

                        party = ClaimManager.getInstance().createParty(player, playerRef, true);
                        party.setName(commandName);
                        party.setOwner(UUID.randomUUID());
                        party.setMembers(new UUID[]{});
                        player.sendMessage(CommandMessages.PARTY_CREATED);
                        player.getPageManager().openCustomPage(ref, store, new PartyInfoEditGui(playerRef, party, true));
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
