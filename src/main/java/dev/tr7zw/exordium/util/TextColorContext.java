package dev.tr7zw.exordium.util;

import dev.tr7zw.exordium.versionless.config.Config;

public final class TextColorContext {

    private static final ThreadLocal<Config.ComponentSettings> CURRENT_SETTINGS = new ThreadLocal<>();

    private TextColorContext() {
    }

    public static void run(Config.ComponentSettings settings, Runnable action) {
        Config.ComponentSettings previous = CURRENT_SETTINGS.get();
        CURRENT_SETTINGS.set(settings);
        try {
            action.run();
        } finally {
            if (previous == null) {
                CURRENT_SETTINGS.remove();
            } else {
                CURRENT_SETTINGS.set(previous);
            }
        }
    }

    public static int remapRenderableTextColor(int color) {
        Config.ComponentSettings settings = CURRENT_SETTINGS.get();
        if (settings == null || !settings.isTextColorEnabled()) {
            return color;
        }
        int alpha = color >>> 24;
        if (alpha == 0) {
            alpha = 0xFF;
        }
        return alpha << 24 | (Config.toOpaqueColor(settings.getTextColor()) & 0xFFFFFF);
    }

    public static int remapTextRgb(int rgb) {
        Config.ComponentSettings settings = CURRENT_SETTINGS.get();
        if (settings == null || !settings.isTextColorEnabled()) {
            return rgb;
        }
        return Config.toOpaqueColor(settings.getTextColor()) & 0xFFFFFF;
    }

}
