package com.buuz135.simpleclaims.files;

import com.buuz135.simpleclaims.claim.party.PartyInfo;
import com.buuz135.simpleclaims.claim.party.PartyOverride;
import com.buuz135.simpleclaims.claim.tracking.ModifiedTracking;
import com.buuz135.simpleclaims.util.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;
import com.hypixel.hytale.server.core.util.io.FileUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class PartyBlockingFile extends BlockingDiskFile {

    private HashMap<String, PartyInfo> parties;

    public PartyBlockingFile() {
        super(Path.of(FileUtils.PARTY_PATH));
        this.parties = new HashMap<>();
    }

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        var root = JsonParser.parseReader(bufferedReader).getAsJsonObject();
        if (root == null) return;
        JsonArray partiesArray = root.getAsJsonArray("Parties");
        if (partiesArray == null) return;
        parties = new HashMap<>();
        partiesArray.forEach(jsonElement -> {
            JsonObject party = jsonElement.getAsJsonObject();
            var partyInfo = new PartyInfo(
                    UUID.fromString(party.get("Id").getAsString()),
                    UUID.fromString(party.get("Owner").getAsString()),
                    party.get("Name").getAsString(),
                    party.get("Description").getAsString(),
                    party.get("Members").getAsJsonArray().asList().stream().map(member -> UUID.fromString(member.getAsString())).toArray(UUID[]::new),
                    party.get("Color").getAsInt()
            );
            party.get("Overrides").getAsJsonArray().forEach(override -> {
                var partyOverrideValue = new PartyOverride.PartyOverrideValue(
                        override.getAsJsonObject().get("Value").getAsJsonObject().get("Type").getAsString(),
                        override.getAsJsonObject().get("Value").getAsJsonObject().get("Value").getAsString());
                partyInfo.setOverride(new PartyOverride(override.getAsJsonObject().get("Type").getAsString(), partyOverrideValue));
            });
            var createdTracker = party.get("CreatedTracker").getAsJsonObject();
            var modifiedTracker = party.get("ModifiedTracker").getAsJsonObject();
            partyInfo.setCreatedTracked(new ModifiedTracking(
                    UUID.fromString(createdTracker.get("UserUUID").getAsString()),
                    createdTracker.get("UserName").getAsString(),
                    createdTracker.get("Date").getAsString()
            ));
            partyInfo.setModifiedTracked(new ModifiedTracking(
                    UUID.fromString(modifiedTracker.get("UserUUID").getAsString()),
                    modifiedTracker.get("UserName").getAsString(),
                    modifiedTracker.get("Date").getAsString()
            ));
            if (party.has("PartyAllies")) {
                party.get("PartyAllies").getAsJsonArray().forEach(jsonElement1 -> partyInfo.addPartyAllies(UUID.fromString(jsonElement1.getAsString())));
            }
            if (party.has("PlayerAllies")) {
                party.get("PlayerAllies").getAsJsonArray().forEach(jsonElement1 -> partyInfo.addPlayerAllies(UUID.fromString(jsonElement1.getAsString())));
            }
            parties.put(partyInfo.getId().toString(), partyInfo);
        });
    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray partiesArray = new JsonArray();
        this.parties.values().forEach(partyInfo -> {
            JsonObject party = new JsonObject();
            party.addProperty("Id", partyInfo.getId().toString());
            party.addProperty("Owner", partyInfo.getOwner().toString());
            party.addProperty("Name", partyInfo.getName());
            party.addProperty("Description", partyInfo.getDescription());
            JsonArray members = new JsonArray();
            for (UUID member : partyInfo.getMembers()) {
                members.add(member.toString());
            }
            party.add("Members", members);
            party.addProperty("Color", partyInfo.getColor());
            JsonArray overrides = new JsonArray();
            partyInfo.getOverrides().forEach(partyOverride -> {
                JsonObject override = new JsonObject();
                override.addProperty("Type", partyOverride.getType());
                JsonObject value = new JsonObject();
                value.addProperty("Type", partyOverride.getValue().getType());
                value.addProperty("Value", partyOverride.getValue().getValue());
                override.add("Value", value);
                overrides.add(override);
            });
            party.add("Overrides", overrides);
            JsonObject createdTracker = new JsonObject();
            createdTracker.addProperty("UserUUID", partyInfo.getCreatedTracked().getUserUUID().toString());
            createdTracker.addProperty("UserName", partyInfo.getCreatedTracked().getUserName());
            createdTracker.addProperty("Date", partyInfo.getCreatedTracked().getDate());
            party.add("CreatedTracker", createdTracker);
            JsonObject modifiedTracker = new JsonObject();
            modifiedTracker.addProperty("UserUUID", partyInfo.getModifiedTracked().getUserUUID().toString());
            modifiedTracker.addProperty("UserName", partyInfo.getModifiedTracked().getUserName());
            modifiedTracker.addProperty("Date", partyInfo.getModifiedTracked().getDate());
            party.add("ModifiedTracker", modifiedTracker);
            JsonArray partyAllies = new JsonArray();
            partyInfo.getPartyAllies().forEach(uuid -> partyAllies.add(uuid.toString()));
            party.add("PartyAllies", partyAllies);
            JsonArray playerAllies = new JsonArray();
            partyInfo.getPlayerAllies().forEach(uuid -> playerAllies.add(uuid.toString()));
            party.add("PlayerAllies", playerAllies);
            partiesArray.add(party);
        });
        root.add("Parties", partiesArray);
        bufferedWriter.write(root.toString());
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        JsonObject root = new JsonObject();
        JsonArray parties = new JsonArray();
        root.add("Parties", parties);
        bufferedWriter.write(root.toString());
    }

    public HashMap<String, PartyInfo> getParties() {
        return parties;
    }
}
