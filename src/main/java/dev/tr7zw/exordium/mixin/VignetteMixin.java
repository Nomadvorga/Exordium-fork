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
import net.minecraft.world.entity.Entity;

@Mixin(Gui.class)
public class VignetteMixin {

    //#if MC >= 12005
    @WrapOperation(method = "renderCameraOverlays", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"), })
    //#else
    //$$ @WrapOperation(method = "render", at = {
    //$$        @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVignette(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/Entity;)V"), })
    //#endif
    private void renderVignetteWrapper(Gui gui, GuiGraphics guiGraphics, Entity entity,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics, entity);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.VIGNETTE,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.VIGNETTE, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics, entity);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics(), entity);
        }
    }

}
