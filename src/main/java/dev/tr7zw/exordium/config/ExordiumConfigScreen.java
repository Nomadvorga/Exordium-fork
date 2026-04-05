package dev.tr7zw.exordium.config;

import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.exordium.versionless.config.Config;
import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.config.widget.ColorPreviewWidget;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ExordiumConfigScreen extends CustomConfigScreen {

    public ExordiumConfigScreen(Screen lastScreen) {
        super(lastScreen, "text.exordium.title");
    }

    @Override
    public void initialize() {
        Config config = ExordiumModBase.instance.config;
        getOptions().addHeader(Component.translatable("text.exordium.section.general"));
        getOptions().addSmall(
                getIntOption("text.exordium.pollRate", 20, 240, () -> config.pollRate, (v) -> config.pollRate = v),
                getIntOption("text.exordium.setting.mainmenu.fps", 60, 360, () -> config.mainMenuFpsLimit,
                        (v) -> config.mainMenuFpsLimit = v));
        getOptions().addHeader(Component.translatable("text.exordium.section.selection"));
        Button openSelectionPicker = Button.builder(Component.translatable("text.exordium.setting.selection.open"),
                button -> this.minecraft.setScreen(new TextSelectionColorScreen(this))).size(150, 20).build();
        ColorPreviewWidget selectionPreview = new ColorPreviewWidget(0, 0, 150, 20,
                () -> ExordiumModBase.instance.config.getTextSelectionColor(),
                () -> this.minecraft.setScreen(new TextSelectionColorScreen(this)));
        getOptions().addSmall(openSelectionPicker, selectionPreview);

        addSettings(config.chatSettings, "chat", false, false);
        addSettings(config.debugScreenSettings, "debug", false, true);
        addSettings(config.healthSettings, "health", false, false);
        addSettings(config.hotbarSettings, "hotbar", false, false);
        addSettings(config.experienceSettings, "experience", false, false);
        addSettings(config.scoreboardSettings, "scoreboard", false, false);
        addScoreboardEditorSettings();
        addSettings(config.tablistSettings, "tablist", false, false);
        addSettings(config.vignetteSettings, "vignette", true, false);
        addSettings(config.crosshairSettings, "crosshair", true, false);
        addSettings(config.bossbarSettings, "bossbar", true, false);
        addSettings(config.xaerosMinimapSettings, "xaerosmimimap", true, true);
        addSettings(config.paperdollSettings, "paperdoll", true, true);
    }

    private void addSettings(Config.ComponentSettings settings, String name, boolean hideBlending,
            boolean hideForceUpdates) {
        getOptions().addHeader(getSectionTitle(name));

        OptionInstance<Boolean> enabled = getOnOffOption("text.exordium.setting." + name + ".enabled",
                () -> settings.isEnabled(), (b) -> settings.setEnabled(b));
        OptionInstance<Integer> fps = getIntOption("text.exordium.setting." + name + ".fps", 5, 240,
                () -> settings.getMaxFps(), (v) -> settings.setMaxFps(v));
        getOptions().addSmall(enabled, fps);

        OptionInstance<Boolean> blending = null;
        if (!hideBlending) {
            blending = getOnOffOption("text.exordium.setting." + name + ".forceblend", () -> settings.isForceBlend(),
                    (b) -> settings.setForceBlend(b));
        }

        OptionInstance<Boolean> updates = null;
        if (!hideForceUpdates) {
            updates = getOnOffOption("text.exordium.setting." + name + ".forceupdates", () -> settings.isForceUpdates(),
                    (b) -> settings.setForceUpdates(b));
        }

        if (blending != null && updates != null) {
            getOptions().addSmall(blending, updates);
        } else if (blending != null) {
            getOptions().addBig(blending);
        } else if (updates != null) {
            getOptions().addBig(updates);
        }
    }

    private void addScoreboardEditorSettings() {
        Button editButton = Button
                .builder(Component.translatable("text.exordium.setting.scoreboard.editor.open"),
                        button -> this.minecraft.setScreen(new ScoreboardEditorScreen(this)))
                .size(150, 20).build();
        Button resetButton = Button.builder(Component.translatable("text.exordium.setting.scoreboard.editor.reset"),
                button -> {
                    Config config = ExordiumModBase.instance.config;
                    config.scoreboardCustomPosition = false;
                    config.scoreboardX = 0;
                    config.scoreboardY = 0;
                    config.scoreboardScale = 100;
                    config.scoreboardOpacity = 100;
                    ExordiumModBase.instance.writeConfig();
                }).size(150, 20).build();
        OptionInstance<Boolean> hideScores = getOnOffOption("text.exordium.setting.scoreboard.hidescores",
                () -> ExordiumModBase.instance.config.scoreboardHideScores, (b) -> ExordiumModBase.instance.config.scoreboardHideScores = b);
        getOptions().addSmall(editButton, resetButton);
        getOptions().addBig(hideScores);
    }

    private Component getSectionTitle(String name) {
        return Component.translatable("text.exordium.section." + name);
    }

    @Override
    public void save() {
        ExordiumModBase.instance.writeConfig();
    }

    @Override
    public void reset() {
        ExordiumModBase.instance.config = new Config();
        ExordiumModBase.instance.writeConfig();
        this.rebuildWidgets();
    }

}
