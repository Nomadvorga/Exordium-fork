package dev.tr7zw.exordium.config;

import java.util.Locale;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ColorPickerScreen extends Screen {

    private static final int PREVIEW_HEIGHT = 42;

    private final Screen parent;
    private final Component previewLabel;
    private final IntSupplier colorSupplier;
    private final IntConsumer colorConsumer;
    private final int defaultColor;
    private float hue;
    private float saturation;
    private float brightness;
    private ColorSlider hueSlider;
    private ColorSlider saturationSlider;
    private ColorSlider brightnessSlider;
    private EditBox hexInput;
    private boolean updatingHexInput;

    public ColorPickerScreen(Screen parent, Component title, Component previewLabel, IntSupplier colorSupplier,
            IntConsumer colorConsumer, int defaultColor) {
        super(title);
        this.parent = parent;
        this.previewLabel = previewLabel;
        this.colorSupplier = colorSupplier;
        this.colorConsumer = colorConsumer;
        this.defaultColor = defaultColor;
        int color = colorSupplier.getAsInt();
        setFromRgb(color >> 16 & 255, color >> 8 & 255, color & 255);
    }

    @Override
    protected void init() {
        int panelWidth = Math.min(320, this.width - 40);
        int left = this.width / 2 - panelWidth / 2;
        int top = 120;

        hexInput = addRenderableWidget(new EditBox(this.font, this.width / 2 - 70, 90, 140, 20,
                Component.translatable("text.exordium.colorpicker.hex")));
        hexInput.setMaxLength(7);
        hexInput.setFilter(ColorPickerScreen::isPotentialHexColor);
        hexInput.setHint(Component.literal("#RRGGBB"));
        setHexInputValue(getHexColor());
        hexInput.setResponder(this::onHexInputChanged);

        hueSlider = addRenderableWidget(new ColorSlider(left, top, panelWidth,
                Component.translatable("text.exordium.setting.selection.hue"), hue,
                value -> hue = value,
                value -> Component.literal(formatHueDegrees(value) + "\u00B0"),
                this::applyCurrentSelection));
        saturationSlider = addRenderableWidget(new ColorSlider(left, top + 26, panelWidth,
                Component.translatable("text.exordium.setting.selection.saturation"), saturation,
                value -> saturation = value,
                value -> Component.literal(Math.round(value * 100f) + "%"),
                this::applyCurrentSelection));
        brightnessSlider = addRenderableWidget(new ColorSlider(left, top + 52, panelWidth,
                Component.translatable("text.exordium.setting.selection.brightness"), brightness,
                value -> brightness = value,
                value -> Component.literal(Math.round(value * 100f) + "%"),
                this::applyCurrentSelection));

        int presetWidth = (panelWidth - 10) / 2;
        int presetTop = top + 92;
        addRenderableWidget(createPresetButton(left, presetTop, presetWidth,
                Component.translatable("text.exordium.colorpicker.preset.default"), defaultColor));
        addRenderableWidget(createPresetButton(left + presetWidth + 10, presetTop, presetWidth,
                Component.translatable("text.exordium.colorpicker.preset.white"), 0xFFFFFFFF));
        addRenderableWidget(createPresetButton(left, presetTop + 24, presetWidth,
                Component.translatable("text.exordium.colorpicker.preset.gray"), 0xFF808080));
        addRenderableWidget(createPresetButton(left + presetWidth + 10, presetTop + 24, presetWidth,
                Component.translatable("text.exordium.colorpicker.preset.black"), 0xFF000000));

        int footerY = this.height - 27;
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL,
                button -> this.minecraft.setScreen(parent)).pos(this.width / 2 - 155, footerY).size(100, 20).build());
        addRenderableWidget(Button.builder(Component.translatable("controls.reset"), button -> applyPreset(defaultColor))
                .pos(this.width / 2 - 50, footerY).size(100, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.minecraft.setScreen(parent))
                .pos(this.width / 2 + 55, footerY).size(100, 20).build());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        int panelWidth = Math.min(320, this.width - 40);
        int left = this.width / 2 - panelWidth / 2;
        int top = 44;
        int color = getCurrentColor();

        guiGraphics.drawCenteredString(this.font, previewLabel, this.width / 2, top - 12, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font, Component.translatable("text.exordium.colorpicker.hex"),
                this.width / 2, 78, 0xFFFFFF);
        guiGraphics.fill(left - 1, top - 1, left + panelWidth + 1, top + PREVIEW_HEIGHT + 1, 0xFFFFFFFF);
        guiGraphics.fill(left, top, left + panelWidth, top + PREVIEW_HEIGHT, 0xFF000000);
        guiGraphics.fill(left + 2, top + 2, left + panelWidth - 2, top + PREVIEW_HEIGHT - 2, color);

        int textColor = getContrastTextColor(color);
        guiGraphics.drawCenteredString(this.font, Component.literal(getHexColor()), this.width / 2, top + 17, textColor);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }

    private Button createPresetButton(int x, int y, int width, Component text, int color) {
        return Button.builder(text, button -> applyPreset(color)).pos(x, y).size(width, 20).build();
    }

    private void applyPreset(int color) {
        setFromRgb(color >> 16 & 255, color >> 8 & 255, color & 255);
        updateSliderValues();
        applyCurrentSelection();
    }

    private void onHexInputChanged(String value) {
        if (updatingHexInput) {
            return;
        }
        String normalized = normalizeHexInput(value);
        if (!normalized.equals(value)) {
            setHexInputValue(normalized);
        }
        if (!isCompleteHexColor(normalized)) {
            return;
        }
        int color = Integer.parseInt(normalized.substring(1), 16);
        setFromRgb(color >> 16 & 255, color >> 8 & 255, color & 255);
        updateSliderValues();
        applyCurrentSelection();
    }

    private void applyCurrentSelection() {
        setHexInputValue(getHexColor());
        colorConsumer.accept(getCurrentColor());
        ExordiumModBase.instance.writeConfig();
    }

    private int getCurrentColor() {
        float wrappedHue = hue >= 1f ? 0f : hue;
        return 0xFF000000 | Mth.hsvToRgb(wrappedHue, saturation, brightness);
    }

    private String getHexColor() {
        int color = getCurrentColor();
        return String.format(Locale.ROOT, "#%02X%02X%02X", color >> 16 & 255, color >> 8 & 255, color & 255);
    }

    private void updateSliderValues() {
        if (hueSlider != null) {
            hueSlider.setSliderValue(hue);
        }
        if (saturationSlider != null) {
            saturationSlider.setSliderValue(saturation);
        }
        if (brightnessSlider != null) {
            brightnessSlider.setSliderValue(brightness);
        }
    }

    private void setFromRgb(int red, int green, int blue) {
        float r = red / 255f;
        float g = green / 255f;
        float b = blue / 255f;
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;

        brightness = max;
        saturation = max == 0f ? 0f : delta / max;

        if (delta == 0f) {
            hue = 0f;
            return;
        }

        if (max == r) {
            hue = ((g - b) / delta) % 6f;
        } else if (max == g) {
            hue = (b - r) / delta + 2f;
        } else {
            hue = (r - g) / delta + 4f;
        }

        hue /= 6f;
        if (hue < 0f) {
            hue += 1f;
        }
    }

    private static int formatHueDegrees(float value) {
        return value >= 1f ? 360 : Math.round(value * 360f);
    }

    private void setHexInputValue(String value) {
        if (hexInput == null) {
            return;
        }
        updatingHexInput = true;
        hexInput.setValue(value);
        updatingHexInput = false;
    }

    private static String normalizeHexInput(String value) {
        String trimmed = value.trim().toUpperCase(Locale.ROOT);
        if (trimmed.isEmpty()) {
            return "";
        }
        String normalized = trimmed.startsWith("#") ? trimmed : "#" + trimmed;
        return normalized.length() > 7 ? normalized.substring(0, 7) : normalized;
    }

    private static boolean isPotentialHexColor(String value) {
        return value == null || value.isEmpty() || value.matches("#?[0-9a-fA-F]{0,6}");
    }

    private static boolean isCompleteHexColor(String value) {
        return value != null && value.matches("#[0-9A-F]{6}");
    }

    private static int getContrastTextColor(int color) {
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int luminance = (red * 299 + green * 587 + blue * 114) / 1000;
        return luminance >= 140 ? 0xFF101010 : 0xFFFFFFFF;
    }

    private static class ColorSlider extends AbstractSliderButton {

        private final Component label;
        private final java.util.function.Consumer<Float> update;
        private final java.util.function.Function<Float, Component> valueTextFactory;
        private final Runnable afterChange;

        protected ColorSlider(int x, int y, int width, Component label, float value,
                java.util.function.Consumer<Float> update,
                java.util.function.Function<Float, Component> valueTextFactory, Runnable afterChange) {
            super(x, y, width, 20, CommonComponents.EMPTY, value);
            this.label = label;
            this.update = update;
            this.valueTextFactory = valueTextFactory;
            this.afterChange = afterChange;
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(CommonComponents.optionNameValue(label, valueTextFactory.apply((float) value)));
        }

        @Override
        protected void applyValue() {
            update.accept((float) value);
            afterChange.run();
        }

        public void setSliderValue(float value) {
            this.value = Mth.clamp(value, 0f, 1f);
            updateMessage();
        }
    }
}
