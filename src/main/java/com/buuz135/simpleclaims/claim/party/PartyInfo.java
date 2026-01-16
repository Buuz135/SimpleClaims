package com.buuz135.simpleclaims.claim.party;

import com.buuz135.simpleclaims.Main;
import com.buuz135.simpleclaims.claim.ClaimManager;
import com.buuz135.simpleclaims.claim.tracking.ModifiedTracking;

import javax.annotation.Nullable;
import java.util.*;

public class PartyInfo {

    private UUID id;
    private UUID owner;
    private String name;
    private String description;
    private final Set<UUID> memberSet;
    private int color;
    private final Map<String, PartyOverride> overrideMap;
    private ModifiedTracking createdTracked;
    private ModifiedTracking modifiedTracked;
    private final Set<UUID> partyAllies;
    private final Set<UUID> playerAllies;

    public PartyInfo(UUID id, UUID owner, String name, String description, UUID[] members, int color) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.memberSet = new HashSet<>();
        this.memberSet.addAll(Arrays.asList(members));
        this.color = color;
        this.overrideMap = new HashMap<>();
        setOverride(new PartyOverride(PartyOverrides.CLAIM_CHUNK_AMOUNT, new PartyOverride.PartyOverrideValue("integer", Main.CONFIG.get().getDefaultPartyClaimsAmount())));
        setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_PLACE_BLOCKS, new PartyOverride.PartyOverrideValue("bool", Main.CONFIG.get().isDefaultPartyBlockPlaceEnabled())));
        setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_BREAK_BLOCKS, new PartyOverride.PartyOverrideValue("bool", Main.CONFIG.get().isDefaultPartyBlockBreakEnabled())));
        setOverride(new PartyOverride(PartyOverrides.PARTY_PROTECTION_INTERACT, new PartyOverride.PartyOverrideValue("bool", Main.CONFIG.get().isDefaultPartyBlockInteractEnabled())));
        this.createdTracked = new ModifiedTracking();
        this.modifiedTracked = new ModifiedTracking();
        this.partyAllies = new HashSet<>();
        this.playerAllies = new HashSet<>();
    }

    public PartyInfo() {
        this(UUID.randomUUID(), UUID.randomUUID(), "", "", new UUID[0], 0);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public UUID[] getMembers() {
        return memberSet.toArray(new UUID[0]);
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMembers(UUID[] members) {
        this.memberSet.clear();
        this.memberSet.addAll(Arrays.asList(members));
    }

    public boolean isOwner(UUID uuid){
        return this.owner.equals(uuid);
    }

    public boolean isMember(UUID uuid){
        return memberSet.contains(uuid);
    }

    public boolean isOwnerOrMember(UUID uuid){
        return isOwner(uuid) || isMember(uuid);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public List<PartyOverride> getOverrides() {
        return new ArrayList<>(overrideMap.values());
    }

    public void addMember(UUID uuid){
        memberSet.add(uuid);
    }

    public void removeMember(UUID uuid){
        memberSet.remove(uuid);
        ClaimManager.getInstance().getPlayerToParty().remove(uuid);
    }

    public int getMaxClaimAmount(){
        var override = this.getOverride(PartyOverrides.CLAIM_CHUNK_AMOUNT);
        if (override != null) {
            return (Integer) override.getValue().getTypedValue();
        }
        return Main.CONFIG.get().getDefaultPartyClaimsAmount();
    }

    public boolean isBlockPlaceEnabled(){
        var override = this.getOverride(PartyOverrides.PARTY_PROTECTION_PLACE_BLOCKS);
        if (override != null) {
            return (Boolean) override.getValue().getTypedValue();
        }
        return Main.CONFIG.get().isDefaultPartyBlockPlaceEnabled();
    }

    public boolean isBlockBreakEnabled(){
        var override = this.getOverride(PartyOverrides.PARTY_PROTECTION_BREAK_BLOCKS);
        if (override != null) {
            return (Boolean) override.getValue().getTypedValue();
        }
        return Main.CONFIG.get().isDefaultPartyBlockBreakEnabled();
    }

    public boolean isBlockInteractEnabled(){
        var override = this.getOverride(PartyOverrides.PARTY_PROTECTION_INTERACT);
        if (override != null) {
            return (Boolean) override.getValue().getTypedValue();
        }
        return Main.CONFIG.get().isDefaultPartyBlockInteractEnabled();
    }

    public boolean isPVPEnabled() {
        var override = this.getOverride(PartyOverrides.PARTY_PROTECTION_PVP);
        if (override != null) {
            return (Boolean) override.getValue().getTypedValue();
        }
        return Main.CONFIG.get().isDefaultPartyPVPEnabled();
    }

    public void setOverride(PartyOverride override){
        if (override.getType().equals(PartyOverrides.CLAIM_CHUNK_AMOUNT)
                && (int) override.getValue().tryGetTypedValue().orElse(0) == Main.CONFIG.get().getDefaultPartyClaimsAmount())
            return;
        overrideMap.put(override.getType(), override);
    }

    public @Nullable PartyOverride getOverride(String type){
        return overrideMap.get(type);
    }

    public ModifiedTracking getCreatedTracked() {
        return createdTracked;
    }

    public void setCreatedTracked(ModifiedTracking createdTracked) {
        this.createdTracked = createdTracked;
    }

    public ModifiedTracking getModifiedTracked() {
        return modifiedTracked;
    }

    public void setModifiedTracked(ModifiedTracking modifiedTracked) {
        this.modifiedTracked = modifiedTracked;
    }

    public Set<UUID> getPartyAllies() {
        return partyAllies;
    }

    public Set<UUID> getPlayerAllies() {
        return playerAllies;
    }

    public void addPartyAllies(UUID uuid) {
        partyAllies.add(uuid);
    }

    public void removePartyAllies(UUID uuid) {
        partyAllies.remove(uuid);
    }

    public void addPlayerAllies(UUID uuid) {
        playerAllies.add(uuid);
    }

    public void removePlayerAllies(UUID uuid) {
        playerAllies.remove(uuid);
    }

    @Override
    public String toString() {
        return "PartyInfo{" +
                "id=" + id +
                ", owner=" + owner +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", members=" + memberSet +
                ", color=" + color +
                ", overrides=" + overrideMap.values() +
                ", createdTracked=" + createdTracked +
                ", modifiedTracked=" + modifiedTracked +
                '}';
    }
}
