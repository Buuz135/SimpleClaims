package com.buuz135.simpleclaims.claim;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminOverridesStorage {

    public static final BuilderCodec<AdminOverridesStorage> CODEC = BuilderCodec.builder(AdminOverridesStorage.class, AdminOverridesStorage::new)
            .append(new KeyedCodec<>("AdminOverrides", new ArrayCodec<>(Codec.STRING, String[]::new)),
                    (storage, overrides, extraInfo) -> storage.setAdminOverrides(Arrays.asList(overrides)),
                    (storage, extraInfo) -> storage.getAdminOverrides().toArray(new String[0])).add()
            .build();

    private List<String> adminOverrides;

    public AdminOverridesStorage() {
        this.adminOverrides = new ArrayList<>();
    }

    public AdminOverridesStorage(List<String> adminOverrides) {
        this.adminOverrides = new ArrayList<>(adminOverrides);
    }

    public List<String> getAdminOverrides() {
        return adminOverrides;
    }

    public void setAdminOverrides(List<String> adminOverrides) {
        this.adminOverrides = new ArrayList<>(adminOverrides);
    }
}
