package com.buuz135.simpleclaims.claim;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;

import java.util.*;

public class AdminOverridesStorage {

    public static final BuilderCodec<AdminOverridesStorage> CODEC = BuilderCodec.builder(AdminOverridesStorage.class, AdminOverridesStorage::new)
            .append(new KeyedCodec<>("AdminOverrides", new ArrayCodec<>(Codec.STRING, String[]::new)),
                    (storage, overrides, extraInfo) -> storage.setAdminOverridesFromStrings(Arrays.asList(overrides)),
                    (storage, extraInfo) -> storage.getAdminOverridesAsStrings()).add()
            .build();

    private Set<UUID> adminOverrides;

    public AdminOverridesStorage() {
        this.adminOverrides = new HashSet<>();
    }

    public AdminOverridesStorage(Set<UUID> adminOverrides) {
        this.adminOverrides = new HashSet<>(adminOverrides);
    }

    public Set<UUID> getAdminOverrides() {
        return adminOverrides;
    }

    public void setAdminOverrides(Set<UUID> adminOverrides) {
        this.adminOverrides = new HashSet<>(adminOverrides);
    }

    private void setAdminOverridesFromStrings(List<String> strings) {
        this.adminOverrides = new HashSet<>();
        for (String s : strings) {
            this.adminOverrides.add(UUID.fromString(s));
        }
    }

    private String[] getAdminOverridesAsStrings() {
        return adminOverrides.stream().map(UUID::toString).toArray(String[]::new);
    }
}
