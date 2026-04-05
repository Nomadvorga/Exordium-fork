package dev.tr7zw.exordium.versionless.config;

import lombok.Getter;
import lombok.Setter;

public class Config {

    public static final int DEFAULT_TEXT_SELECTION_COLOR = 0xFF0000FF;

    public int configVersion = 10;
    public ComponentSettings globalSettings = new ComponentSettings(true, 30);
    public ComponentSettings inventorySettings = new ComponentSettings(true, 5);
    public int mainMenuFpsLimit = 60;
    public int pollRate = 60;
    public int textSelectionRed = 0;
    public int textSelectionGreen = 0;
    public int textSelectionBlue = 255;
    public int textColorBlack = 0xFF000000;
    public int textColorDarkBlue = 0xFF0000AA;
    public int textColorDarkGreen = 0xFF00AA00;
    public int textColorDarkAqua = 0xFF00AAAA;
    public int textColorDarkRed = 0xFFAA0000;
    public int textColorDarkPurple = 0xFFAA00AA;
    public int textColorGold = 0xFFFFAA00;
    public int textColorGray = 0xFFAAAAAA;
    public int textColorDarkGray = 0xFF555555;
    public int textColorBlue = 0xFF5555FF;
    public int textColorGreen = 0xFF55FF55;
    public int textColorAqua = 0xFF55FFFF;
    public int textColorRed = 0xFFFF5555;
    public int textColorLightPurple = 0xFFFF55FF;
    public int textColorYellow = 0xFFFFFF55;
    public int textColorWhite = 0xFFFFFFFF;
    public ComponentSettings chatSettings = new ComponentSettings(true, 20);
    public ComponentSettings debugScreenSettings = new ComponentSettings(true, 10);
    public ComponentSettings hotbarSettings = new ComponentSettings(true, 20);
    public ComponentSettings experienceSettings = new ComponentSettings(true, 5);
    public ComponentSettings healthSettings = new ComponentSettings(true, 20);
    public ComponentSettings scoreboardSettings = new ComponentSettings(true, 5);
    public boolean scoreboardCustomPosition = false;
    public int scoreboardX = 0;
    public int scoreboardY = 0;
    public int scoreboardScale = 100;
    public int scoreboardOpacity = 100;
    public boolean scoreboardHideScores = false;
    public ComponentSettings tablistSettings = new ComponentSettings(true, 20);
    public ComponentSettings vignetteSettings = new ComponentSettings(true, 5);
    public ComponentSettings crosshairSettings = new ComponentSettings(false, 20);
    public ComponentSettings bossbarSettings = new ComponentSettings(true, 5);
    public ComponentSettings xaerosMinimapSettings = new ComponentSettings(true, 30, true);
    public ComponentSettings paperdollSettings = new ComponentSettings(true, 30);

    @Getter
    @Setter
    public static class ComponentSettings {
        private boolean enabled = true;
        private int maxFps = 10;
        private boolean forceBlend = false;
        private boolean forceUpdates = false;
        private boolean textColorEnabled = false;
        private int textColor = 0xFFFFFFFF;

        public ComponentSettings(boolean enabled, int maxFps) {
            this.enabled = enabled;
            this.maxFps = maxFps;
        }

        public ComponentSettings(boolean enabled, int maxFps, boolean forceBlend) {
            this.enabled = enabled;
            this.maxFps = maxFps;
            this.forceBlend = forceBlend;
        }
    }

    public int getTextSelectionColor() {
        return 0xFF000000 | clampChannel(textSelectionRed) << 16 | clampChannel(textSelectionGreen) << 8
                | clampChannel(textSelectionBlue);
    }

    public void setTextSelectionColor(int color) {
        int normalizedColor = toOpaqueColor(color);
        textSelectionRed = normalizedColor >> 16 & 255;
        textSelectionGreen = normalizedColor >> 8 & 255;
        textSelectionBlue = normalizedColor & 255;
    }

    public int remapRenderableTextColor(int color) {
        int alpha = color >>> 24;
        if (alpha == 0) {
            alpha = 0xFF;
        }
        return alpha << 24 | remapTextRgb(color & 0xFFFFFF);
    }

    public int remapTextRgb(int rgb) {
        TextColorPaletteOption option = switch (rgb & 0xFFFFFF) {
        case 0x000000, 0x202020 -> TextColorPaletteOption.BLACK;
        case 0x0000AA -> TextColorPaletteOption.DARK_BLUE;
        case 0x00AA00 -> TextColorPaletteOption.DARK_GREEN;
        case 0x00AAAA -> TextColorPaletteOption.DARK_AQUA;
        case 0xAA0000 -> TextColorPaletteOption.DARK_RED;
        case 0xAA00AA -> TextColorPaletteOption.DARK_PURPLE;
        case 0xFFAA00 -> TextColorPaletteOption.GOLD;
        case 0xAAAAAA, 0xA0A0A0, 0x909090 -> TextColorPaletteOption.GRAY;
        case 0x555555, 0x404040 -> TextColorPaletteOption.DARK_GRAY;
        case 0x5555FF -> TextColorPaletteOption.BLUE;
        case 0x55FF55 -> TextColorPaletteOption.GREEN;
        case 0x55FFFF -> TextColorPaletteOption.AQUA;
        case 0xFF5555 -> TextColorPaletteOption.RED;
        case 0xFF55FF -> TextColorPaletteOption.LIGHT_PURPLE;
        case 0xFFFF55 -> TextColorPaletteOption.YELLOW;
        case 0xFFFFFF, 0xE0E0E0, 0xF0F0F0 -> TextColorPaletteOption.WHITE;
        default -> null;
        };
        if (option == null) {
            return rgb & 0xFFFFFF;
        }
        return option.getColor(this) & 0xFFFFFF;
    }

    public static int toOpaqueColor(int color) {
        return 0xFF000000 | color & 0xFFFFFF;
    }

    public boolean useCustomScoreboardLayout() {
        return scoreboardCustomPosition || scoreboardScale != 100 || scoreboardOpacity != 100 || scoreboardHideScores;
    }

    private static int clampChannel(int value) {
        return Math.max(0, Math.min(255, value));
    }

}
