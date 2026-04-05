package dev.tr7zw.exordium.config;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TextSelectionColorScreen extends ColorPickerScreen {

    public TextSelectionColorScreen(Screen parent) {
        super(parent, Component.translatable("text.exordium.selectionpicker.title"),
                Component.translatable("text.exordium.selectionpicker.preview"),
                () -> ExordiumModBase.instance.config.getTextSelectionColor(),
                color -> ExordiumModBase.instance.config.setTextSelectionColor(color),
                Config.DEFAULT_TEXT_SELECTION_COLOR);
    }

}
