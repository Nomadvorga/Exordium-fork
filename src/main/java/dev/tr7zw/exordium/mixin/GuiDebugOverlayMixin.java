package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.HudBufferSession;
import dev.tr7zw.exordium.components.HudCategory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;

@Mixin(Gui.class)
public class GuiDebugOverlayMixin {

    //#if MC >= 12005
    @WrapOperation(method = "renderDebugOverlay", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //#else
    //$$@WrapOperation(method = "render", at = {
    //$$        @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    //#endif
    private void renderDebugOverlayWrapper(DebugScreenOverlay overlay, GuiGraphics guiGraphics,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(overlay, guiGraphics);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.DEBUG,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.DEBUG, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(overlay, guiGraphics);
        }
        if (session.shouldCapture()) {
            operation.call(overlay, session.captureGraphics());
        }
    }

}
