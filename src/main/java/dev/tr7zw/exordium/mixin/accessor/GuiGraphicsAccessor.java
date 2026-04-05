package dev.tr7zw.exordium.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;

import net.minecraft.client.gui.GuiGraphics;

@Mixin(GuiGraphics.class)
public interface GuiGraphicsAccessor {

    @Accessor("pendingCursor")
    CursorType exordium$getPendingCursor();

    @Accessor("pendingCursor")
    void exordium$setPendingCursor(CursorType pendingCursor);

    @Invoker("submitBlit")
    void exordium$submitBlit(RenderPipeline pipeline, GpuTextureView textureView, GpuSampler sampler, int x0, int y0,
            int x1, int y1, float u0, float u1, float v0, float v1, int color);

}
