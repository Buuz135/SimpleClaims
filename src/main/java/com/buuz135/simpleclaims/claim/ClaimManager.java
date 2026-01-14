package com.buuz135.simpleclaims.claim;

import com.buuz135.simpleclaims.claim.party.PartyInvite;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.buuz135.simpleclaims.files.ClaimedChunkBlockingFile;
import com.buuz135.simpleclaims.files.PartyBlockingFile;
import com.buuz135.simpleclaims.files.PlayerNameTrackerBlockingFile;
import com.buuz135.simpleclaims.util.FileUtils;
import com.buuz135.simpleclaims.claim.chunk.ChunkInfo;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.player_name.PlayerNameTracker;
import com.buuz135.simpleclaims.claim.tracking.ModifiedTracking;
import com.hypixel.hytale.codec.util.RawJsonReader;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.util.BsonUtil;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ClaimManager {

    private static final ClaimManager INSTANCE = new ClaimManager();

    private HashMap<String, UUID> adminUsageParty;
    private HashMap<String, PartyInvite> partyInvites;
    private List<String> adminClaimOverrides;
    private boolean needsMapUpdate;
    private boolean isDirty;
    private Thread savingThread;
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("SimpleClaims");
    private PlayerNameTrackerBlockingFile playerNameTrackerBlockingFile;
    private PartyBlockingFile partyBlockingFile;
    private ClaimedChunkBlockingFile claimedChunkBlockingFile;

    public static ClaimManager getInstance() {
        return INSTANCE;
    }

    private ClaimManager() {
        this.adminUsageParty = new HashMap<>();
        this.needsMapUpdate = false;
        this.isDirty = false;
        this.partyInvites = new HashMap<>();
        this.adminClaimOverrides = new ArrayList<>();
        this.partyBlockingFile = new PartyBlockingFile();
        this.claimedChunkBlockingFile = new ClaimedChunkBlockingFile();
        this.playerNameTrackerBlockingFile = new PlayerNameTrackerBlockingFile();

        FileUtils.ensureMainDirectory();

        try {
            var partyPath = FileUtils.ensureFile(FileUtils.PARTY_PATH, "{}");
            logger.at(Level.INFO).log("Loading party data...");
            this.partyBlockingFile.syncLoad();
            for (PartyInfo value : this.partyBlockingFile.getParties().values()) {
                System.out.println(value);
            }
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("LOADING PARTY FILE ERROR");
            logger.at(Level.SEVERE).log(e.getMessage());
            e.printStackTrace();
            //throw new RuntimeException(e);
            // TODO Create the file again
        }

        try {
            var claimPath = FileUtils.ensureFile(FileUtils.CLAIM_PATH, "{}");
            this.claimedChunkBlockingFile.syncLoad();

        } catch (Exception e) {
            logger.at(Level.SEVERE).log("LOADING CLAIM FILE ERROR");
            logger.at(Level.SEVERE).log(e.getMessage());
            //throw new RuntimeException(e);
            // TODO Create the file again
        }

        try {
            var nameCacheFile = FileUtils.ensureFile(FileUtils.NAMES_CACHE_PATH, "{}");
            this.playerNameTrackerBlockingFile.syncLoad();
        } catch (Exception e) {
            logger.at(Level.SEVERE).log("LOADING NAME CACHE FILE ERROR");
            logger.at(Level.SEVERE).log(e.getMessage());
            //throw new RuntimeException(e);
            // TODO Create the file again
        }

        try {
            var adminOverridesFile = FileUtils.ensureFile(FileUtils.ADMIN_OVERRIDES_PATH, "{}");
            var loadedOverrides = RawJsonReader.readSync(adminOverridesFile.toPath(), AdminOverridesStorage.CODEC, HytaleLogger.getLogger());
            this.adminClaimOverrides = new ArrayList<>(loadedOverrides.getAdminOverrides());
        } catch (IOException e) {
            logger.at(Level.SEVERE).log("LOADING ADMIN OVERRIDES FILE ERROR");
            logger.at(Level.SEVERE).log(e.getMessage());
        }

        this.savingThread = new Thread(() -> {
            while (true) {
                if (isDirty) {
                    isDirty = false;
                    logger.at(Level.INFO).log("Saving data...");
                    FileUtils.ensureMainDirectory();

                    try {
                        var partyPath = FileUtils.ensureFile(FileUtils.PARTY_PATH, "{}");
                        this.partyBlockingFile.syncSave();
                    } catch (Exception e) {
                        logger.at(Level.SEVERE).log(e.getMessage());
                    }

                    try {
                        var claimPath = FileUtils.ensureFile(FileUtils.CLAIM_PATH, "{}");
                        this.claimedChunkBlockingFile.syncSave();
                    } catch (Exception e) {
                        logger.at(Level.SEVERE).log(e.getMessage());
                    }

                    try {
                        var namesCacheFile = FileUtils.ensureFile(FileUtils.NAMES_CACHE_PATH, "{}");
                        this.playerNameTrackerBlockingFile.syncSave();
                    } catch (Exception e) {
                        logger.at(Level.SEVERE).log(e.getMessage());
                    }

                    try {
                        var adminOverridesFile = FileUtils.ensureFile(FileUtils.ADMIN_OVERRIDES_PATH, "{}");
                        BsonUtil.writeSync(adminOverridesFile.toPath(), AdminOverridesStorage.CODEC, new AdminOverridesStorage(this.adminClaimOverrides), HytaleLogger.getLogger());
                    } catch (IOException e) {
                        logger.at(Level.SEVERE).log(e.getMessage());
                    }

                    logger.at(Level.INFO).log("Finished saving data... Eepy time...");
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    logger.at(Level.SEVERE).log("SAVING THREAD ERROR");
                    logger.at(Level.SEVERE).log(e.getMessage());
                }
            }
        });
        this.savingThread.start();

        markDirty();
    }

    public void markDirty() {
        this.isDirty = true;
        setNeedsMapUpdate(true);
    }

    public void addParty(PartyInfo partyInfo){
        this.partyBlockingFile.getParties().put(partyInfo.getId().toString(), partyInfo);
    }

    public boolean isAllowedToInteract(UUID playerUUID, String dimension, int chunkX, int chunkZ, Predicate<PartyInfo> interactMethod) {
        // Admin Overrides ignores all the checks
        if (adminClaimOverrides.contains(playerUUID.toString())) return true;
        // Check if the chunk is claimed and yf the chunk is not claimed return true
        var chunkInfo = getChunkRawCoords(dimension, chunkX, chunkZ);
        if (chunkInfo == null) return true;

        // Check the chunk party interact restrictions and check if the interact method is allowed
        var chunkParty = getPartyById(chunkInfo.getPartyOwner());
        if (chunkParty == null || interactMethod.test(chunkParty)) return true;

        // Get the player party, if the player doesn't have a party then it can't be a member of the claimed chunk party
        var partyFromPlayer = getPartyFromPlayer(playerUUID);
        if (partyFromPlayer == null) return false;

        // Check if its the same party
        return chunkInfo.getPartyOwner().equals(partyFromPlayer.getId());
    }

    @Nullable
    public PartyInfo getPartyFromPlayer(UUID player){
        return this.partyBlockingFile.getParties().values().stream().filter(partyInfo -> partyInfo.isOwnerOrMember(player)).findFirst().orElse(null);
    }

    @Nullable
    public PartyInfo getPartyById(UUID partyId){
        return this.partyBlockingFile.getParties().get(partyId.toString());
    }

    public PartyInfo createParty(Player owner, PlayerRef playerRef) {
        var party = new PartyInfo(UUID.randomUUID(), playerRef.getUuid(), owner.getDisplayName() + "'s Party", owner.getDisplayName() + "'s Party Description", new UUID[0], Color.getHSBColor(new Random().nextFloat(), 1, 1).getRGB());
        party.addMember(playerRef.getUuid());
        party.setCreatedTracked(new ModifiedTracking(playerRef.getUuid(), owner.getDisplayName(), LocalDateTime.now().toString()));
        party.setModifiedTracked(new ModifiedTracking(playerRef.getUuid(), owner.getDisplayName(), LocalDateTime.now().toString()));
        this.partyBlockingFile.getParties().put(party.getId().toString(), party);
        this.markDirty();
        return party;
    }

    public boolean canClaimInDimension(World world){
        if (world.getWorldConfig().isDeleteOnRemove()) return false;
        if (world.getName().contains("Gaia_Temple")) return false;
        return true;
    }

    @Nullable
    public ChunkInfo getChunk(String dimension, int chunkX, int chunkZ){
        var chunkInfo = this.getChunks().computeIfAbsent(dimension, k -> new HashMap<>());
        var formattedChunk = ChunkInfo.formatCoordinates(chunkX, chunkZ);
        return chunkInfo.getOrDefault(formattedChunk, null);
    }

    @Nullable
    public ChunkInfo getChunkRawCoords(String dimension, int blockX, int blockZ){
        return this.getChunk(dimension, ChunkUtil.chunkCoordinate(blockX), ChunkUtil.chunkCoordinate(blockZ));
    }

    public ChunkInfo claimChunkBy(String dimension, int chunkX, int chunkZ, PartyInfo partyInfo, Player owner, PlayerRef playerRef) {
        var chunkInfo = new ChunkInfo(partyInfo.getId(), chunkX, chunkZ);
        var chunkDimension = this.getChunks().computeIfAbsent(dimension, k -> new HashMap<>());
        chunkDimension.put(ChunkInfo.formatCoordinates(chunkX, chunkZ), chunkInfo);
        chunkInfo.setCreatedTracked(new ModifiedTracking(playerRef.getUuid(), owner.getDisplayName(), LocalDateTime.now().toString()));
        this.markDirty();
        return chunkInfo;
    }

    public ChunkInfo claimChunkByRawCoords(String dimension, int blockX, int blockZ, PartyInfo partyInfo, Player owner, PlayerRef playerRef) {
        return this.claimChunkBy(dimension, ChunkUtil.chunkCoordinate(blockX), ChunkUtil.chunkCoordinate(blockZ), partyInfo, owner, playerRef);
    }

    public boolean hasEnoughClaimsLeft(PartyInfo partyInfo){
        int maxAmount = partyInfo.getMaxClaimAmount();
        int currentAmount = 0;
        for (String dimension : this.getChunks().keySet()) {
            for (ChunkInfo value : this.getChunks().get(dimension).values()) {
                if (value.getPartyOwner().equals(partyInfo.getId())) currentAmount++;
                if (currentAmount >= maxAmount) return false;
            }
        }
        return true;
    }

    public int getAmountOfClaims(PartyInfo partyInfo) {
        int currentAmount = 0;
        for (String dimension : this.getChunks().keySet()) {
            for (ChunkInfo value : this.getChunks().get(dimension).values()) {
                if (value.getPartyOwner().equals(partyInfo.getId())) currentAmount++;

            }
        }
        return currentAmount;
    }

    public void unclaim(String dimension, int chunkX, int chunkZ){
        this.getChunks().computeIfAbsent(dimension, k -> new HashMap<>()).remove(ChunkInfo.formatCoordinates(chunkX, chunkZ));
        this.markDirty();
    }

    public void unclaimRawCoords(String dimension, int blockX, int blockZ){
        this.unclaim(dimension, ChunkUtil.chunkCoordinate(blockX), ChunkUtil.chunkCoordinate(blockZ));
    }

    public boolean needsMapUpdate() {
        return needsMapUpdate;
    }

    public void setNeedsMapUpdate(boolean needsMapUpdate) {
        this.needsMapUpdate = needsMapUpdate;
    }

    public PlayerNameTracker getPlayerNameTracker() {
        return playerNameTrackerBlockingFile.getTracker();
    }

    public HashMap<String, PartyInfo> getParties() {
        return partyBlockingFile.getParties();
    }

    public HashMap<String, HashMap<String, ChunkInfo>> getChunks() {
        return this.claimedChunkBlockingFile.getChunks();
    }

    public HashMap<String, UUID> getAdminUsageParty() {
        return adminUsageParty;
    }

    public void invitePlayerToParty(PlayerRef recipient, PartyInfo partyInfo, PlayerRef sender) {
        this.partyInvites.put(recipient.getUuid().toString(), new PartyInvite(recipient.getUuid(), sender.getUuid(), partyInfo.getId()));
    }

    public PartyInvite acceptInvite(PlayerRef player) {
        var invite = this.partyInvites.get(player.getUuid().toString());
        if (invite == null) return null;
        var party = this.getPartyById(invite.party());
        if (party == null) return null;
        party.addMember(player.getUuid());
        this.partyInvites.remove(player.getUuid().toString());
        return invite;
    }

    public void leaveParty(PlayerRef player, PartyInfo partyInfo) {
        if (partyInfo.isOwner(player.getUuid())) {
            partyInfo.removeMember(player.getUuid());
            if (partyInfo.getMembers().length > 0) {
                partyInfo.setOwner(partyInfo.getMembers()[0]);
                player.sendMessage(CommandMessages.PARTY_OWNER_TRANSFERRED.param("username", this.getPlayerNameTracker().getPlayerName(partyInfo.getMembers()[0])));
            } else {
                disbandParty(partyInfo);
                player.sendMessage(CommandMessages.PARTY_DISBANDED);
            }
        } else {
            partyInfo.removeMember(player.getUuid());
            player.sendMessage(CommandMessages.PARTY_LEFT);
        }
        markDirty();
    }

    public void disbandParty(PartyInfo partyInfo){
        this.getChunks().forEach((dimension, chunkInfos) -> chunkInfos.values().removeIf(chunkInfo -> chunkInfo.getPartyOwner().equals(partyInfo.getId())));
        this.partyBlockingFile.getParties().remove(partyInfo.getId().toString());
        markDirty();
    }

    public List<String> getAdminClaimOverrides() {
        return adminClaimOverrides;
    }
}
