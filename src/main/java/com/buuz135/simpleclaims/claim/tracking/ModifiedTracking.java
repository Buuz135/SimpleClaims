package com.buuz135.simpleclaims.claim.tracking;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import dev.unnm3d.codeclib.config.FieldName;

import java.util.UUID;

public class ModifiedTracking {


    @FieldName("UserUUID")
    private UUID user_uuid;
    @FieldName("UserName")
    private String user_name;
    @FieldName("Date")
    private String date;

    public ModifiedTracking(UUID user_uuid, String user_name, String date) {
        this.user_uuid = user_uuid;
        this.user_name = user_name;
        this.date = date;
    }

    public ModifiedTracking() {
        this(UUID.randomUUID(), "-", "");
    }

    public UUID getUserUUID() {
        return user_uuid;
    }

    public String getUserName() {
        return user_name;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "ModifiedTracking{" +
                "user_uuid=" + user_uuid +
                ", user_name='" + user_name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
