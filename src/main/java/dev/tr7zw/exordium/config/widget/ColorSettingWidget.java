package dev.tr7zw.exordium.config.widget;

import java.util.function.IntSupplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ColorSettingWidget extends AbstractWidget {

    private static final int PREVIEW_WIDTH = 38;

    private final IntSupplier colorSupplier;
    private final Runnable onPress;

    public ColorSettingWidget(int x, int y, int width, int height, Component message, IntSupplier colorSupplier,
            Runnable onPress) {
        super(x, y, width, height, message);
        this.colorSupplier = colorSupplier;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int color = colorSupplier.getAsInt();
        int borderColor = isHoveredOrFocused() ? 0xFFFFFFFF : 0xFFA0A0A0;
        int previewLeft = getRight() - PREVIEW_WIDTH - 4;
        guiGraphics.fill(getX(), getY(), getRight(), getBottom(), borderColor);
        guiGraphics.fill(getX() + 1, getY() + 1, getRight() - 1, getBottom() - 1, 0xFF000000);
        guiGraphics.fill(previewLeft, getY() + 3, getRight() - 4, getBottom() - 3, color);

        Minecraft minecraft = Minecraft.getInstance();
        String label = minecraft.font.plainSubstrByWidth(getMessage().getString(), previewLeft - getX() - 10);
        guiGraphics.drawString(minecraft.font, label, getX() + 6, getY() + 6, 0xFFFFFF, false);
    }

    @Override
    public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        onPress.run();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
