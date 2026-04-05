package dev.tr7zw.exordium.components;

import java.util.function.Function;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.versionless.config.Config;

public enum HudCategory {
    CHAT(config -> config.chatSettings),
    DEBUG(config -> config.debugScreenSettings),
    HEALTH(config -> config.healthSettings),
    HOTBAR(config -> config.hotbarSettings),
    EXPERIENCE(config -> config.experienceSettings),
    SCOREBOARD(config -> config.scoreboardSettings),
    TABLIST(config -> config.tablistSettings),
    VIGNETTE(config -> config.vignetteSettings),
    CROSSHAIR(config -> config.crosshairSettings),
    BOSSBAR(config -> config.bossbarSettings);

    private final Function<Config, Config.ComponentSettings> settingsGetter;

    HudCategory(Function<Config, Config.ComponentSettings> settingsGetter) {
        this.settingsGetter = settingsGetter;
    }

    public Config.ComponentSettings getSettings() {
        if (ExordiumModBase.instance == null || ExordiumModBase.instance.config == null) {
            return null;
        }
        return settingsGetter.apply(ExordiumModBase.instance.config);
    }

}
