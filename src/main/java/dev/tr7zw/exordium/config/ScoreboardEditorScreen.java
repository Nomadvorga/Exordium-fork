package dev.tr7zw.exordium.config;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.ScoreboardRenderHelper;
import dev.tr7zw.exordium.util.ScoreboardRenderHelper.ScoreboardBounds;
import dev.tr7zw.exordium.util.ScoreboardRenderHelper.ScoreboardLayout;
import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

public class ScoreboardEditorScreen extends Screen {

    private final Screen parent;
    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;
    private boolean dirty = false;
    private boolean lastLeftMouseDown = false;
    private boolean lastRightMouseDown = false;
    private boolean lastResetKeyDown = false;

    public ScoreboardEditorScreen(Screen parent) {
        super(Component.translatable("text.exordium.scoreboard_editor.title"));
        this.parent = parent;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        handleEditorInput(mouseX, mouseY);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        Config config = ExordiumModBase.instance.config;
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 18, 0xFFFFFF);
        guiGraphics.drawCenteredString(this.font,
                Component.translatable("text.exordium.scoreboard_editor.status", config.scoreboardScale,
                        config.scoreboardOpacity),
                this.width / 2, 34, 0xB8E0FF);

        ScoreboardBounds bounds = ScoreboardRenderHelper.render(guiGraphics, this.font, this.width, this.height, config,
                true);
        if (bounds != null) {
            boolean hovered = bounds.contains(mouseX, mouseY);
            if (dragging || hovered) {
                int outlineColor = dragging ? 0xC0FFAA00 : 0x80FFFFFF;
                guiGraphics.fill(bounds.x() - 1, bounds.y() - 1, bounds.x() + bounds.width() + 1, bounds.y(),
                        outlineColor);
                guiGraphics.fill(bounds.x() - 1, bounds.y() + bounds.height(), bounds.x() + bounds.width() + 1,
                        bounds.y() + bounds.height() + 1, outlineColor);
                guiGraphics.fill(bounds.x() - 1, bounds.y(), bounds.x(), bounds.y() + bounds.height(), outlineColor);
                guiGraphics.fill(bounds.x() + bounds.width(), bounds.y(), bounds.x() + bounds.width() + 1,
                        bounds.y() + bounds.height(), outlineColor);
            }
        }

        renderHintPanel(guiGraphics);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        Config config = ExordiumModBase.instance.config;
        long window = this.minecraft.getWindow().handle();
        boolean shiftDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        if (shiftDown) {
            config.scoreboardOpacity = Mth.clamp(config.scoreboardOpacity + (verticalAmount > 0 ? 5 : -5), 10, 100);
        } else {
            config.scoreboardScale = Mth.clamp(config.scoreboardScale + (verticalAmount > 0 ? 5 : -5), 50, 200);
        }
        dirty = true;
        writeConfig();
        return true;
    }

    @Override
    public void onClose() {
        writeConfig();
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void resetScoreboardConfig() {
        Config config = ExordiumModBase.instance.config;
        config.scoreboardCustomPosition = false;
        config.scoreboardX = 0;
        config.scoreboardY = 0;
        config.scoreboardScale = 100;
        config.scoreboardOpacity = 100;
        dirty = true;
    }

    private void writeConfig() {
        if (!dirty) {
            return;
        }
        ExordiumModBase.instance.writeConfig();
        dirty = false;
    }

    private ScoreboardBounds getBounds(Config config) {
        ScoreboardLayout layout = ScoreboardRenderHelper.layout(this.font, this.width, this.height, config,
                ScoreboardRenderHelper.getCurrentState(true));
        return layout == null ? null : layout.bounds();
    }

    private void renderHintPanel(GuiGraphics guiGraphics) {
        Component[] lines = new Component[] {
                Component.translatable("text.exordium.scoreboard_editor.hint.drag"),
                Component.translatable("text.exordium.scoreboard_editor.hint.size"),
                Component.translatable("text.exordium.scoreboard_editor.hint.opacity"),
                Component.translatable("text.exordium.scoreboard_editor.hint.reset")
        };
        int lineHeight = 11;
        int x = 6;
        int y = this.height - lines.length * lineHeight - 6;

        for (int i = 0; i < lines.length; i++) {
            int color = (i == lines.length - 1) ? 0x80AAAAAA : 0x90CCCCCC;
            guiGraphics.drawString(this.font, lines[i], x, y + i * lineHeight, color, false);
        }
    }

    private void handleEditorInput(int mouseX, int mouseY) {
        long window = this.minecraft.getWindow().handle();
        boolean leftMouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean rightMouseDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        boolean resetKeyDown = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_R) == GLFW.GLFW_PRESS;

        Config config = ExordiumModBase.instance.config;
        ScoreboardBounds bounds = getBounds(config);

        if (!lastLeftMouseDown && leftMouseDown && bounds != null && bounds.contains(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = mouseX - bounds.x();
            dragOffsetY = mouseY - bounds.y();
            config.scoreboardCustomPosition = true;
        }

        if (dragging && leftMouseDown && bounds != null) {
            int maxX = Math.max(0, this.width - bounds.width());
            int maxY = Math.max(0, this.height - bounds.height());
            config.scoreboardX = Mth.clamp(mouseX - dragOffsetX, 0, maxX);
            config.scoreboardY = Mth.clamp(mouseY - dragOffsetY, 0, maxY);
            dirty = true;
        }

        if (dragging && lastLeftMouseDown && !leftMouseDown) {
            dragging = false;
            writeConfig();
        }

        if (!lastRightMouseDown && rightMouseDown && bounds != null && bounds.contains(mouseX, mouseY)) {
            resetScoreboardConfig();
            writeConfig();
        }

        if (!lastResetKeyDown && resetKeyDown) {
            resetScoreboardConfig();
            writeConfig();
        }

        lastLeftMouseDown = leftMouseDown;
        lastRightMouseDown = rightMouseDown;
        lastResetKeyDown = resetKeyDown;
    }
}
