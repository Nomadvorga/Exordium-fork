package dev.tr7zw.exordium.util;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public final class ScoreboardRenderHelper {

    private static final int HORIZONTAL_PADDING = 3;
    private static final int ROW_HEIGHT = 9;
    private static final int DEFAULT_MARGIN = 3;
    private static final int TITLE_GAP = 0;

    private ScoreboardRenderHelper() {
    }

    public static ScoreboardStateView getCurrentState(boolean previewFallback) {
        ScoreboardHelper.ScoreboardState state = null;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null && minecraft.player != null) {
            state = ScoreboardHelper.getScoreboardData();
        }
        if (state == null && previewFallback) {
            state = createPreviewState();
        }
        return state == null ? null : new ScoreboardStateView(state.title(), state.entries());
    }

    public static ScoreboardBounds render(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight,
            Config config, boolean previewFallback) {
        ScoreboardStateView state = getCurrentState(previewFallback);
        if (state == null) {
            return null;
        }
        return render(guiGraphics, font, screenWidth, screenHeight, config, state);
    }

    public static ScoreboardBounds render(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight,
            Config config, ScoreboardStateView state) {
        ScoreboardLayout layout = layout(font, screenWidth, screenHeight, config, state);
        if (layout == null) {
            return null;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(layout.x(), layout.y());
        guiGraphics.pose().scale(layout.scale(), layout.scale());

        int y = 0;
        guiGraphics.fill(0, y, layout.baseWidth(), y + ROW_HEIGHT, layout.titleBackgroundColor());
        guiGraphics.drawCenteredString(font, state.title(), layout.baseWidth() / 2, y + 1, layout.textColor());
        y += ROW_HEIGHT;

        boolean hideScores = config.scoreboardHideScores;
        int rowY = y;
        for (Pair<Component, Component> entry : state.entries()) {
            Component score = entry.getFirst();
            Component name = entry.getSecond();
            guiGraphics.fill(0, rowY, layout.baseWidth(), rowY + ROW_HEIGHT, layout.rowBackgroundColor());
            guiGraphics.drawString(font, name, HORIZONTAL_PADDING, rowY + 1, layout.textColor(), false);
            if (!hideScores && score != null) {
                int scoreWidth = font.width(score);
                int scoreX = layout.baseWidth() - HORIZONTAL_PADDING - scoreWidth;
                guiGraphics.drawString(font, score, scoreX, rowY + 1, layout.textColor(), false);
            }
            rowY += ROW_HEIGHT;
        }

        guiGraphics.pose().popMatrix();
        return layout.bounds();
    }

    public static ScoreboardLayout layout(Font font, int screenWidth, int screenHeight, Config config,
            ScoreboardStateView state) {
        if (state == null) {
            return null;
        }
        int contentWidth = font.width(state.title());
        for (Pair<Component, Component> entry : state.entries()) {
            int lineWidth = font.width(entry.getSecond());
            if (entry.getFirst() != null && !config.scoreboardHideScores) {
                lineWidth += 2 + font.width(entry.getFirst());
            }
            contentWidth = Math.max(contentWidth, lineWidth);
        }

        int baseWidth = contentWidth + HORIZONTAL_PADDING * 2;
        int baseHeight = ROW_HEIGHT * (state.entries().size() + 1) + TITLE_GAP;
        float scale = Mth.clamp(config.scoreboardScale / 100f, 0.5f, 2.0f);
        int scaledWidth = Math.max(1, Math.round(baseWidth * scale));
        int scaledHeight = Math.max(1, Math.round(baseHeight * scale));
        int scaledEntriesHeight = Math.max(1, Math.round((state.entries().size() * ROW_HEIGHT + TITLE_GAP) * scale));

        int defaultX = screenWidth - scaledWidth - 1;
        int defaultY = screenHeight / 2 - scaledHeight / 2;
        int maxX = Math.max(DEFAULT_MARGIN, screenWidth - scaledWidth);
        int maxY = Math.max(DEFAULT_MARGIN, screenHeight - scaledHeight);
        int x = config.scoreboardCustomPosition ? Mth.clamp(config.scoreboardX, 0, maxX) : defaultX;
        int y = config.scoreboardCustomPosition ? Mth.clamp(config.scoreboardY, 0, maxY) : defaultY;

        float opacity = Mth.clamp(config.scoreboardOpacity / 100f, 0.1f, 1f);
        int titleBackgroundColor = alphaColor(0x000000, Math.round(88 * opacity));
        int rowBackgroundColor = alphaColor(0x000000, Math.round(72 * opacity));
        int textAlpha = Mth.clamp(Math.round(96 + 159 * opacity), 96, 255);
        int textColor = alphaColor(0xFFFFFF, textAlpha);

        return new ScoreboardLayout(baseWidth, baseHeight, x, y, scale, textColor, rowBackgroundColor,
                titleBackgroundColor, new ScoreboardBounds(x, y, scaledWidth, scaledHeight));
    }

    private static int alphaColor(int rgb, int alpha) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static ScoreboardHelper.ScoreboardState createPreviewState() {
        return new ScoreboardHelper.ScoreboardState(Component.literal("Scoreboard"),
                List.of(
                        Pair.of(Component.literal("12"), Component.literal("ExampleOne")),
                        Pair.of(Component.literal("8"), Component.literal("ExampleTwo")),
                        Pair.of(Component.literal("3"), Component.literal("ExampleThree"))));
    }

    public record ScoreboardBounds(int x, int y, int width, int height) {

        public boolean contains(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    public record ScoreboardLayout(int baseWidth, int baseHeight, int x, int y, float scale, int textColor,
            int rowBackgroundColor, int titleBackgroundColor, ScoreboardBounds bounds) {
    }

    public record ScoreboardStateView(Component title, List<Pair<Component, Component>> entries) {
    }
}
