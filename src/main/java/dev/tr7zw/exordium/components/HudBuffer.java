package dev.tr7zw.exordium.components;

import java.util.List;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.mixin.accessor.GuiGraphicsAccessor;
import dev.tr7zw.exordium.mixin.accessor.GuiRendererAccessor;
import dev.tr7zw.exordium.util.PacingTracker;
import dev.tr7zw.exordium.util.ScreenTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.RenderPipelines;

public class HudBuffer {

    private final HudCategory category;
    private final GuiRenderState renderState = new GuiRenderState();
    private final RenderTarget renderTarget;
    private final ScreenTracker screenTracker;
    private final PacingTracker pacing = new PacingTracker();

    private GuiRenderer renderer;
    private boolean framePrepared = false;
    private boolean pendingRender = false;
    private boolean hasCache = false;
    private HudBufferSession session = HudBufferSession.PASS_THROUGH;

    public HudBuffer(HudCategory category) {
        this.category = category;
        this.renderTarget = new TextureTarget("Exordium " + category.name(), 100, 100, true);
        this.screenTracker = new ScreenTracker(renderTarget);
    }

    public void beginFrame() {
        framePrepared = false;
        pendingRender = false;
        session = HudBufferSession.PASS_THROUGH;
        pacing.clearFlag();
    }

    public HudBufferSession prepare(GuiGraphics guiGraphics) {
        if (framePrepared) {
            return session;
        }
        framePrepared = true;
        if (!isActive()) {
            session = HudBufferSession.PASS_THROUGH;
            return session;
        }
        boolean screenChanged = screenTracker.hasChanged();
        if (screenChanged) {
            screenTracker.updateState();
            hasCache = false;
        }
        boolean updateFrame = !hasCache || screenChanged || pacing.isCooldownOver();
        if (updateFrame) {
            renderState.reset();
            session = new HudBufferSession(
                    hasCache ? HudBufferSession.Mode.CACHE_AND_CAPTURE : HudBufferSession.Mode.LIVE_AND_CAPTURE,
                    new GuiGraphics(Minecraft.getInstance(), renderState, guiGraphics.guiWidth(),
                            guiGraphics.guiHeight()));
            pendingRender = true;
            return session;
        }
        session = new HudBufferSession(HudBufferSession.Mode.CACHE_ONLY, null);
        return session;
    }

    public void drawCached(GuiGraphics guiGraphics) {
        if (!hasCache || renderTarget.getColorTextureView() == null) {
            return;
        }
        // HUD is first composited into a transparent target, so it needs the
        // premultiplied-alpha GUI pipeline when drawn back to the main screen.
        ((GuiGraphicsAccessor) guiGraphics).exordium$submitBlit(RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
                renderTarget.getColorTextureView(),
                RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST), 0, 0, guiGraphics.guiWidth(),
                guiGraphics.guiHeight(), 0f, 1f, 1f, 0f, -1);
    }

    public void renderPending(GuiRenderer sourceRenderer, GpuBufferSlice fogBuffer) {
        if (!pendingRender) {
            return;
        }
        if (renderer == null) {
            GuiRendererAccessor accessor = (GuiRendererAccessor) sourceRenderer;
            renderer = new GuiRenderer(renderState, accessor.exordium$getBufferSource(),
                    accessor.exordium$getSubmitNodeCollector(), accessor.exordium$getFeatureRenderDispatcher(),
                    List.of());
        }
        clearTarget();
        ExordiumModBase.instance.beginTemporaryScreenOverwrite(renderTarget);
        try {
            renderer.render(fogBuffer);
        } finally {
            ExordiumModBase.instance.endTemporaryScreenOverwrite();
        }
        renderer.incrementFrameNumber();
        hasCache = true;
        pendingRender = false;
        pacing.setCooldown(System.currentTimeMillis()
                + (1000L / Math.max(1, category.getSettings() != null ? category.getSettings().getMaxFps() : 20)));
    }

    private void clearTarget() {
        if (renderTarget.useDepth) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(renderTarget.getColorTexture(), 0,
                    renderTarget.getDepthTexture(), 1.0f);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(renderTarget.getColorTexture(), 0);
        }
    }

    private boolean isActive() {
        if (ExordiumModBase.instance == null || !ExordiumModBase.instance.isInitialized()) {
            return false;
        }
        if (Minecraft.getInstance().screen != null || Minecraft.getInstance().level == null) {
            return false;
        }
        var settings = category.getSettings();
        return settings != null && settings.isEnabled();
    }

}
