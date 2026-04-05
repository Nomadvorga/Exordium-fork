package dev.tr7zw.exordium.render;

import java.util.function.Supplier;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.util.IBufferedComponent;
import dev.tr7zw.exordium.util.ScreenTracker;
import dev.tr7zw.exordium.util.rendersystem.BlendStateHolder;
import dev.tr7zw.exordium.util.rendersystem.DepthStateHolder;
import dev.tr7zw.exordium.util.rendersystem.MultiStateHolder;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.Getter;
import net.minecraft.client.Minecraft;

public class BufferedComponent implements IBufferedComponent {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private final Supplier<Config.ComponentSettings> settings;
    //#if MC >= 12102
    private final RenderTarget guiTarget = new TextureTarget("Exordium GUI Buffer", 100, 100, true);
    //#else
    //$$private final RenderTarget guiTarget = new TextureTarget(100, 100, true, false);
    //#endif
    private final ScreenTracker screenTracker = new ScreenTracker(guiTarget);
    private final MultiStateHolder stateHolder = new MultiStateHolder(new BlendStateHolder(), new DepthStateHolder());
    private boolean forceBlending = false;
    private boolean captureUnsupported = false;

    public BufferedComponent(Supplier<Config.ComponentSettings> settings) {
        this(false, settings);
    }

    public BufferedComponent(boolean forceBlending, Supplier<Config.ComponentSettings> settings) {
        this.forceBlending = forceBlending;
        this.settings = settings;
    }

    public void captureComponent() {
        // Check for Screen size/scaling changes
        if (screenTracker.hasChanged()) {
            screenTracker.updateState();
        }

        clearTarget();

        ExordiumModBase.correctBlendMode();
        if (forceBlending || settings.get().isForceBlend()) {
            ExordiumModBase.setForceBlend(true);
        }

        ExordiumModBase.instance.beginTemporaryScreenOverwrite(guiTarget);
    }

    public void renderBuffer() {
        ExordiumModBase.instance.getDelayedRenderCallManager().addBufferedComponent(this);
        // set the blendstate to what it would be if the normal render logic had run
        stateHolder.apply();
    }

    public void finishCapture() {
        boolean capturedOffscreen = ExordiumModBase.instance.endTemporaryScreenOverwrite();
        if (forceBlending || settings.get().isForceBlend()) {
            ExordiumModBase.setForceBlend(false);
        }
        if (!capturedOffscreen) {
            captureUnsupported = true;
            return;
        }
        renderBuffer();
    }

    private void clearTarget() {
        if (guiTarget.useDepth) {
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(guiTarget.getColorTexture(), 0,
                    guiTarget.getDepthTexture(), 1.0);
        } else {
            RenderSystem.getDevice().createCommandEncoder().clearColorTexture(guiTarget.getColorTexture(), 0);
        }
    }

    public RenderTarget getRenderTarget() {
        return guiTarget;
    }

    public boolean needsBlendstateSample() {
        return !stateHolder.isFetched();
    }

    public void captureBlendstateSample() {
        if (needsBlendstateSample()) {
            stateHolder.fetch();
        }
    }

    public boolean screenChanged() {
        return screenTracker.hasChanged();
    }

    public boolean supportsBuffering() {
        return !captureUnsupported;
    }

    private boolean isCrosshair = false;
    @Override
    public boolean getCrosshair() {
        return this.isCrosshair;
    }

    @Override
    public void setCrosshair(boolean isCrosshair) {
        this.isCrosshair = isCrosshair;
    }

}
