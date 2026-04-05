package dev.tr7zw.exordium.mixin.support;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.BufferInstance;
import dev.tr7zw.exordium.components.support.XaerosMinimapComponent;
import net.minecraft.client.gui.GuiGraphics;

@Pseudo
@Mixin(targets = "xaero.common.events.ModClientEvents")
public class XaerosMinimapMixin {

    @WrapOperation(method = "handleRenderModOverlay", at = { @At(value = "INVOKE", target = "render") }, remap = false)
    private void renderMinimap(Object renderer, Object hud, GuiGraphics guiGraphics, float tick,
            final Operation<Void> operation) {
        BufferInstance<Void> buffer = ExordiumModBase.instance.getBufferManager()
                .getBufferInstance(XaerosMinimapComponent.getId(), Void.class);
        if (!buffer.renderBuffer(null, guiGraphics)) {
            operation.call(renderer, hud, guiGraphics, tick);
        }
        buffer.postRender(null, guiGraphics);
    }

}
