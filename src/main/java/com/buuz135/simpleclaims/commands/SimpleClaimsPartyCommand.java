package com.buuz135.simpleclaims.commands;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.subcommand.party.CreatePartyCommand;
import com.buuz135.simpleclaims.commands.subcommand.party.PartyAcceptCommand;
import com.buuz135.simpleclaims.commands.subcommand.party.PartyInviteCommand;
import com.buuz135.simpleclaims.commands.subcommand.party.PartyLeaveCommand;
import com.buuz135.simpleclaims.commands.subcommand.party.op.*;
import com.buuz135.simpleclaims.gui.PartyInfoEditGui;
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

public class SimpleClaimsPartyCommand extends AbstractAsyncCommand {

    public SimpleClaimsPartyCommand() {
        super("simpleclaimsparty", "Simple Claims Party Commands" );
        this.addAliases(Main.CONFIG.get().getPartyCommandAliases());
        this.requirePermission(CommandMessages.BASE_PERM + "edit-party");

        this.addSubCommand(new CreatePartyCommand());
        this.addSubCommand(new PartyInviteCommand());
        this.addSubCommand(new PartyAcceptCommand());
        this.addSubCommand(new PartyLeaveCommand());
        //OP Commands
        this.addSubCommand(new OpCreatePartyCommand());
        this.addSubCommand(new OpPartyListCommand());
        this.addSubCommand(new OpModifyChunkAmountCommand());
        this.addSubCommand(new OpAllModifyChunkAmountCommand());
        this.addSubCommand(new OpOverrideCommand());
        this.addSubCommand(new OpAddChunkAmountCommand());
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
                            commandContext.sendMessage(CommandMessages.NOT_IN_A_PARTY);
                            return;
                        }
                        player.getPageManager().openCustomPage(ref, store, new PartyInfoEditGui(playerRef, party, false));
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
