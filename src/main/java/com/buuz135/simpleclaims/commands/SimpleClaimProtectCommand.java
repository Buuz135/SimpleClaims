package com.buuz135.simpleclaims.commands;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.subcommand.chunk.ClaimChunkCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.UnclaimChunkCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.op.OpChunkGuiCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.op.OpClaimChunkCommand;
import com.buuz135.simpleclaims.commands.subcommand.chunk.op.OpUnclaimChunkCommand;
import com.buuz135.simpleclaims.gui.ChunkInfoGui;
import com.buuz135.simpleclaims.gui.ChunkInfoMapAsset;
import com.buuz135.simpleclaims.util.TranslationHelper;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class SimpleClaimProtectCommand extends AbstractAsyncCommand {

    public SimpleClaimProtectCommand() {
        super("simpleclaims", "Opens the chunk claim gui");
        this.addAliases(Main.CONFIG.get().getClaimCommandAliases());
        this.requirePermission(CommandMessages.BASE_PERM + "claim-gui");

        this.addSubCommand(new ClaimChunkCommand());
        this.addSubCommand(new UnclaimChunkCommand());

        this.addSubCommand(new OpClaimChunkCommand());
        this.addSubCommand(new OpUnclaimChunkCommand());
        this.addSubCommand(new OpChunkGuiCommand());
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
                    var position = store.getComponent(ref, TransformComponent.getComponentType());
                    int chunkX = ChunkUtil.chunkCoordinate(position.getPosition().getX());
                    int chunkZ = ChunkUtil.chunkCoordinate(position.getPosition().getZ());
                    if (Main.CONFIG.get().isRenderMapInClaimUI()) {
                        NotificationUtil.sendNotification(playerRef.getPacketHandler(), Message.raw(TranslationHelper.rawTextOrEnglish("commands.simpleclaims.waitingOpenWindow", playerRef)), NotificationStyle.Default);
                        var mapFuture = ChunkInfoMapAsset.generate(playerRef, chunkX - 8, chunkZ - 8, chunkX + 8, chunkZ + 8);
                        if (mapFuture != null) {
                            mapFuture.thenRunAsync(() ->
                                player.getPageManager().openCustomPage(ref, store, new ChunkInfoGui(playerRef, player.getWorld().getName(), chunkX, chunkZ, false, mapFuture))
                            , world);
                            return;
                        }
                    }
                    player.getPageManager().openCustomPage(ref, store, new ChunkInfoGui(playerRef, player.getWorld().getName(), chunkX, chunkZ, false));
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
