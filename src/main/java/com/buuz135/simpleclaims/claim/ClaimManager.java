package com.buuz135.simpleclaims.claim;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.party.PartyInvite;
import com.buuz135.simpleclaims.commands.CommandMessages;
import com.buuz135.simpleclaims.files.AdminOverridesBlockingFile;
import com.buuz135.simpleclaims.files.ClaimedChunkBlockingFile;
import com.buuz135.simpleclaims.files.PartyBlockingFile;
import com.buuz135.simpleclaims.files.PlayerNameTrackerBlockingFile;
import com.buuz135.simpleclaims.util.FileUtils;
import com.buuz135.simpleclaims.claim.chunk.ChunkInfo;
import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.player_name.PlayerNameTracker;
import com.buuz135.simpleclaims.claim.tracking.ModifiedTracking;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

import javax.annotation.Nullable;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Level;

public class ClaimManager {

    private static final ClaimManager INSTANCE = new ClaimManager();

    private final Map<UUID, UUID> adminUsageParty;
    private final Map<UUID, PartyInvite> partyInvites;
    private final Map<UUID, UUID> playerToParty;
    private final Map<UUID, Integer> partyClaimCounts;
    private Set<String> worldsNeedingUpdates;
    private boolean isDirty;
    private Thread savingThread;
    private HytaleLogger logger = HytaleLogger.getLogger().getSubLogger("SimpleClaims");
    private PlayerNameTrackerBlockingFile playerNameTrackerBlockingFile;
    private PartyBlockingFile partyBlockingFile;
    private ClaimedChunkBlockingFile claimedChunkBlockingFile;
    private AdminOverridesBlockingFile adminOverridesBlockingFile;
    private HashMap<String, LongSet> mapUpdateQueue;

    public static ClaimManager getInstance() {
        return INSTANCE;
    }

