package com.buuz135.simpleclaims.files;

import com.buuz135.simpleclaims.util.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdminOverridesBlockingFile extends BlockingDiskFile {

    private Set<UUID> adminOverrides;

    public AdminOverridesBlockingFile() {
        super(Path.of(FileUtils.ADMIN_OVERRIDES_PATH));
        this.adminOverrides = new HashSet<>();
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        var root = JsonParser.parseReader(bufferedReader).getAsJsonObject();
        if (root == null) return;
        JsonArray overridesArray = root.getAsJsonArray("AdminOverrides");
        if (overridesArray == null) return;
        adminOverrides = new HashSet<>();
        overridesArray.forEach(jsonElement -> {
            adminOverrides.add(UUID.fromString(jsonElement.getAsString()));
        });
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray overridesArray = new JsonArray();
        adminOverrides.forEach(uuid -> overridesArray.add(uuid.toString()));
        root.add("AdminOverrides", overridesArray);
        bufferedWriter.write(root.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray overrides = new JsonArray();
        root.add("AdminOverrides", overrides);
        bufferedWriter.write(root.toString());
    }

    public Set<UUID> getAdminOverrides() {
        return adminOverrides;
    }
}
