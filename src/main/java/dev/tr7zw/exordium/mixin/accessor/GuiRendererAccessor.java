package dev.tr7zw.exordium.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;

import java.util.Map;

@Mixin(GuiRenderer.class)
public interface GuiRendererAccessor {

    @Accessor("bufferSource")
    MultiBufferSource.BufferSource exordium$getBufferSource();

    @Accessor("submitNodeCollector")
    SubmitNodeCollector exordium$getSubmitNodeCollector();

    @Accessor("featureRenderDispatcher")
    FeatureRenderDispatcher exordium$getFeatureRenderDispatcher();

    @Accessor("pictureInPictureRenderers")
    Map<?, PictureInPictureRenderer<?>> exordium$getPictureInPictureRenderers();

}
