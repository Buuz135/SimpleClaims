package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Constants;
import org.bson.BsonValue;
import org.bson.json.JsonWriter;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class FileUtils {

    public static String MAIN_PATH = Constants.UNIVERSE_PATH.resolve("SimpleClaims").toAbsolutePath().toString();
    public static String PARTY_PATH = MAIN_PATH + File.separator + "Parties.json";
    public static String CLAIM_PATH = MAIN_PATH + File.separator + "Claims.json";
    public static String NAMES_CACHE_PATH = MAIN_PATH + File.separator + "NameCache.json";
    public static String ADMIN_OVERRIDES_PATH = MAIN_PATH + File.separator + "AdminOverrides.json";

    public static void ensureDirectory(String path){
        var file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void ensureMainDirectory(){
        ensureDirectory(MAIN_PATH);
    }

    public static File ensureFile(String path, String defaultContent){
        var file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
                var writer = new FileWriter(file);
                writer.write(defaultContent);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

}
