package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.HudBufferSession;
import dev.tr7zw.exordium.components.HudCategory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class CrosshairMixin {

    @WrapOperation(method = "render", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V") })
    private void renderCrosshairWrapper(Gui gui, GuiGraphics guiGraphics, DeltaTracker delta,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics, delta);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.CROSSHAIR,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.CROSSHAIR, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics, delta);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics(), delta);
        }
    }

}
