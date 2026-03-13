package com.buuz135.simpleclaims.util;

import com.hypixel.hytale.server.core.modules.i18n.I18nModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public final class TranslationHelper {

    private TranslationHelper() {
    }

    public static String rawTextOrEnglish(String translationKey, PlayerRef playerRef) {
        String playerLanguage = playerRef.getLanguage();
        if (!playerLanguage.isBlank()) {
            String translated = I18nModule.get().getMessage(playerLanguage, translationKey);
            if (translated != null && !translated.isBlank() && !translated.equals(translationKey)) {
                return translated;
            }
        }

        String translated = I18nModule.get().getMessage("en-US", translationKey);
        if (translated != null && !translated.isBlank() && !translated.equals(translationKey)) {
            return translated;
        }

        return translationKey;
    }
}