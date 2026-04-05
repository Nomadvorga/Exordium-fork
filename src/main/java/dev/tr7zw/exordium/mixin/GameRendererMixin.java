package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void exordium$beginHudFrame(DeltaTracker deltaTracker, boolean tick, CallbackInfo ci) {
        if (ExordiumModBase.instance != null) {
            ExordiumModBase.instance.getHudBufferManager().beginFrame();
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/render/GuiRenderer;render(Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V"))
    private void exordium$renderPendingCategories(GuiRenderer guiRenderer, GpuBufferSlice gpuBufferSlice,
            Operation<Void> operation) {
        operation.call(guiRenderer, gpuBufferSlice);
        if (ExordiumModBase.instance != null) {
            ExordiumModBase.instance.getHudBufferManager().renderPending(guiRenderer, gpuBufferSlice);
        }
    }

}
