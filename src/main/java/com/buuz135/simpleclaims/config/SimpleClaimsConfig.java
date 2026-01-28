package com.buuz135.simpleclaims.config;


public class SimpleClaimsConfig {


    private String[] PartyCommandAliases = new String[]{"scp", "sc-party", "party"};
    private int DefaultPartyClaimsAmount = 25;
    private int MaxPartyMembers = -1;
    private int MaxPartyAllies = -1;
    private int PartyInactivityHours = -1;
    private boolean NotifyPartyChatToggling = true;
    private boolean DefaultPartyBlockPlaceEnabled = false;
    private boolean DefaultPartyBlockBreakEnabled = false;
    private boolean DefaultPartyBlockInteractEnabled = false;
    private boolean DefaultPartyPVPEnabled = false;
    private boolean DefaultPartyFriendlyFireEnabled = false;
    private boolean DefaultPartyAllowEntry = true;
    private boolean DefaultPartyInteractChest = false;
    private boolean DefaultPartyInteractDoor = false;
    private boolean DefaultPartyInteractBench = false;
    private boolean DefaultPartyInteractChair = false;
    private boolean DefaultPartyInteractPortal = false;

    private boolean AllowPartyPVPSetting = true;
    private boolean AllowPartyFriendlyFireSetting = true;
    private boolean AllowPartyPlaceBlockSetting = true;
    private boolean AllowPartyBreakBlockSetting = true;
    private boolean AllowPartyInteractBlockSetting = true;
    private boolean AllowPartyAllowEntrySetting = true;
    private boolean AllowPartyInteractChestSetting = true;
    private boolean AllowPartyInteractDoorSetting = true;
    private boolean AllowPartyInteractBenchSetting = true;
    private boolean AllowPartyInteractChairSetting = true;
    private boolean AllowPartyInteractPortalSetting = true;

    private String[] ClaimCommandAliases = new String[]{"sc", "sc-chunks", "scc", "chunk", "chunks", "claim", "claims"};
    private String[] WorldNameBlacklistForClaiming = new String[0];
    private String TitleTopClaimTitleText = "Simple Claims";
    private String[] FullWorldProtection = new String[0];
    private boolean EnableAlloyEntryTesting = false;
    private boolean EnableParticleBorders = true;
    private boolean RenderClaimNamesOnWorldMap = false;
    private boolean RenderMapInClaimUI = true;

    private boolean ForceSimpleClaimsChunkWorldMap = true;
    private boolean CreativeModeBypassProtection = false;

    private String[] BlocksThatIgnoreInteractRestrictions = new String[]{"gravestone"};

    public SimpleClaimsConfig() {

    }

    public String[] getPartyCommandAliases() {
        return PartyCommandAliases;
    }

    public int getDefaultPartyClaimsAmount() {
        return DefaultPartyClaimsAmount;
    }

    public boolean isDefaultPartyBlockPlaceEnabled() {
        return DefaultPartyBlockPlaceEnabled;
    }

    public boolean isDefaultPartyBlockBreakEnabled() {
        return DefaultPartyBlockBreakEnabled;
    }

    public boolean isDefaultPartyBlockInteractEnabled() {
        return DefaultPartyBlockInteractEnabled;
    }

    public boolean isForceSimpleClaimsChunkWorldMap() {
        return ForceSimpleClaimsChunkWorldMap;
    }

    public boolean isCreativeModeBypassProtection() {
        return CreativeModeBypassProtection;
    }

    public boolean isDefaultPartyPVPEnabled() {
        return DefaultPartyPVPEnabled;
    }

    public boolean isDefaultPartyFriendlyFireEnabled() {
        return DefaultPartyFriendlyFireEnabled;
    }

    public boolean isAllowPartyPVPSetting() {
        return AllowPartyPVPSetting;
    }

    public boolean isAllowPartyFriendlyFireSetting() {
        return AllowPartyFriendlyFireSetting;
    }

    public String[] getClaimCommandAliases() {
        return ClaimCommandAliases;
    }

    public String[] getWorldNameBlacklistForClaiming() {
        return WorldNameBlacklistForClaiming;
    }

    public boolean isAllowPartyPlaceBlockSetting() {
        return AllowPartyPlaceBlockSetting;
    }

    public boolean isAllowPartyBreakBlockSetting() {
        return AllowPartyBreakBlockSetting;
    }

    public boolean isAllowPartyInteractBlockSetting() {
        return AllowPartyInteractBlockSetting;
    }

    public String getTitleTopClaimTitleText() {
        return TitleTopClaimTitleText;
    }

    public String[] getFullWorldProtection() {
        return FullWorldProtection;
    }

    public boolean isDefaultPartyAllowEntry() {
        return DefaultPartyAllowEntry;
    }

    public boolean isAllowPartyAllowEntrySetting() {
        return AllowPartyAllowEntrySetting;
    }

    public boolean isEnableAlloyEntryTesting() {
        return EnableAlloyEntryTesting;
    }

    public boolean isDefaultPartyInteractChest() {
        return DefaultPartyInteractChest;
    }

    public boolean isDefaultPartyInteractDoor() {
        return DefaultPartyInteractDoor;
    }

    public boolean isDefaultPartyInteractBench() {
        return DefaultPartyInteractBench;
    }

    public boolean isDefaultPartyInteractChair() {
        return DefaultPartyInteractChair;
    }

    public boolean isAllowPartyInteractChestSetting() {
        return AllowPartyInteractChestSetting;
    }

    public boolean isAllowPartyInteractDoorSetting() {
        return AllowPartyInteractDoorSetting;
    }

    public boolean isAllowPartyInteractBenchSetting() {
        return AllowPartyInteractBenchSetting;
    }

    public boolean isAllowPartyInteractChairSetting() {
        return AllowPartyInteractChairSetting;
    }

    public boolean isDefaultPartyInteractPortal() {
        return DefaultPartyInteractPortal;
    }

    public boolean isAllowPartyInteractPortalSetting() {
        return AllowPartyInteractPortalSetting;
    }

    public boolean isEnableParticleBorders() {
        return EnableParticleBorders;
    }

    public String[] getBlocksThatIgnoreInteractRestrictions() {
        return BlocksThatIgnoreInteractRestrictions;
    }

    public int getMaxPartyMembers() {
        return MaxPartyMembers;
    }

    public int getMaxPartyAllies() {
        return MaxPartyAllies;
    }

    public int getPartyInactivityHours() {
        return PartyInactivityHours;
    }

    public boolean isRenderClaimNamesOnWorldMap() {
        return RenderClaimNamesOnWorldMap;
    }

    public boolean isRenderMapInClaimUI() {
        return RenderMapInClaimUI;
    }

    public boolean isNotifyPartyChatToggling() {
        return NotifyPartyChatToggling;
    }

}
