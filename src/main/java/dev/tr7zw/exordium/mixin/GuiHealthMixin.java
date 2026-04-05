package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.HudBufferSession;
import dev.tr7zw.exordium.components.HudCategory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;

@Mixin(Gui.class)
public class GuiHealthMixin {

    @Unique
    private boolean exordium$healthBuffered = false;

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    private void exordium$resetHealthState(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        exordium$healthBuffered = false;
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderPlayerHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"), })
    private void renderPlayerHealthWrapper(Gui gui, GuiGraphics guiGraphics, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.HEALTH,
                guiGraphics);
        if (session.shouldDrawCache() && !exordium$healthBuffered) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.HEALTH, guiGraphics);
            exordium$healthBuffered = true;
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics());
        }
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void renderVehicleHealthHead(Gui gui, GuiGraphics guiGraphics, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.HEALTH,
                guiGraphics);
        if (session.shouldDrawCache() && !exordium$healthBuffered) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.HEALTH, guiGraphics);
            exordium$healthBuffered = true;
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics());
        }
    }

}
