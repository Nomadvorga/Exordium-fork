package dev.tr7zw.exordium.config.widget;

import java.util.Locale;
import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ColorPreviewWidget extends AbstractWidget {

    private final IntSupplier colorSupplier;
    private final Runnable onPress;

    public ColorPreviewWidget(int x, int y, int width, int height, IntSupplier colorSupplier, Runnable onPress) {
        super(x, y, width, height, Component.translatable("text.exordium.setting.selection.preview"));
        this.colorSupplier = colorSupplier;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int color = colorSupplier.getAsInt();
        int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFA0A0A0;
        guiGraphics.fill(getX(), getY(), getRight(), getBottom(), borderColor);
        guiGraphics.fill(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1, 0xFF000000);
        guiGraphics.fill(getX() + 2, getY() + 2, getRight() - 2, getBottom() - 2, color);

        String hex = String.format(Locale.ROOT, "#%02X%02X%02X", color >> 16 & 255, color >> 8 & 255, color & 255);
        int textColor = getContrastTextColor(color);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, hex, getX() + getWidth() / 2,
                getY() + (getHeight() - 8) / 2, textColor);
    }

    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }

    private static int getContrastTextColor(int color) {
        int red = color >> 16 & 255;
        int green = color >> 8 & 255;
        int blue = color & 255;
        int luminance = (red * 299 + green * 587 + blue * 114) / 1000;
        return luminance >= 140 ? 0xFF101010 : 0xFFFFFFFF;
    }

}
