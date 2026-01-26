package com.buuz135.simpleclaims.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class SimpleClaimsConfig {

    public static final BuilderCodec<SimpleClaimsConfig> CODEC = BuilderCodec.builder(SimpleClaimsConfig.class, SimpleClaimsConfig::new)
            .append(new KeyedCodec<String[]>("PartyCommandAliases", Codec.STRING_ARRAY),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.PartyCommandAliases = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.PartyCommandAliases).add()
            .append(new KeyedCodec<Integer>("DefaultPartyClaimsAmount", Codec.INTEGER),
                    (simpleClaimsConfig, aDouble, extraInfo) -> simpleClaimsConfig.DefaultPartyClaimsAmount = aDouble,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyClaimsAmount).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyBlockPlaceEnabled", Codec.BOOLEAN),
                    (simpleClaimsConfig, aDouble, extraInfo) -> simpleClaimsConfig.DefaultPartyBlockPlaceEnabled = aDouble,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyBlockPlaceEnabled).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyBlockBreakEnabled", Codec.BOOLEAN),
                    (simpleClaimsConfig, aDouble, extraInfo) -> simpleClaimsConfig.DefaultPartyBlockBreakEnabled = aDouble,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyBlockBreakEnabled).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyBlockInteractEnabled", Codec.BOOLEAN),
                    (simpleClaimsConfig, aDouble, extraInfo) -> simpleClaimsConfig.DefaultPartyBlockInteractEnabled = aDouble,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyBlockInteractEnabled).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyPVPEnabled", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyPVPEnabled = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyPVPEnabled).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyFriendlyFireEnabled", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyFriendlyFireEnabled = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyFriendlyFireEnabled).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyAllowEntry", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyAllowEntry = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyAllowEntry).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyInteractChest", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractChest = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractChest).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyInteractDoor", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractDoor = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractDoor).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyInteractBench", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractBench = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractBench).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyInteractChair", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractChair = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractChair).add()
            .append(new KeyedCodec<Boolean>("DefaultPartyInteractPortal", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractPortal = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.DefaultPartyInteractPortal).add()

            .append(new KeyedCodec<Boolean>("AllowPartyPVPSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyPVPSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyPVPSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyFriendlyFireSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyFriendlyFireSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyFriendlyFireSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyPlaceBlockSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyPlaceBlockSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyPlaceBlockSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyBreakBlockSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyBreakBlockSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyBreakBlockSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyInteractBlockSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyInteractBlockSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyInteractBlockSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyAllowEntrySettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyAllowEntrySetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyAllowEntrySetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyInteractChestSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyInteractChestSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyInteractChestSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyInteractDoorSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyInteractDoorSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyInteractDoorSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyInteractBechSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyInteractBenchSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyInteractBenchSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyInteractChairSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyInteractChairSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyInteractChairSetting).add()
            .append(new KeyedCodec<Boolean>("AllowPartyInteractPortalSettingChanges", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.AllowPartyInteractPortalSetting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.AllowPartyInteractPortalSetting).add()

            .append(new KeyedCodec<String[]>("ClaimCommandAliases", Codec.STRING_ARRAY),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.ClaimCommandAliases = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.ClaimCommandAliases).add()
            .append(new KeyedCodec<String[]>("WorldClaimBlacklist", Codec.STRING_ARRAY),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.WorldNameBlacklistForClaiming = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.WorldNameBlacklistForClaiming).add()
            .append(new KeyedCodec<String>("TitleTopClaimTitleText", Codec.STRING),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.TitleTopClaimTitleText = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.TitleTopClaimTitleText).add()
            .append(new KeyedCodec<String[]>("FullWorldProtection", Codec.STRING_ARRAY),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.FullWorldProtection = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.FullWorldProtection).add()
            .append(new KeyedCodec<Boolean>("EXPERIMENTAL-EnableAlloyEntryTesting", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.EnableAlloyEntryTesting = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.EnableAlloyEntryTesting).add()
            .append(new KeyedCodec<Boolean>("EnableParticleBorders", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.EnableParticleBorders = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.EnableParticleBorders).add()
            .append(new KeyedCodec<Boolean>("ForceSimpleClaimsChunkWorldMap", Codec.BOOLEAN),
                    (simpleClaimsConfig, aDouble, extraInfo) -> simpleClaimsConfig.ForceSimpleClaimsChunkWorldMap = aDouble,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.ForceSimpleClaimsChunkWorldMap).add()
            .append(new KeyedCodec<Boolean>("CreativeModeBypassProtection", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.CreativeModeBypassProtection = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.CreativeModeBypassProtection).add()

            .append(new KeyedCodec<String[]>("BlocksThatIgnoreInteractRestrictions", Codec.STRING_ARRAY),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.BlocksThatIgnoreInteractRestrictions = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.BlocksThatIgnoreInteractRestrictions).add()

            .append(new KeyedCodec<Integer>("MaxPartyMembers", Codec.INTEGER),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.MaxPartyMembers = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.MaxPartyMembers).add()
            .append(new KeyedCodec<Integer>("MaxPartyAllies", Codec.INTEGER),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.MaxPartyAllies = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.MaxPartyAllies).add()
            .append(new KeyedCodec<Integer>("PartyInactivityHours", Codec.INTEGER),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.PartyInactivityHours = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.PartyInactivityHours).add()
            .append(new KeyedCodec<Boolean>("RenderClaimNamesOnWorldMap", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.RenderClaimNamesOnWorldMap = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.RenderClaimNamesOnWorldMap).add()
            .append(new KeyedCodec<Boolean>("EXPERIMENTAL-RenderMapInClaimUI", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.RenderMapInClaimUI = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.RenderMapInClaimUI).add()
            .build();

    private String[] PartyCommandAliases = new String[]{"scp", "sc-party", "party"};
    private int DefaultPartyClaimsAmount = 25;
    private int MaxPartyMembers = -1;
    private int MaxPartyAllies = -1;
    private int PartyInactivityHours = -1;
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
}
