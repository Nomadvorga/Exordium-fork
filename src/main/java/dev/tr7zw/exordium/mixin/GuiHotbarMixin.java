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
public class GuiHotbarMixin {

    @Unique
    private boolean exordium$hotbarBuffered = false;

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    private void exordium$resetHotbarState(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        exordium$hotbarBuffered = false;
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V") })
    private void renderHotbarWrapper(Gui gui, GuiGraphics guiGraphics, DeltaTracker deltaTracker,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics, deltaTracker);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.HOTBAR,
                guiGraphics);
        if (session.shouldDrawCache() && !exordium$hotbarBuffered) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.HOTBAR, guiGraphics);
            exordium$hotbarBuffered = true;
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics, deltaTracker);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics(), deltaTracker);
        }
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V") })
    private void exordium$renderSelectedItemName(Gui gui, GuiGraphics guiGraphics, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.HOTBAR,
                guiGraphics);
        if (session.shouldDrawCache() && !exordium$hotbarBuffered) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.HOTBAR, guiGraphics);
            exordium$hotbarBuffered = true;
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics());
        }
    }

}
