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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;

@Mixin(Gui.class)
public class GuiExperienceMixin {

    @Unique
    private boolean exordium$experienceBuffered = false;

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    private void exordium$resetExperienceState(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        exordium$experienceBuffered = false;
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"), })
    private void renderExperienceBarWrapper(ContextualBarRenderer renderer, GuiGraphics guiGraphics,
            DeltaTracker deltaTracker, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(renderer, guiGraphics, deltaTracker);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.EXPERIENCE,
                guiGraphics);
        if (session.shouldDrawCache() && !exordium$experienceBuffered) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.EXPERIENCE, guiGraphics);
            exordium$experienceBuffered = true;
        }
        if (session.shouldRenderLive()) {
            operation.call(renderer, guiGraphics, deltaTracker);
        }
        if (session.shouldCapture()) {
            operation.call(renderer, session.captureGraphics(), deltaTracker);
        }
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;renderExperienceLevel(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"), })
    private void renderExperienceLevelWrapper(GuiGraphics guiGraphics, Font font, int level,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(guiGraphics, font, level);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.EXPERIENCE,
                guiGraphics);
        if (session.shouldRenderLive()) {
            operation.call(guiGraphics, font, level);
        }
        if (session.shouldCapture()) {
            operation.call(session.captureGraphics(), font, level);
        }
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"), })
    private void exordium$renderExperienceForeground(ContextualBarRenderer renderer, GuiGraphics guiGraphics,
            DeltaTracker deltaTracker, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(renderer, guiGraphics, deltaTracker);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.EXPERIENCE,
                guiGraphics);
        if (session.shouldRenderLive()) {
            operation.call(renderer, guiGraphics, deltaTracker);
        }
        if (session.shouldCapture()) {
            operation.call(renderer, session.captureGraphics(), deltaTracker);
        }
    }

}
