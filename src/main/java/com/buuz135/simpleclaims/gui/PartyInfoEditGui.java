package com.buuz135.simpleclaims.gui;

import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.party.PartyInvite;
import com.buuz135.simpleclaims.claim.party.PartyOverride;
import com.buuz135.simpleclaims.claim.party.PartyOverrides;
import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.DropdownEntryInfo;
import com.hypixel.hytale.server.core.ui.LocalizableString;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.worldgen.loader.util.ColorUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyInfoEditGui extends InteractiveCustomUIPage<PartyInfoEditGui.PartyInfoData> {

    private final PartyInfo info;
    private String name;
    private String description;
    private int requestingConfirmation;
    private final boolean isOpEdit;
    private String inviteDropdown;
    private String alliesDropdown;

    public PartyInfoEditGui(@NonNullDecl PlayerRef playerRef, PartyInfo info, boolean isOpEdit) {
        super(playerRef, CustomPageLifetime.CanDismiss, PartyInfoData.CODEC);
        this.info = info;
        this.name = info.getName();
        this.description = info.getDescription();
        this.requestingConfirmation = -1;
        this.isOpEdit = isOpEdit;
    }

    @Override
    public void handleDataEvent(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store, @NonNullDecl PartyInfoData data) {
        super.handleDataEvent(ref, store, data);
        var player = store.getComponent(ref, Player.getComponentType());
        var playerCanModify = this.info.isOwner(playerRef.getUuid()) || this.isOpEdit;
        if (!playerCanModify) {
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.build(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, true);
            return;
        }
        if (data.name != null) {
            this.name = data.name;
        }
        if (data.description != null) {
            this.description = data.description;
        }
        if (data.save != null) {
            this.info.setName(this.name);
            this.info.setDescription(this.description);
            ClaimManager.getInstance().queueMapUpdateForParty(this.info);
            ClaimManager.getInstance().markDirty();
        }
        if (data.cancel != null) {
            this.close();
        }
        if (data.removeButtonAction != null) {
            var split = data.removeButtonAction.split(":");
            var action = split[0];
            if (action.equals("DeleteInvite")) {
                ClaimManager.getInstance().getPartyInvites().remove(UUID.fromString(split[1]));
                ClaimManager.getInstance().markDirty();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.build(ref, commandBuilder, eventBuilder, store);
                this.sendUpdate(commandBuilder, eventBuilder, true);
                return;
            }
            if (action.equals("DeleteAllyPlayer")) {
                this.info.getPlayerAllies().remove(UUID.fromString(split[1]));
                ClaimManager.getInstance().markDirty();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.build(ref, commandBuilder, eventBuilder, store);
                this.sendUpdate(commandBuilder, eventBuilder, true);
                return;
            }
            if (action.equals("DeleteAllyParty")) {
                this.info.getPartyAllies().remove(UUID.fromString(split[1]));
                ClaimManager.getInstance().markDirty();
                UICommandBuilder commandBuilder = new UICommandBuilder();
                UIEventBuilder eventBuilder = new UIEventBuilder();
                this.build(ref, commandBuilder, eventBuilder, store);
                this.sendUpdate(commandBuilder, eventBuilder, true);
                return;
            }
            var index = Integer.parseInt(split[1]);
            if (action.equals("Click")){
                this.requestingConfirmation = index;
            }
            if (action.equals("Delete")){
                this.info.removeMember(this.info.getMembers()[index]);
                ClaimManager.getInstance().markDirty();
            }
            if (action.equals("PlaceBlocksSetting")){
                this.info.setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_PLACE_BLOCKS, new PartyOverride.PartyOverrideValue("bool", !this.info.isBlockPlaceEnabled())));
            }
            if (action.equals("BreakBlocksSetting")){
                this.info.setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_BREAK_BLOCKS, new PartyOverride.PartyOverrideValue("bool", !this.info.isBlockBreakEnabled())));
            }
            if (action.equals("InteractBlocksSetting")){
                this.info.setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_INTERACT, new PartyOverride.PartyOverrideValue("bool", !this.info.isBlockInteractEnabled())));
            }
            if (action.equals("PVPSetting")) {
                this.info.setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_PVP, new PartyOverride.PartyOverrideValue("bool", !this.info.isPVPEnabled())));
            }
            UICommandBuilder commandBuilder = new UICommandBuilder();
            UIEventBuilder eventBuilder = new UIEventBuilder();
            this.build(ref, commandBuilder, eventBuilder, store);
            this.sendUpdate(commandBuilder, eventBuilder, true);
            return;
        }
        if (data.claimColor != null) {
            try {
                this.info.setColor(0xFF000000 | Integer.parseInt(data.claimColor.substring(1,7), 16));
            } catch (NumberFormatException e) {
                System.out.println("Invalid color");
            }
        }
        if (data.inviteDropdown != null) {
            this.inviteDropdown = data.inviteDropdown;
        }
        if (data.alliesDropdown != null) {
            this.alliesDropdown = data.alliesDropdown;
        }
        if (data.button != null) {
            if (data.button.equals("Invite") && this.inviteDropdown != null) {
                if (player.hasPermission(CommandMessages.BASE_PERM + "create-invite")) {
                    if (!this.info.isMember(UUID.fromString(this.inviteDropdown))) {
                        var invited = Universe.get().getPlayer(UUID.fromString(this.inviteDropdown));
                        if (invited != null) {
                            ClaimManager.getInstance().invitePlayerToParty(invited, this.info, this.playerRef);
                            ClaimManager.getInstance().markDirty();
                            invited.sendMessage(CommandMessages.PARTY_INVITE_RECEIVED.param("party_name", this.info.getName()).param("username", this.playerRef.getUsername()));
                            UICommandBuilder commandBuilder = new UICommandBuilder();
                            UIEventBuilder eventBuilder = new UIEventBuilder();
                            this.build(ref, commandBuilder, eventBuilder, store);
                            this.sendUpdate(commandBuilder, eventBuilder, true);
                            return;
                        }
                    }
                } else {
                    playerRef.sendMessage(Message.translation("commands.parsing.error.noPermissionForCommand"));
                }
            }
            if (data.button.equals("Allies") && this.alliesDropdown != null) {
                var invited = Universe.get().getPlayer(UUID.fromString(this.alliesDropdown));
                if (invited != null) { //IS Player
                    this.info.getPlayerAllies().add(invited.getUuid());
                    ClaimManager.getInstance().markDirty();
                    UICommandBuilder commandBuilder = new UICommandBuilder();
                    UIEventBuilder eventBuilder = new UIEventBuilder();
                    this.build(ref, commandBuilder, eventBuilder, store);
                    this.sendUpdate(commandBuilder, eventBuilder, true);
                    return;
                } else {
                    var party = ClaimManager.getInstance().getPartyById(UUID.fromString(this.alliesDropdown));
                    if (party != null) {
                        this.info.getPartyAllies().add(party.getId());
                        ClaimManager.getInstance().markDirty();
                        UICommandBuilder commandBuilder = new UICommandBuilder();
                        UIEventBuilder eventBuilder = new UIEventBuilder();
                        this.build(ref, commandBuilder, eventBuilder, store);
                        this.sendUpdate(commandBuilder, eventBuilder, true);
                        return;
                    }
                }
            }
        }
        this.sendUpdate();
    }

    @Override
    public void build(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl UICommandBuilder uiCommandBuilder, @NonNullDecl UIEventBuilder uiEventBuilder, @NonNullDecl Store<EntityStore> store) {
        var player = store.getComponent(ref, PlayerRef.getComponentType());
        var playerCanModify = this.info.isOwner(player.getUuid()) || this.isOpEdit;
        uiCommandBuilder.append("Pages/Buuz135_SimpleClaims_EditParty.ui");
        uiCommandBuilder.set("#PartyInfo #PartyNameField.Value", this.info.getName());
        uiCommandBuilder.set("#PartyInfo #PartyNameField.IsReadOnly", !playerCanModify);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PartyNameField", EventData.of("@Name", "#PartyNameField.Value"), false);
        uiCommandBuilder.set("#PartyInfo #PartyDescriptionField.Value", this.info.getDescription());
        uiCommandBuilder.set("#PartyInfo #PartyDescriptionField.IsReadOnly", !playerCanModify);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PartyDescriptionField", EventData.of("@Description", "#PartyDescriptionField.Value"), false);
        int i = 0;
        for (; i < this.info.getMembers().length; i++) {
            uiCommandBuilder.append("#MemberEntries", "Pages/Buuz135_SimpleClaims_PartyMemberListEntry.ui");
            var isOwner = this.info.isOwner(this.info.getMembers()[i]);
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberName.Text", ClaimManager.getInstance().getPlayerNameTracker().getPlayerName(this.info.getMembers()[i]));
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Text", isOwner ? "Owner" : "Member");
            if (!isOwner) {
                uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Background.Color", "#1a8dec83");
                uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.OutlineColor", "#1a8decde");
            }
            if (!playerCanModify || isOwner){
                uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Disabled", true);
            } else {
                //uiEventBuilder.addEventBinding(CustomUIEventBindingType.SlotMouseExited, "#Members[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Left:" + i), false);
                if (this.requestingConfirmation == i) {
                    uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Text", "Are you sure?");
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Delete:" + i), false);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:-1"), false);
                } else {
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:" + i), false);
                }
            }
        }
        for (PartyInvite value : ClaimManager.getInstance().getPartyInvites().values()) {
            uiCommandBuilder.append("#MemberEntries", "Pages/Buuz135_SimpleClaims_PartyMemberListEntry.ui");
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberName.Text", ClaimManager.getInstance().getPlayerNameTracker().getPlayerName(value.recipient()));
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Text", "Pending Invite");

            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Background.Color", "#cac85383");
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.OutlineColor", "#cac853de");

            if (!playerCanModify) {
                uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Disabled", true);
            } else {
                //uiEventBuilder.addEventBinding(CustomUIEventBindingType.SlotMouseExited, "#Members[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Left:" + i), false);
                if (this.requestingConfirmation == i) {
                    uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Text", "Are you sure?");
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "DeleteInvite:" + value.recipient().toString()), false);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:-1"), false);
                } else {
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:" + i), false);
                }
            }

            ++i;
        }

        for (UUID uuid : this.info.getPlayerAllies()) {
            uiCommandBuilder.append("#MemberEntries", "Pages/Buuz135_SimpleClaims_PartyMemberListEntry.ui");
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberName.Text", ClaimManager.getInstance().getPlayerNameTracker().getPlayerName(uuid));
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Text", "  Ally  ");

            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Background.Color", "#5ab44e83");
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.OutlineColor", "#5ab44ede");

            if (!playerCanModify) {
                uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Disabled", true);
            } else {
                //uiEventBuilder.addEventBinding(CustomUIEventBindingType.SlotMouseExited, "#Members[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Left:" + i), false);
                if (this.requestingConfirmation == i) {
                    uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Text", "Are you sure?");
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "DeleteAllyPlayer:" + uuid.toString()), false);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:-1"), false);
                } else {
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:" + i), false);
                }
            }

            ++i;
        }

        for (UUID uuid : this.info.getPartyAllies()) {
            uiCommandBuilder.append("#MemberEntries", "Pages/Buuz135_SimpleClaims_PartyMemberListEntry.ui");
            var name = "Unknown Party";
            if (ClaimManager.getInstance().getParties().containsKey(uuid.toString())) {
                name = ClaimManager.getInstance().getParties().get(uuid.toString()).getName();
            }
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberName.Text", name);
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Text", "Party Ally");

            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.Background.Color", "#5ab44e83");
            uiCommandBuilder.set("#MemberEntries[" + i + "] #MemberRole.OutlineColor", "#5ab44ede");

            if (!playerCanModify) {
                uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Disabled", true);
            } else {
                //uiEventBuilder.addEventBinding(CustomUIEventBindingType.SlotMouseExited, "#Members[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Left:" + i), false);
                if (this.requestingConfirmation == i) {
                    uiCommandBuilder.set("#MemberEntries[" + i + "] #RemoveMemberButton.Text", "Are you sure?");
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "DeleteAllyParty:" + uuid.toString()), false);
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.MouseExited, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:-1"), false);
                } else {
                    uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#MemberEntries[" + i + "] #RemoveMemberButton", EventData.of("RemoveButtonAction", "Click:" + i), false);
                }
            }

            ++i;
        }

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#SaveChangesButton", EventData.of("Save", "true"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#CancelButton", EventData.of("Cancel", "true"), false);

        uiCommandBuilder.set("#PlaceBlocksSetting #CheckBox.Value", this.info.isBlockPlaceEnabled());
        if (!isOpEdit)
            uiCommandBuilder.set("#PlaceBlocksSetting #CheckBox.Disabled", !playerCanModify || !Main.CONFIG.get().isAllowPartyBreakBlockSetting());
        uiCommandBuilder.set("#BreakBlocksSetting #CheckBox.Value", this.info.isBlockBreakEnabled());
        if (!isOpEdit)
            uiCommandBuilder.set("#BreakBlocksSetting #CheckBox.Disabled", !playerCanModify || !Main.CONFIG.get().isAllowPartyPlaceBlockSetting());
        uiCommandBuilder.set("#InteractBlocksSetting #CheckBox.Value",this.info.isBlockInteractEnabled());
        if (!isOpEdit)
            uiCommandBuilder.set("#InteractBlocksSetting #CheckBox.Disabled", !playerCanModify || !Main.CONFIG.get().isAllowPartyInteractBlockSetting());
        uiCommandBuilder.set("#PVPSetting #CheckBox.Value", this.info.isPVPEnabled());
        if (!isOpEdit)
            uiCommandBuilder.set("#PVPSetting #CheckBox.Disabled", !playerCanModify || !Main.CONFIG.get().isAllowPartyPVPSetting());

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PlaceBlocksSetting #CheckBox", EventData.of("RemoveButtonAction", "PlaceBlocksSetting:0"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#BreakBlocksSetting #CheckBox", EventData.of("RemoveButtonAction", "BreakBlocksSetting:0"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#InteractBlocksSetting #CheckBox", EventData.of("RemoveButtonAction", "InteractBlocksSetting:0"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#PVPSetting #CheckBox", EventData.of("RemoveButtonAction", "PVPSetting:0"), false);


        uiCommandBuilder.set("#ClaimColorPickerGroup #ClaimColorPicker.Value", String.format("#%06X", (0xFFFFFF & this.info.getColor())));
        //uiCommandBuilder.set("#ClaimColorPickerGroup #ClaimColorPicker.IsReadOnly", !playerCanModify);

        uiCommandBuilder.set("#ClaimedChunksInfo #ClaimedChunksCount.Text", ClaimManager.getInstance().getAmountOfClaims(this.info)+ "");
        uiCommandBuilder.set("#ClaimedChunksInfo #MaxChunksCount.Text",this.info.getMaxClaimAmount() + "");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#ClaimColorPicker", EventData.of("@ClaimColor", "#ClaimColorPicker.Value"), false);

        //Invite Dropdowns
        var players = new ArrayList<>(Universe.get().getPlayers().stream().filter(playerRef1 -> ClaimManager.getInstance().getPartyFromPlayer(playerRef1.getUuid()) == null)
                .map(playerRef1 -> new DropdownEntryInfo(LocalizableString.fromString(playerRef1.getUsername()), playerRef1.getUuid().toString())).toList());
        uiCommandBuilder.set("#InviteDropdown.Entries", players);
        players = new ArrayList<>(Universe.get().getPlayers().stream().map(playerRef1 -> new DropdownEntryInfo(LocalizableString.fromString(playerRef1.getUsername()), playerRef1.getUuid().toString())).toList());
        var parties = ClaimManager.getInstance().getParties().values().stream().map(party -> new DropdownEntryInfo(LocalizableString.fromString("[PAR] " + party.getName()), party.getId().toString())).toList();
        players.addAll(parties);
        uiCommandBuilder.set("#AlliesDropdown.Entries", players);

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#InviteDropdown", EventData.of("@InviteDropdown", "#InviteDropdown.Value"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.ValueChanged, "#AlliesDropdown", EventData.of("@AlliesDropdown", "#AlliesDropdown.Value"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmInviteButton", EventData.of("Button", "Invite"), false);
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmAlliesButton", EventData.of("Button", "Allies"), false);

        uiCommandBuilder.set("#ConfirmInviteButton.Disabled", !playerCanModify);
        uiCommandBuilder.set("#ConfirmAlliesButton.Disabled", !playerCanModify);
    }

    public static class PartyInfoData {
        static final String KEY_NAME = "@Name";
        static final String KEY_DESCRIPTION = "@Description";
        static final String KEY_SAVE = "Save";
        static final String KEY_CANCEL = "Cancel";
        static final String KEY_REMOVE_BUTTON_ACTION = "RemoveButtonAction";
        static final String KEY_CLAIM_COLOR = "@ClaimColor";
        static final String KEY_INVITE_DROPDOWN = "@InviteDropdown";
        static final String KEY_ALLIES_DROPDOWN = "@AlliesDropdown";
        static final String KEY_BUTTON = "Button";

        public static final BuilderCodec<PartyInfoData> CODEC = BuilderCodec.<PartyInfoData>builder(PartyInfoData.class, PartyInfoData::new)
                .addField(new KeyedCodec<>(KEY_NAME, Codec.STRING), (searchGuiData, s) -> searchGuiData.name = s, searchGuiData -> searchGuiData.name)
                .addField(new KeyedCodec<>(KEY_DESCRIPTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.description = s, searchGuiData -> searchGuiData.description)
                .addField(new KeyedCodec<>(KEY_SAVE, Codec.STRING), (searchGuiData, s) -> searchGuiData.save = s, searchGuiData -> searchGuiData.save)
                .addField(new KeyedCodec<>(KEY_CANCEL, Codec.STRING), (searchGuiData, s) -> searchGuiData.cancel = s, searchGuiData -> searchGuiData.cancel)
                .addField(new KeyedCodec<>(KEY_REMOVE_BUTTON_ACTION, Codec.STRING), (searchGuiData, s) -> searchGuiData.removeButtonAction = s, searchGuiData -> searchGuiData.removeButtonAction)
                .addField(new KeyedCodec<>(KEY_CLAIM_COLOR, Codec.STRING), (searchGuiData, s) -> searchGuiData.claimColor = s, searchGuiData -> searchGuiData.claimColor)
                .addField(new KeyedCodec<>(KEY_INVITE_DROPDOWN, Codec.STRING), (searchGuiData, s) -> searchGuiData.inviteDropdown = s, searchGuiData -> searchGuiData.inviteDropdown)
                .addField(new KeyedCodec<>(KEY_ALLIES_DROPDOWN, Codec.STRING), (searchGuiData, s) -> searchGuiData.alliesDropdown = s, searchGuiData -> searchGuiData.alliesDropdown)
                .addField(new KeyedCodec<>(KEY_BUTTON, Codec.STRING), (searchGuiData, s) -> searchGuiData.button = s, searchGuiData -> searchGuiData.button)
                .build();

        private String name;
        private String description;
        private String save;
        private String cancel;
        private String removeButtonAction;
        private String claimColor;
        private String inviteDropdown;
        private String alliesDropdown;
        private String button;

        @Override
        public String toString() {
            return "PartyInfoData{" +
                    "name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", save='" + save + '\'' +
                    ", cancel='" + cancel + '\'' +
                    ", removeButtonAction='" + removeButtonAction + '\'' +
                    ", claimColor='" + claimColor + '\'' +
                    '}';
        }
    }
}
