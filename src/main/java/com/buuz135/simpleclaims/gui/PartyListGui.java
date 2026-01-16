package com.buuz135.simpleclaims.gui;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.party.PartyOverride;
import com.buuz135.simpleclaims.claim.party.PartyOverrides;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;
import java.util.UUID;

public class PartyListGui extends InteractiveCustomUIPage<PartyListGui.PartyListCodec> {

    private String searchQuery;
    private String requestingConfirmation;

    public PartyListGui(@NonNullDecl PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PartyListCodec.CODEC);
        this.searchQuery = "";
        this.requestingConfirmation = "-1";
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl PartyListCodec data) {
        super.handleDataEvent(ref, store, data);
        if (data.searchQuery != null) {
            this.searchQuery = data.searchQuery;
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            buildList(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, false);
            return;
        }
        if (data.action != null) {
            var split = data.action.split(":");
            if (split[0].equals("Edit")){
                var party = ClaimManager.getInstance().getPartyById(UUID.fromString(split[1]));
                if (party != null){
                    var player = store.getComponent(ref, Player.getComponentType());
                    player.getPageManager().openCustomPage(ref, store, new PartyInfoEditGui(playerRef, party, true));
                }
                return;
            }
            if (split[0].equals("Use")){
                var party = ClaimManager.getInstance().getPartyById(UUID.fromString(split[1]));
                if (party != null){
                    ClaimManager.getInstance().getAdminUsageParty().put(playerRef.getUuid(), UUID.fromString(split[1]));
                    playerRef.sendMessage(Message.join(CommandMessages.NOW_USING_PARTY, Message.raw(party.getName())));
                    return;
                }
            }
            if (split[0].equals("Claim")) {
                var party = ClaimManager.getInstance().getPartyById(UUID.fromString(split[1]));
                if (party != null) {
                    ClaimManager.getInstance().getAdminUsageParty().put(playerRef.getUuid(), UUID.fromString(split[1]));
                    playerRef.sendMessage(Message.join(CommandMessages.NOW_USING_PARTY, Message.raw(party.getName())));
                    var player = store.getComponent(ref, Player.getComponentType());
                    var position = store.getComponent(ref, TransformComponent.getComponentType());
                    player.getPageManager().openCustomPage(ref, store, new ChunkInfoGui(playerRef, player.getWorld().getName(), ChunkUtil.chunkCoordinate(position.getPosition().getX()), ChunkUtil.chunkCoordinate(position.getPosition().getZ()), true));
                    return;
                }
            }
        }
        if (data.removeButtonAction != null) {
            var split = data.removeButtonAction.split(":");
            var action = split[0];
            var index = split[1];
            if (action.equals("Click")) {
                this.requestingConfirmation = index;
            }
            if (action.equals("Delete")) {
                var party = ClaimManager.getInstance().getPartyById(UUID.fromString(index));
                if (party != null) {
                    ClaimManager.getInstance().disbandParty(party);
                    ClaimManager.getInstance().markDirty();
                    playerRef.sendMessage(CommandMessages.PARTY_DISBANDED);
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
        uiCommandBuilder.append("Pages/Buuz135_SimpleClaims_OpPartyList.ui");
        uiCommandBuilder.set("#SearchInput.Value", searchQuery);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#SearchInput", EventData.of("@SearchQuery", "#SearchInput.Value"), false);
        buildList(ref, uiCommandBuilder, uiEventBuilder, store);
    }

    private void buildList(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull ComponentAccessor<EntityStore> store) {
        uiCommandBuilder.clear("#PartyCards");
        uiCommandBuilder.appendInline("#Main #PartyList", "Group #PartyCards { LayoutMode: Left; }");
        var i = 0;
        for (PartyInfo value : ClaimManager.getInstance().getParties().values()) {
            if (!value.getName().toLowerCase().contains(searchQuery.toLowerCase())) continue;
            uiCommandBuilder.append("#PartyCards", "Pages/Buuz135_SimpleClaims_OpPartyListEntry.ui");
            uiCommandBuilder.set("#PartyCards[" + i + "] #PartyName.Text", value.getName());
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PartyCards[" + i + "] #EditPartyButton", EventData.of("Action", "Edit:" + value.getId().toString()), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PartyCards[" + i + "] #ClaimButton", EventData.of("Action", "Claim:" + value.getId().toString()), false);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PartyCards[" + i + "] #UsePartyButton", EventData.of("Action", "Use:" + value.getId().toString()), false);
            if (this.requestingConfirmation.equals(value.getId().toString())) {
                uiCommandBuilder.set("#PartyCards[" + i + "] #RemovePartyButton.Text", "Are you sure?");
                uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PartyCards[" + i + "] #RemovePartyButton", EventData.of("RemoveButtonAction", "Delete:" + value.getId().toString()), false);
                uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#PartyCards[" + i + "] #RemovePartyButton", EventData.of("RemoveButtonAction", "Click:-1"), false);
            } else {
                uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#PartyCards[" + i + "] #RemovePartyButton", EventData.of("RemoveButtonAction", "Click:" + value.getId().toString()), false);
            }
            ++i;
        }
    }

    public static class PartyListCodec {
        static final String KEY_ACTION = "Action";
        static final String KEY_SEARCH_QUERY = "@SearchQuery";
        static final String KEY_REMOVE_BUTTON_ACTION = "RemoveButtonAction";


        public static final BuilderCodec<PartyListCodec> CODEC = BuilderCodec.<PartyListCodec>builder(PartyListCodec.class, PartyListCodec::new)
                .addField(new KeyedCodec<>(KEY_SEARCH_QUERY, Codec.STRING), (searchGuiData, s) -> searchGuiData.searchQuery = s, searchGuiData -> searchGuiData.searchQuery)
                .addField(new KeyedCodec<>(KEY_ACTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.action = s, searchGuiData -> searchGuiData.action)
                .addField(new KeyedCodec<>(KEY_REMOVE_BUTTON_ACTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.removeButtonAction = s, searchGuiData -> searchGuiData.removeButtonAction)

                .build();

        private String action;
        private String searchQuery;
        private String removeButtonAction;

    }
}
