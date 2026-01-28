package com.buuz135.simpleclaims.gui;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.buuz135.simpleclaims.util.MessageHelper;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.util.ColorParseUtil;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.unnm3d.codeclib.config.CodecFactory;
import dev.unnm3d.codeclib.config.FieldName;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

public class ChunkInfoGui extends InteractiveCustomUIPage<ChunkInfoGui.ChunkInfoData> {

    private final int chunkX;
    private final int chunkZ;
    private final String dimension;
    private boolean isOp;

    private CompletableFuture<ChunkInfoMapAsset> mapAsset = null;

    public ChunkInfoGui(@NonNullDecl PlayerRef playerRef, String dimension, int chunkX, int chunkZ, boolean isOp) {
        super(playerRef, CustomPageLifetime.CanDismiss, CodecFactory.createClassCodec(ChunkInfoData.class));
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.dimension = dimension;
        this.isOp = isOp;
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl ChunkInfoData data) {
        super.handleDataEvent(ref, store, data);
        if (data.action != null){
            var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
            var playerInstance = store.getComponent(ref, Player.getComponentType());
            var playerParty = ClaimManager.getInstance().getPartyFromPlayer(playerRef.getUuid());
            if (playerParty == null && !isOp) {
                this.sendUpdate();
                return;
            }
            var actions = data.action.split(":");
            var button = actions[0];
            var x = Integer.parseInt(actions[1]);
            var z = Integer.parseInt(actions[2]);
            if (button.equals("LeftClicking")) {
                if (isOp) {
                    var selectedPartyID = ClaimManager.getInstance().getAdminUsageParty().get(playerRef.getUuid());
                    if (selectedPartyID == null) {
                        playerInstance.sendMessage(CommandMessages.ADMIN_PARTY_NOT_SELECTED);
                        this.sendUpdate();
                        return;
                    }
                    var chunk = ClaimManager.getInstance().getChunk(dimension, x, z);
                    var selectedParty = ClaimManager.getInstance().getPartyById(selectedPartyID);
                    if ((chunk == null || ClaimManager.getInstance().getPartyById(chunk.getPartyOwner()) == null) && selectedParty != null && ClaimManager.getInstance().hasEnoughClaimsLeft(selectedParty)) {
                        var chunkInfo = ClaimManager.getInstance().claimChunkBy(dimension, x, z, selectedParty, playerInstance, playerRef);
                        ClaimManager.getInstance().queueMapUpdate(playerInstance.getWorld(), x, z);
                    }
                } else {
                    var chunk = ClaimManager.getInstance().getChunk(dimension, x, z);
                    if ((chunk == null || ClaimManager.getInstance().getPartyById(chunk.getPartyOwner()) == null) && ClaimManager.getInstance().hasEnoughClaimsLeft(playerParty)) {
                        var chunkInfo = ClaimManager.getInstance().claimChunkBy(dimension, x, z, playerParty, playerInstance, playerRef);
                        ClaimManager.getInstance().queueMapUpdate(playerInstance.getWorld(), x, z);
                    }
                }
            }
            if (button.equals("RightClicking")) {
                if (isOp) {
                    var chunk = ClaimManager.getInstance().getChunk(dimension, x, z);
                    var selectedPartyID = ClaimManager.getInstance().getAdminUsageParty().get(playerRef.getUuid());
                    if (selectedPartyID == null) {
                        playerInstance.sendMessage(CommandMessages.ADMIN_PARTY_NOT_SELECTED);
                        this.sendUpdate();
                        return;
                    }
                    if (chunk != null && selectedPartyID.equals(chunk.getPartyOwner())) {
                        ClaimManager.getInstance().unclaim(dimension, x, z);
                        ClaimManager.getInstance().queueMapUpdate(playerInstance.getWorld(), x, z);
                    }
                } else {
                    var chunk = ClaimManager.getInstance().getChunk(dimension, x, z);
                    if (chunk != null && chunk.getPartyOwner().equals(playerParty.getId())) {
                        ClaimManager.getInstance().unclaim(dimension, x, z);
                        ClaimManager.getInstance().queueMapUpdate(playerInstance.getWorld(), x, z);
                    }
                }
            }
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.build(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, true);
            return;
        }
        this.sendUpdate();
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Buuz135_SimpleClaims_ChunkVisualizer.ui");
        if (isOp) {
            uiCommandBuilder.set("#TitleText.Text", "Nearby Claimed Chunks - Admin Mode");
        }
        var player = store.getComponent(ref, PlayerRef.getComponentType());
        var playerParty = ClaimManager.getInstance().getPartyFromPlayer(player.getUuid());
        if (isOp) {
            var selectedPartyID = ClaimManager.getInstance().getAdminUsageParty().get(playerRef.getUuid());
            if (selectedPartyID != null) {
                playerParty = ClaimManager.getInstance().getPartyById(selectedPartyID);
            }
        }
        uiCommandBuilder.set("#ClaimedChunksInfo #ClaimedChunksCount.Text", ClaimManager.getInstance().getAmountOfClaims(playerParty)+ "");
        uiCommandBuilder.set("#ClaimedChunksInfo #MaxChunksCount.Text", playerParty.getMaxClaimAmount() + "");

        if (this.mapAsset == null && Main.CONFIG.get().isRenderMapInClaimUI()) {
            ChunkInfoMapAsset.sendToPlayer(this.playerRef.getPacketHandler(), ChunkInfoMapAsset.empty());

            this.mapAsset = ChunkInfoMapAsset.generate(this.playerRef, chunkX - 8, chunkZ - 8, chunkX + 8, chunkZ + 8);

            if (this.mapAsset != null) {
                this.mapAsset.thenAccept(asset -> {
                    if (asset == null) return;

                    ChunkInfoMapAsset.sendToPlayer(this.playerRef.getPacketHandler(), asset);
                    this.sendUpdate();
                });
            }
        }

        var hytaleGold = "#93844c";

        for (int z = 0; z <= 8*2; z++) {
            uiCommandBuilder.appendInline("#ChunkCards", "Group { LayoutMode: Left; Anchor: (Bottom: 0); }");
            for (int x = 0; x <= 8*2; x++) {
                uiCommandBuilder.append("#ChunkCards[" + z  + "]", "Pages/Buuz135_SimpleClaims_ChunkEntry.ui");
                var chunk = ClaimManager.getInstance().getChunk(dimension, chunkX + x - 8, chunkZ + z - 8);
                if ((z - 8) == 0 && (x - 8) == 0) {
                    uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].Text", "+");
                }
                if (chunk != null) {
                    var partyInfo = ClaimManager.getInstance().getPartyById(chunk.getPartyOwner());
                    if (partyInfo != null) {
                        var color = new Color(partyInfo.getColor());
                        color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
                        uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].Background.Color", ColorParseUtil.colorToHexAlpha(color));
                        uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].OutlineColor", ColorParseUtil.colorToHexAlpha(color));
                        uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].OutlineSize", 1);
                        var tooltip = MessageHelper.multiLine()
                                .append(Message.raw("Owner: ").bold(true).color(hytaleGold))
                                .append(Message.raw(partyInfo.getName())).nl()
                                .append(Message.raw("Description: ").bold(true).color(hytaleGold))
                                .append(Message.raw(partyInfo.getDescription()));
                        if (playerParty != null && playerParty.getId().equals(partyInfo.getId())) {
                            tooltip = tooltip.nl().nl().append(Message.raw("*Right Click to Unclaim*").bold(true).color(Color.RED.darker().darker()));
                        }
                        uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].TooltipTextSpans", tooltip.build());
                        uiEventBuilder.addEventBinding(CustomUIEventBindingType.RightClicking, "#ChunkCards[" + z + "][" + x + "]", EventData.of("Action", "RightClicking:" + (chunkX + x - 8) + ":" + (chunkZ + z - 8)));
                    } else { // The chunk doesnt have a valid party
                        var tooltip = MessageHelper.multiLine().append(Message.raw("Wilderness").bold(true).color(Color.GREEN.darker()));
                        if (playerParty != null) {
                            tooltip = tooltip.nl().nl().append(Message.raw("*Left Click to claim*").bold(true).color(Color.GRAY));
                            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ChunkCards[" + z + "][" + x + "]", EventData.of("Action", "LeftClicking:" + (chunkX + x - 8) + ":" + (chunkZ + z - 8)));
                        } else {
                            tooltip = tooltip.nl().nl().append(Message.raw("*Create a party to claim*").bold(true).color(Color.GRAY));
                        }
                        uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].TooltipTextSpans", tooltip.build());
                    }
                } else {
                    var tooltip = MessageHelper.multiLine().append(Message.raw("Wilderness" ).bold(true).color(Color.GREEN.darker()));
                    if (playerParty != null) {
                        tooltip = tooltip.nl().nl().append(Message.raw("*Left Click to claim*").bold(true).color(Color.GRAY));
                        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ChunkCards[" + z + "][" + x + "]", EventData.of("Action", "LeftClicking:" + (chunkX + x - 8) + ":" + (chunkZ + z - 8)));
                    } else {
                        tooltip = tooltip.nl().nl().append(Message.raw("*Create a party to claim*").bold(true).color(Color.GRAY));
                    }
                    uiCommandBuilder.set("#ChunkCards[" + z + "][" + x + "].TooltipTextSpans", tooltip.build());
                }
            }
        }
    }

    public static class ChunkInfoData {

        @FieldName("Action")
        private String action;

    }
}
