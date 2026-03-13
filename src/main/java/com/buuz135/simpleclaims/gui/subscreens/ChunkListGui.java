package com.buuz135.simpleclaims.gui.subscreens;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.chunk.ChunkInfo;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.buuz135.simpleclaims.util.TranslationHelper;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.UUID;

public class ChunkListGui extends GuiWithParent<ChunkListGui.ChunkListGuiData> {

    private String requestingConfirmation;
    private final PartyInfo partyInfo;
    private final boolean isOpEdit;

    public ChunkListGui(@NonNullDecl PlayerRef playerRef, PartyInfo partyInfo, InteractiveCustomUIPage<?> parent, boolean isOpEdit) {
        super(playerRef, CustomPageLifetime.CanDismiss, ChunkListGuiData.CODEC, parent);
        this.requestingConfirmation = "-1";
        this.partyInfo = partyInfo;
        this.isOpEdit = isOpEdit;
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl ChunkListGuiData data) {
        super.handleDataEvent(ref, store, data);
        var player = store.getComponent(ref, Player.getComponentType());
        if (data.removeButtonAction != null) {
            var action = data.removeButtonAction;
            var index = data.chunkId;
            if (action.equals("Click")) {
                this.requestingConfirmation = index;
            }
            if (action.equals("Delete")) {
                if (player.hasPermission(CommandMessages.BASE_PERM + "unclaim")) {
                    var split = data.chunkId.split(":");
                    ClaimManager.getInstance().unclaim(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    if (Universe.get().getWorlds().containsKey(split[0])) {
                        ClaimManager.getInstance().queueMapUpdate(Universe.get().getWorlds().get(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
                    }
                } else {
                    playerRef.sendMessage(Message.translation("commands.parsing.error.noPermissionForCommand"));
                }
            }
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.build(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, true);
            return;
        }
        if (data.action != null) {
            if (data.action.equals("Teleport")) {
                var split = data.chunkId.split(":");
                if (Universe.get().getWorlds().containsKey(split[0])) {
                    var teleport = new Teleport(Universe.get().getWorlds().get(split[0]),
                            new Vector3d(ChunkUtil.minBlock(Integer.parseInt(split[1])) + 15,
                                    150, ChunkUtil.minBlock(Integer.parseInt(split[2])) + 15), new Vector3f());
                    store.putComponent(ref, Teleport.getComponentType(), teleport);
                }
            }
        }
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        super.build(ref, uiCommandBuilder, uiEventBuilder, store);
        uiCommandBuilder.append("Pages/Buuz135_SimpleClaims_PartyChunkList.ui");

        buildList(ref, uiCommandBuilder, uiEventBuilder, store);
    }

    private void buildList(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull ComponentAccessor<EntityStore> store) {
        uiCommandBuilder.clear("#ClaimsCards");
        uiCommandBuilder.appendInline("#Main #ClaimList", "Group #ClaimsCards { LayoutMode: Left; }");
        var i = 0;
        for (String world : ClaimManager.getInstance().getChunks().keySet()) {
            for (ChunkInfo value : ClaimManager.getInstance().getChunks().get(world).values()) {
                if (!value.getPartyOwner().equals(this.partyInfo.getId())) continue;
                uiCommandBuilder.append("#ClaimsCards", "Pages/Buuz135_SimpleClaims_PartyChunkListEntry.ui");
                uiCommandBuilder.set("#ClaimsCards[" + i + "] #ChunkWorldName.Text", world);
                uiCommandBuilder.set("#ClaimsCards[" + i + "] #ChunkPosName.Text", "X: " + (ChunkUtil.minBlock(value.getChunkX()) + 15) + " Z: " + (ChunkUtil.minBlock(value.getChunkZ()) + 15) + "");
                uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ClaimsCards[" + i + "] #UnclaimButton", EventData.of("Action", "Unclaim").append("ChunkId", world + ":" + value.getCoordinates()), false);
                if (isOpEdit) {
                    uiCommandBuilder.set("#ClaimsCards[" + i + "] #TeleportButton.Visible", true);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ClaimsCards[" + i + "] #TeleportButton", EventData.of("Action", "Teleport").append("ChunkId", world + ":" + value.getCoordinates()), false);
                }
                if (this.requestingConfirmation.equals(world + ":" + value.getCoordinates())) {
                    uiCommandBuilder.set("#ClaimsCards[" + i + "] #UnclaimButton.Text", TranslationHelper.rawTextOrEnglish("ui.simpleclaims.common.areYouSure", this.playerRef));
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ClaimsCards[" + i + "] #UnclaimButton", EventData.of("RemoveButtonAction", "Delete").append("ChunkId", world + ":" + value.getCoordinates()), false);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#ClaimsCards[" + i + "] #UnclaimButton", EventData.of("RemoveButtonAction", "Click").append("ChunkId", world + ":" + value.getCoordinates()), false);
                } else {
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ClaimsCards[" + i + "] #UnclaimButton", EventData.of("RemoveButtonAction", "Click").append("ChunkId", world + ":" + value.getCoordinates()), false);
                }
                ++i;
            }
        }
    }


    public static class ChunkListGuiData extends GuiWithParentData {
        static final String KEY_ACTION = "Action";
        static final String KEY_REMOVE_BUTTON_ACTION = "RemoveButtonAction";
        static final String KEY_CHUNK_ID = "ChunkId";


        public static final BuilderCodec<ChunkListGuiData> CODEC = BuilderCodec.<ChunkListGuiData>builder(ChunkListGuiData.class, ChunkListGuiData::new)
                .addField(new KeyedCodec<>(KEY_ACTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.action = s, searchGuiData -> searchGuiData.action)
                .addField(new KeyedCodec<>(GuiWithParentData.BACK_INTERACTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.backInteraction = s, searchGuiData -> searchGuiData.backInteraction)
                .addField(new KeyedCodec<>(KEY_REMOVE_BUTTON_ACTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.removeButtonAction = s, searchGuiData -> searchGuiData.removeButtonAction)
                .addField(new KeyedCodec<>(KEY_CHUNK_ID, Codec.STRING), (searchGuiData, s) -> searchGuiData.chunkId = s, searchGuiData -> searchGuiData.chunkId)
                .build();

        private String action;
        private String removeButtonAction;
        private String chunkId;
    }
}
