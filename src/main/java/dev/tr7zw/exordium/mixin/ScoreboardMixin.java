package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.HudBufferSession;
import dev.tr7zw.exordium.components.HudCategory;
import dev.tr7zw.exordium.config.ScoreboardEditorScreen;
import dev.tr7zw.exordium.util.ScoreboardRenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.scores.Objective;

@Mixin(Gui.class)
public class ScoreboardMixin {

    //#if MC >= 12005
    @WrapOperation(method = "renderScoreboardSidebar", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V"), })
    //#else
    //$$ @WrapOperation(method = "render", at = {
    //$$         @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;displayScoreboardSidebar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/scores/Objective;)V"), })
    //#endif
    private void displayScoreboardSidebarWrapper(Gui gui, GuiGraphics guiGraphics, Objective objective,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(gui, guiGraphics, objective);
            return;
        }
        if (Minecraft.getInstance().screen instanceof ScoreboardEditorScreen) {
            return;
        }
        if (!ExordiumModBase.instance.config.useCustomScoreboardLayout()) {
            renderVanillaOrBuffered(gui, guiGraphics, objective, operation);
            return;
        }
        if (ScoreboardRenderHelper.getCurrentState(false) == null) {
            renderVanillaOrBuffered(gui, guiGraphics, objective, operation);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.SCOREBOARD,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.SCOREBOARD, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            ScoreboardRenderHelper.render(guiGraphics, Minecraft.getInstance().font, guiGraphics.guiWidth(),
                    guiGraphics.guiHeight(), ExordiumModBase.instance.config, false);
        }
        if (session.shouldCapture()) {
            GuiGraphics captureGraphics = session.captureGraphics();
            ScoreboardRenderHelper.render(captureGraphics, Minecraft.getInstance().font, captureGraphics.guiWidth(),
                    captureGraphics.guiHeight(), ExordiumModBase.instance.config, false);
        }
    }

    private void renderVanillaOrBuffered(Gui gui, GuiGraphics guiGraphics, Objective objective, Operation<Void> operation) {
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.SCOREBOARD,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.SCOREBOARD, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(gui, guiGraphics, objective);
        }
        if (session.shouldCapture()) {
            operation.call(gui, session.captureGraphics(), objective);
        }
    }

}