    private ClaimManager() {
        this.adminUsageParty = new ConcurrentHashMap<>();
        this.worldsNeedingUpdates = new HashSet<>();
        this.isDirty = false;
        this.partyInvites = new ConcurrentHashMap<>();
        this.playerToParty = new ConcurrentHashMap<>();
        this.partyClaimCounts = new ConcurrentHashMap<>();
        this.partyBlockingFile = new PartyBlockingFile();
        this.claimedChunkBlockingFile = new ClaimedChunkBlockingFile();
        this.playerNameTrackerBlockingFile = new PlayerNameTrackerBlockingFile();
        this.adminOverridesBlockingFile = new AdminOverridesBlockingFile();
        this.mapUpdateQueue = new HashMap<>();

        FileUtils.ensureMainDirectory();

        try {
            var partyPath = FileUtils.ensureFile(FileUtils.PARTY_PATH, "{}");
            logger.at(Level.INFO).log("Loading party data...");
            this.partyBlockingFile.syncLoad();
            for (PartyInfo party : this.partyBlockingFile.getParties().values()) {
                for (UUID member : party.getMembers()) {
                    playerToParty.put(member, party.getId());
                }
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
            for (HashMap<String, ChunkInfo> dimensionChunks : this.claimedChunkBlockingFile.getChunks().values()) {
                for (ChunkInfo chunk : dimensionChunks.values()) {
                    partyClaimCounts.merge(chunk.getPartyOwner(), 1, Integer::sum);
                }
            }
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
            this.adminOverridesBlockingFile.syncLoad();
        } catch (Exception e) {
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
                        this.adminOverridesBlockingFile.syncSave();
                    } catch (Exception e) {
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
    }

    public void addParty(PartyInfo partyInfo){
        this.partyBlockingFile.getParties().put(partyInfo.getId().toString(), partyInfo);
    }

    public boolean isAllowedToInteract(UUID playerUUID, String dimension, int chunkX, int chunkZ, Predicate<PartyInfo> interactMethod) {
        if (adminOverridesBlockingFile.getAdminOverrides().contains(playerUUID)) return true;

        var chunkInfo = getChunkRawCoords(dimension, chunkX, chunkZ);
        if (chunkInfo == null) return true;

        var chunkParty = getPartyById(chunkInfo.getPartyOwner());
        if (chunkParty == null || interactMethod.test(chunkParty)) return true;

        if (chunkParty.getPlayerAllies().contains(playerUUID)) return true;

        var partyId = playerToParty.get(playerUUID);
        if (partyId == null) return false;

        return chunkInfo.getPartyOwner().equals(partyId) || chunkParty.getPartyAllies().contains(partyId);
    }

    @Nullable
    public PartyInfo getPartyFromPlayer(UUID player) {
        UUID partyId = playerToParty.get(player);
        if (partyId == null) return null;
        return getPartyById(partyId);
    }

    @Nullable
    public PartyInfo getPartyById(UUID partyId){
        return this.partyBlockingFile.getParties().get(partyId.toString());
    }

    public PartyInfo createParty(Player owner, PlayerRef playerRef, boolean isAdmin) {
        var party = new PartyInfo(UUID.randomUUID(), playerRef.getUuid(), owner.getDisplayName() + "'s Party", owner.getDisplayName() + "'s Party Description", new UUID[0], Color.getHSBColor(new Random().nextFloat(), 1, 1).getRGB());
        party.addMember(playerRef.getUuid());
        party.setCreatedTracked(new ModifiedTracking(playerRef.getUuid(), owner.getDisplayName(), LocalDateTime.now().toString()));
        party.setModifiedTracked(new ModifiedTracking(playerRef.getUuid(), owner.getDisplayName(), LocalDateTime.now().toString()));
        this.partyBlockingFile.getParties().put(party.getId().toString(), party);
        if (!isAdmin) this.playerToParty.put(playerRef.getUuid(), party.getId());
        this.markDirty();
        return party;
    }

    public boolean canClaimInDimension(World world){
        if (world.getWorldConfig().isDeleteOnRemove()) return false;
        if (world.getName().contains("Gaia_Temple")) return false;
        if (Arrays.asList(Main.CONFIG.get().getWorldNameBlacklistForClaiming()).contains(world.getName())) return false;
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
        partyClaimCounts.merge(partyInfo.getId(), 1, Integer::sum);
        this.markDirty();
        return chunkInfo;
    }

    public ChunkInfo claimChunkByRawCoords(String dimension, int blockX, int blockZ, PartyInfo partyInfo, Player owner, PlayerRef playerRef) {
        return this.claimChunkBy(dimension, ChunkUtil.chunkCoordinate(blockX), ChunkUtil.chunkCoordinate(blockZ), partyInfo, owner, playerRef);
    }

    public boolean hasEnoughClaimsLeft(PartyInfo partyInfo) {
        int maxAmount = partyInfo.getMaxClaimAmount();
        int currentAmount = partyClaimCounts.getOrDefault(partyInfo.getId(), 0);
        return currentAmount < maxAmount;
    }

    public int getAmountOfClaims(PartyInfo partyInfo) {
        return partyClaimCounts.getOrDefault(partyInfo.getId(), 0);
    }

    public void unclaim(String dimension, int chunkX, int chunkZ) {
        var chunkMap = this.getChunks().get(dimension);
        if (chunkMap != null) {
            ChunkInfo removed = chunkMap.remove(ChunkInfo.formatCoordinates(chunkX, chunkZ));
            if (removed != null) {
                partyClaimCounts.computeIfPresent(removed.getPartyOwner(), (k, v) -> v > 1 ? v - 1 : null);
            }
        }
        this.markDirty();
    }

    public void unclaimRawCoords(String dimension, int blockX, int blockZ){
        this.unclaim(dimension, ChunkUtil.chunkCoordinate(blockX), ChunkUtil.chunkCoordinate(blockZ));
    }

    public Set<String> getWorldsNeedingUpdates() {
        return worldsNeedingUpdates;
    }

    public void setNeedsMapUpdate(String world) {
        this.worldsNeedingUpdates.add(world);
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

    public Map<UUID, UUID> getAdminUsageParty() {
        return adminUsageParty;
    }

    public void invitePlayerToParty(PlayerRef recipient, PartyInfo partyInfo, PlayerRef sender) {
        this.partyInvites.put(recipient.getUuid(), new PartyInvite(recipient.getUuid(), sender.getUuid(), partyInfo.getId()));
    }

    public PartyInvite acceptInvite(PlayerRef player) {
        var invite = this.partyInvites.get(player.getUuid());
        if (invite == null) return null;
        var party = this.getPartyById(invite.party());
        if (party == null) return null;
        party.addMember(player.getUuid());
        this.playerToParty.put(player.getUuid(), party.getId());
        this.partyInvites.remove(player.getUuid());
        return invite;
    }

    public Map<UUID, PartyInvite> getPartyInvites() {
        return partyInvites;
    }

    public void leaveParty(PlayerRef player, PartyInfo partyInfo) {
        this.playerToParty.remove(player.getUuid());

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

    public void disbandParty(PartyInfo partyInfo) {
        for (UUID member : partyInfo.getMembers()) {
            playerToParty.remove(member);
        }
        queueMapUpdateForParty(partyInfo);
        this.getChunks().forEach((dimension, chunkInfos) -> chunkInfos.values().removeIf(chunkInfo -> chunkInfo.getPartyOwner().equals(partyInfo.getId())));
        partyClaimCounts.remove(partyInfo.getId());

        this.partyBlockingFile.getParties().remove(partyInfo.getId().toString());
        markDirty();
    }

    public Set<UUID> getAdminClaimOverrides() {
        return adminOverridesBlockingFile.getAdminOverrides();
    }

    public void queueMapUpdateForParty(PartyInfo partyInfo) {
        this.getChunks().forEach((dimension, chunkInfos) -> {
            var world = Main.WORLDS.get(dimension);
            if (world != null) {
                for (ChunkInfo value : chunkInfos.values()) {
                    if (value.getPartyOwner().equals(partyInfo.getId())) {
                        queueMapUpdate(world, value.getChunkX(), value.getChunkZ());
                    }
                }
            }
        });
    }

    public void queueMapUpdate(World world, int chunkX, int chunkZ) {
        if (!mapUpdateQueue.containsKey(world.getName())) {
            mapUpdateQueue.put(world.getName(), new LongOpenHashSet());
        }
        mapUpdateQueue.get(world.getName()).add(ChunkUtil.indexChunk(chunkX, chunkZ));
        mapUpdateQueue.get(world.getName()).add(ChunkUtil.indexChunk(chunkX + 1, chunkZ));
        mapUpdateQueue.get(world.getName()).add(ChunkUtil.indexChunk(chunkX - 1, chunkZ));
        mapUpdateQueue.get(world.getName()).add(ChunkUtil.indexChunk(chunkX, chunkZ + 1));
        mapUpdateQueue.get(world.getName()).add(ChunkUtil.indexChunk(chunkX, chunkZ - 1));
        this.setNeedsMapUpdate(world.getName());
    }

    public HashMap<String, LongSet> getMapUpdateQueue() {
        return mapUpdateQueue;
    }

    public Map<UUID, UUID> getPlayerToParty() {
        return playerToParty;
    }
}
