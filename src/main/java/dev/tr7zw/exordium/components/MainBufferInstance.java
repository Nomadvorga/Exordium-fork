package dev.tr7zw.exordium.components;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.platform.cursor.CursorType;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.mixin.accessor.GuiGraphicsAccessor;
import dev.tr7zw.exordium.mixin.accessor.GuiRendererAccessor;
import dev.tr7zw.exordium.util.PacingTracker;
import dev.tr7zw.exordium.versionless.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;

public final class MainBufferInstance {

    private final BufferedComponent buffer = new BufferedComponent();
    private final PacingTracker pacing = new PacingTracker();
    private final Supplier<Config.ComponentSettings> settings;
    private GuiRenderState captureRenderState;
    private GuiRenderer captureRenderer;
    private boolean captureQueued = false;
    private boolean drawCached = false;
    private boolean hasCache = false;
    private Screen lastScreen = null;
    private CursorType cachedCursor = null;

    public MainBufferInstance(Supplier<Config.ComponentSettings> settings) {
        this.settings = settings;
    }

    public boolean enabled(Screen screen) {
        Config.ComponentSettings componentSettings = settings.get();
        return componentSettings != null && componentSettings.isEnabled() && screen != null
                && !(screen instanceof ChatScreen) && Minecraft.getInstance().level != null
                && ExordiumModBase.instance.isInitialized();
    }

    public GuiGraphics beginScreenCapture(Screen screen, GuiGraphics originalGraphics, int mouseX, int mouseY,
            GuiRenderer mainGuiRenderer) {
        drawCached = false;
        captureQueued = false;
        if (!enabled(screen)) {
            lastScreen = null;
            return originalGraphics;
        }
        boolean updateFrame = !hasCache || screen != lastScreen || buffer.screenChanged() || pacing.isCooldownOver();
        pacing.clearFlag();
        lastScreen = screen;
        if (updateFrame) {
            ensureCaptureRenderer(mainGuiRenderer);
            captureQueued = true;
            return new GuiGraphics(Minecraft.getInstance(), captureRenderState, mouseX, mouseY);
        }
        drawCached = hasCache;
        applyCachedCursor(originalGraphics);
        return null;
    }

    public void completeScreenCapture(GuiGraphics originalGraphics, GuiGraphics captureGraphics) {
        CursorType pendingCursor = ((GuiGraphicsAccessor) (Object) captureGraphics).exordium$getPendingCursor();
        cachedCursor = pendingCursor;
        ((GuiGraphicsAccessor) (Object) originalGraphics).exordium$setPendingCursor(pendingCursor);
    }

    public void renderPending(GpuBufferSlice gpuBufferSlice) {
        if (drawCached && hasCache) {
            drawCached = false;
            buffer.getGuiTarget()
                    .blitAndBlendToTexture(Minecraft.getInstance().getMainRenderTarget().getColorTextureView());
            return;
        }
        if (!captureQueued || captureRenderer == null) {
            return;
        }
        captureQueued = false;
        buffer.captureComponent();
        captureRenderer.render(gpuBufferSlice);
        captureRenderer.incrementFrameNumber();
        buffer.finishCapture();
        hasCache = true;
        pacing.setCooldown(System.currentTimeMillis() + (1000L / Math.max(1, settings.get().getMaxFps())));
        buffer.getGuiTarget()
                .blitAndBlendToTexture(Minecraft.getInstance().getMainRenderTarget().getColorTextureView());
    }

    private void ensureCaptureRenderer(GuiRenderer mainGuiRenderer) {
        if (captureRenderer != null) {
            return;
        }
        GuiRendererAccessor accessor = (GuiRendererAccessor) mainGuiRenderer;
        Map<?, PictureInPictureRenderer<?>> pictureInPictureRenderers = accessor.exordium$getPictureInPictureRenderers();
        captureRenderState = new GuiRenderState();
        captureRenderer = new GuiRenderer(captureRenderState, accessor.exordium$getBufferSource(),
                accessor.exordium$getSubmitNodeCollector(), accessor.exordium$getFeatureRenderDispatcher(),
                new ArrayList<>(pictureInPictureRenderers.values()));
    }

    private void applyCachedCursor(GuiGraphics guiGraphics) {
        if (cachedCursor != null) {
            ((GuiGraphicsAccessor) (Object) guiGraphics).exordium$setPendingCursor(cachedCursor);
        }
    }

}
