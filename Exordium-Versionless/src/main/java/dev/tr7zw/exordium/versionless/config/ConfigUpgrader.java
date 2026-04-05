package dev.tr7zw.exordium.versionless.config;

public class ConfigUpgrader {

    public static boolean upgradeConfig(Config config) {
        boolean changed = false;

        if (config.configVersion <= 1) {
            config.configVersion = 2;
            // set screenBuffering to false
            changed = true;
        }

        if (config.configVersion <= 2) {
            config.configVersion = 3;
            // remove screenBuffering
            changed = true;
        }

        if (config.configVersion <= 3) {
            config.configVersion = 4;
            changed = true;
        }

        if (config.configVersion <= 4) {
            config.configVersion = 5;
            changed = true;
        }

        if (config.configVersion <= 5) {
            config.configVersion = 6;
            config.textSelectionRed = 0;
            config.textSelectionGreen = 0;
            config.textSelectionBlue = 255;
            changed = true;
        }

        if (config.configVersion <= 6) {
            config.configVersion = 7;
            changed = true;
        }

        if (config.configVersion <= 7) {
            for (TextColorPaletteOption option : TextColorPaletteOption.values()) {
                option.setColor(config, option.getDefaultColor());
            }
            config.configVersion = 8;
            changed = true;
        }

        if (config.configVersion <= 8) {
            normalizeTextColor(config.chatSettings);
            normalizeTextColor(config.debugScreenSettings);
            normalizeTextColor(config.hotbarSettings);
            normalizeTextColor(config.experienceSettings);
            normalizeTextColor(config.healthSettings);
            normalizeTextColor(config.scoreboardSettings);
            normalizeTextColor(config.tablistSettings);
            normalizeTextColor(config.vignetteSettings);
            normalizeTextColor(config.crosshairSettings);
            normalizeTextColor(config.bossbarSettings);
            normalizeTextColor(config.xaerosMinimapSettings);
            normalizeTextColor(config.paperdollSettings);
            config.configVersion = 9;
            changed = true;
        }

        if (config.configVersion <= 9) {
            config.configVersion = 10;
            changed = true;
        }

        if (config.globalSettings == null) {
            config.globalSettings = new Config.ComponentSettings(true, 30);
            changed = true;
        }

        if (config.inventorySettings == null) {
            config.inventorySettings = new Config.ComponentSettings(config.globalSettings.isEnabled(),
                    config.globalSettings.getMaxFps(), config.globalSettings.isForceBlend());
            config.inventorySettings.setForceUpdates(config.globalSettings.isForceUpdates());
            changed = true;
        }

        if (config.mainMenuFpsLimit < 60 || config.mainMenuFpsLimit > 360) {
            config.mainMenuFpsLimit = 60;
            changed = true;
        }

        if (config.textSelectionRed < 0 || config.textSelectionRed > 255) {
            config.textSelectionRed = 0;
            changed = true;
        }

        if (config.textSelectionGreen < 0 || config.textSelectionGreen > 255) {
            config.textSelectionGreen = 0;
            changed = true;
        }

        if (config.textSelectionBlue < 0 || config.textSelectionBlue > 255) {
            config.textSelectionBlue = 255;
            changed = true;
        }

        if (config.scoreboardScale < 50 || config.scoreboardScale > 200) {
            config.scoreboardScale = 100;
            changed = true;
        }

        if (config.scoreboardOpacity < 10 || config.scoreboardOpacity > 100) {
            config.scoreboardOpacity = 100;
            changed = true;
        }

        if (!config.scoreboardCustomPosition && (config.scoreboardX != 0 || config.scoreboardY != 0)) {
            config.scoreboardX = 0;
            config.scoreboardY = 0;
            changed = true;
        }

        for (TextColorPaletteOption option : TextColorPaletteOption.values()) {
            int currentColor = option.getColor(config);
            if (currentColor == 0xFF000000 && option != TextColorPaletteOption.BLACK
                    && option.getDefaultColor() != 0xFF000000) {
                option.setColor(config, option.getDefaultColor());
                changed = true;
                continue;
            }
            if (currentColor != Config.toOpaqueColor(currentColor)) {
                option.setColor(config, currentColor);
                changed = true;
            }
        }

        changed |= normalizeTextColor(config.chatSettings);
        changed |= normalizeTextColor(config.debugScreenSettings);
        changed |= normalizeTextColor(config.hotbarSettings);
        changed |= normalizeTextColor(config.experienceSettings);
        changed |= normalizeTextColor(config.healthSettings);
        changed |= normalizeTextColor(config.scoreboardSettings);
        changed |= normalizeTextColor(config.tablistSettings);
        changed |= normalizeTextColor(config.vignetteSettings);
        changed |= normalizeTextColor(config.crosshairSettings);
        changed |= normalizeTextColor(config.bossbarSettings);
        changed |= normalizeTextColor(config.xaerosMinimapSettings);
        changed |= normalizeTextColor(config.paperdollSettings);

        // check for more changes here

        return changed;
    }

    private static boolean normalizeTextColor(Config.ComponentSettings settings) {
        if (settings == null) {
            return false;
        }
        if (settings.getTextColor() == 0) {
            settings.setTextColor(0xFFFFFFFF);
            return true;
        }
        int opaqueColor = Config.toOpaqueColor(settings.getTextColor());
        if (opaqueColor != settings.getTextColor()) {
            settings.setTextColor(opaqueColor);
            return true;
        }
        return false;
    }

}
