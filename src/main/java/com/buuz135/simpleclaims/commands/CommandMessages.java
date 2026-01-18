package com.buuz135.simpleclaims.commands;

import com.hypixel.hytale.server.core.Message;

import java.awt.*;

public class CommandMessages {

    public static String BASE_PERM = "simpleclaims.";
    public static String ADMIN_PERM = "simpleclaims.admin.";

    public static final Message NOT_IN_A_PARTY = Message.translation("commands.errors.simpleclaims.playerNotInParty").color(Color.RED).bold(true);
    public static final Message IN_A_PARTY = Message.translation("commands.errors.simpleclaims.playerInParty").color(Color.RED).bold(true);

    public static final Message ALREADY_CLAIMED_BY_YOU = Message.translation("commands.errors.simpleclaims.alreadyClaimedByYou").color(Color.RED).bold(true);
    public static final Message ALREADY_CLAIMED_BY_ANOTHER_PLAYER = Message.translation("commands.errors.simpleclaims.alreadyClaimedByAnotherPlayer").color(Color.RED).bold(true);
    public static final Message NOT_CLAIMED = Message.translation("commands.errors.simpleclaims.notClaimed").color(Color.RED).bold(true);
    public static final Message NOT_YOUR_CLAIM = Message.translation("commands.errors.simpleclaims.notYourClaim").color(Color.RED).bold(true);
    public static final Message CANT_CLAIM_IN_THIS_DIMENSION = Message.translation("commands.errors.simpleclaims.cantClaimInThisDimension").color(Color.RED).bold(true);

    public static final Message NOT_ENOUGH_CHUNKS = Message.translation("commands.errors.simpleclaims.notEnoughChunks").color(Color.RED).bold(true);
    public static final Message PLAYER_NOT_FOUND = Message.translation("commands.errors.simpleclaims.playerNotFound").color(Color.RED).bold(true);

    public static final Message PARTY_CREATED = Message.translation("commands.simpleclaims.partyCreated").color(Color.GREEN).bold(true);

    public static final Message UNCLAIMED = Message.translation("commands.info.simpleclaims.unclaimed").color(Color.GREEN).bold(true);
    public static final Message CLAIMED = Message.translation("commands.info.simpleclaims.claimed").color(Color.GREEN).bold(true);

    public static final Message NOW_USING_PARTY = Message.translation("commands.simpleclaims.nowUsingParty").color(Color.GREEN).bold(true);

    public static final Message ADMIN_PARTY_NOT_SELECTED = Message.translation("commands.errors.simpleclaims.admin.partyNotSelected").color(Color.RED).bold(true);
    public static final Message PARTY_NOT_FOUND = Message.translation("commands.errors.simpleclaims.admin.partyNotFound").color(Color.RED).bold(true);

    public static final Message PARTY_OWNER_CHANGED = Message.translation("commands.simpleclaims.admin.partyOwnerChanged");

    public static final Message PARTY_INVITE_SENT = Message.translation("commands.simpleclaims.partyInviteSent").color(Color.GREEN).bold(true);
    public static final Message PARTY_INVITE_RECEIVED = Message.translation("commands.simpleclaims.partyInviteReceived").color(Color.GREEN).bold(true);
    public static final Message PARTY_INVITE_JOIN = Message.translation("commands.simpleclaims.partyInviteJoined").color(Color.GREEN).bold(true);

    public static final Message PARTY_INVITE_SELF = Message.translation("commands.simpleclaims.partyInviteSelf").color(Color.RED).bold(true);
    public static final Message PARTY_LEFT = Message.translation("commands.simpleclaims.partyLeft").color(Color.GREEN).bold(true);
    public static final Message PARTY_DISBANDED = Message.translation("commands.simpleclaims.partyDisbanded").color(Color.GREEN).bold(true);
    public static final Message PARTY_OWNER_TRANSFERRED = Message.translation("commands.simpleclaims.partyOwnerTransferred").color(Color.GREEN).bold(true);

    public static final Message MODIFIED_MAX_CHUNK_AMOUNT = Message.translation("commands.simpleclaims.modifiedMaxChunkAmount").color(Color.GREEN).bold(true);

    public static final Message ENABLED_OVERRIDE = Message.translation("commands.simpleclaims.enabledOverride").color(Color.GREEN).bold(true);
    public static final Message DISABLED_OVERRIDE = Message.translation("commands.simpleclaims.disabledOverride").color(Color.GREEN).bold(true);
    public static final Message PARTY_MEMBER_LIMIT_REACHED = Message.translation("commands.errors.simpleclaims.partyMemberLimitReached").color(Color.RED).bold(true);
    public static final Message PARTY_ALLY_LIMIT_REACHED = Message.translation("commands.errors.simpleclaims.partyAllyLimitReached").color(Color.RED).bold(true);
}
