package com.buuz135.simpleclaims.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class SimpleClaimsConfig {

    public static final BuilderCodec<SimpleClaimsConfig> CODEC = BuilderCodec.builder(SimpleClaimsConfig.class, SimpleClaimsConfig::new)
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
            .append(new KeyedCodec<Boolean>("ForceSimpleClaimsChunkWorldMap", Codec.BOOLEAN),
                    (simpleClaimsConfig, aDouble, extraInfo) -> simpleClaimsConfig.ForceSimpleClaimsChunkWorldMap = aDouble,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.ForceSimpleClaimsChunkWorldMap).add()
            .append(new KeyedCodec<Boolean>("CreativeModeBypassProtection", Codec.BOOLEAN),
                    (simpleClaimsConfig, value, extraInfo) -> simpleClaimsConfig.CreativeModeBypassProtection = value,
                    (simpleClaimsConfig, extraInfo) -> simpleClaimsConfig.CreativeModeBypassProtection).add()
            .build();

    private int DefaultPartyClaimsAmount = 25;
    private boolean DefaultPartyBlockPlaceEnabled = false;
    private boolean DefaultPartyBlockBreakEnabled = false;
    private boolean DefaultPartyBlockInteractEnabled = false;

    private boolean ForceSimpleClaimsChunkWorldMap = true;
    private boolean CreativeModeBypassProtection = false;

    public SimpleClaimsConfig() {

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
}
