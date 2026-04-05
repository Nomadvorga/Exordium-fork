package dev.tr7zw.exordium.versionless.config;

public enum TextColorPaletteOption {

    BLACK("black", 0x000000),
    DARK_BLUE("dark_blue", 0x0000AA),
    DARK_GREEN("dark_green", 0x00AA00),
    DARK_AQUA("dark_aqua", 0x00AAAA),
    DARK_RED("dark_red", 0xAA0000),
    DARK_PURPLE("dark_purple", 0xAA00AA),
    GOLD("gold", 0xFFAA00),
    GRAY("gray", 0xAAAAAA),
    DARK_GRAY("dark_gray", 0x555555),
    BLUE("blue", 0x5555FF),
    GREEN("green", 0x55FF55),
    AQUA("aqua", 0x55FFFF),
    RED("red", 0xFF5555),
    LIGHT_PURPLE("light_purple", 0xFF55FF),
    YELLOW("yellow", 0xFFFF55),
    WHITE("white", 0xFFFFFF);

    private final String id;
    private final int defaultRgb;

    TextColorPaletteOption(String id, int defaultRgb) {
        this.id = id;
        this.defaultRgb = defaultRgb;
    }

    public String getTranslationKey() {
        return "text.exordium.setting.textcolor." + id;
    }

    public int getDefaultColor() {
        return Config.toOpaqueColor(defaultRgb);
    }

    public int getDefaultRgb() {
        return defaultRgb;
    }

    public int getColor(Config config) {
        return switch (this) {
        case BLACK -> Config.toOpaqueColor(config.textColorBlack);
        case DARK_BLUE -> Config.toOpaqueColor(config.textColorDarkBlue);
        case DARK_GREEN -> Config.toOpaqueColor(config.textColorDarkGreen);
        case DARK_AQUA -> Config.toOpaqueColor(config.textColorDarkAqua);
        case DARK_RED -> Config.toOpaqueColor(config.textColorDarkRed);
        case DARK_PURPLE -> Config.toOpaqueColor(config.textColorDarkPurple);
        case GOLD -> Config.toOpaqueColor(config.textColorGold);
        case GRAY -> Config.toOpaqueColor(config.textColorGray);
        case DARK_GRAY -> Config.toOpaqueColor(config.textColorDarkGray);
        case BLUE -> Config.toOpaqueColor(config.textColorBlue);
        case GREEN -> Config.toOpaqueColor(config.textColorGreen);
        case AQUA -> Config.toOpaqueColor(config.textColorAqua);
        case RED -> Config.toOpaqueColor(config.textColorRed);
        case LIGHT_PURPLE -> Config.toOpaqueColor(config.textColorLightPurple);
        case YELLOW -> Config.toOpaqueColor(config.textColorYellow);
        case WHITE -> Config.toOpaqueColor(config.textColorWhite);
        };
    }

    public void setColor(Config config, int color) {
        int normalizedColor = Config.toOpaqueColor(color);
        switch (this) {
        case BLACK -> config.textColorBlack = normalizedColor;
        case DARK_BLUE -> config.textColorDarkBlue = normalizedColor;
        case DARK_GREEN -> config.textColorDarkGreen = normalizedColor;
        case DARK_AQUA -> config.textColorDarkAqua = normalizedColor;
        case DARK_RED -> config.textColorDarkRed = normalizedColor;
        case DARK_PURPLE -> config.textColorDarkPurple = normalizedColor;
        case GOLD -> config.textColorGold = normalizedColor;
        case GRAY -> config.textColorGray = normalizedColor;
        case DARK_GRAY -> config.textColorDarkGray = normalizedColor;
        case BLUE -> config.textColorBlue = normalizedColor;
        case GREEN -> config.textColorGreen = normalizedColor;
        case AQUA -> config.textColorAqua = normalizedColor;
        case RED -> config.textColorRed = normalizedColor;
        case LIGHT_PURPLE -> config.textColorLightPurple = normalizedColor;
        case YELLOW -> config.textColorYellow = normalizedColor;
        case WHITE -> config.textColorWhite = normalizedColor;
        }
    }

}
