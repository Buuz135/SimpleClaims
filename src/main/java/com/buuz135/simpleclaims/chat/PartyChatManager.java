package com.buuz135.simpleclaims.chat;

import com.buuz135.simpleclaims.claim.ClaimManager;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PartyChatManager {

    private static final PartyChatManager INSTANCE = new PartyChatManager();

    public static PartyChatManager getInstance() {
        return INSTANCE;
    }

    private final Set<String> partyChatToggledPlayers;

    private PartyChatManager() {
        this.partyChatToggledPlayers = ConcurrentHashMap.newKeySet();
    }

    /**
     * Toggles the party chat for given player.
     * @param playerUUID Player's UUID
     * @return result of the toggling
     */
    public ToggleResult togglePartyChat(final UUID playerUUID) {
        final var playerParty = ClaimManager.getInstance().getPartyFromPlayer(playerUUID);
        if (playerParty == null) {
            return ToggleResult.NOT_IN_A_PARTY;
        }

        if (this.partyChatToggledPlayers.contains(playerUUID.toString())) {
            this.partyChatToggledPlayers.remove(playerUUID.toString());
            return ToggleResult.DEACTIVATED;
        } else {
            this.partyChatToggledPlayers.add(playerUUID.toString());
            return ToggleResult.ACTIVATED;
        }
    }

    /**
     * Is the player toggled on the party chat?
     * @param playerUUID uuid of the player
     * @return true if player toggled on the party chat
     */
    public boolean isPlayerToggledPartyChat(final UUID playerUUID) {
        return this.partyChatToggledPlayers.contains(playerUUID.toString());
    }

    public enum ToggleResult {
        ACTIVATED, DEACTIVATED, NOT_IN_A_PARTY
    }

}
