package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.components.HudBufferSession;
import dev.tr7zw.exordium.components.HudCategory;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

@Mixin(Gui.class)
public class GuiMixin {

    @WrapOperation(method = "renderChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/ChatComponent;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;IIIZZ)V"))
    private void exordium$renderChat(ChatComponent chatComponent, GuiGraphics guiGraphics, Font font, int tickCount,
            int j, int k, boolean focused, boolean hidden, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(chatComponent, guiGraphics, font, tickCount, j, k, focused, hidden);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.CHAT, guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.CHAT, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(chatComponent, guiGraphics, font, tickCount, j, k, focused, hidden);
        }
        if (session.shouldCapture()) {
            operation.call(chatComponent, session.captureGraphics(), font, tickCount, j, k, focused, hidden);
        }
    }

    @WrapOperation(method = "renderTabList", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/world/scores/Scoreboard;Lnet/minecraft/world/scores/Objective;)V"))
    private void exordium$renderTabList(PlayerTabOverlay playerTabOverlay, GuiGraphics guiGraphics, int screenWidth,
            Scoreboard scoreboard, Objective objective, Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(playerTabOverlay, guiGraphics, screenWidth, scoreboard, objective);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.TABLIST,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.TABLIST, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(playerTabOverlay, guiGraphics, screenWidth, scoreboard, objective);
        }
        if (session.shouldCapture()) {
            operation.call(playerTabOverlay, session.captureGraphics(), screenWidth, scoreboard, objective);
        }
    }

    @WrapOperation(method = "renderBossOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;render(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void exordium$renderBossOverlay(BossHealthOverlay bossHealthOverlay, GuiGraphics guiGraphics,
            Operation<Void> operation) {
        if (ExordiumModBase.instance == null) {
            operation.call(bossHealthOverlay, guiGraphics);
            return;
        }
        HudBufferSession session = ExordiumModBase.instance.getHudBufferManager().prepare(HudCategory.BOSSBAR,
                guiGraphics);
        if (session.shouldDrawCache()) {
            ExordiumModBase.instance.getHudBufferManager().drawCached(HudCategory.BOSSBAR, guiGraphics);
        }
        if (session.shouldRenderLive()) {
            operation.call(bossHealthOverlay, guiGraphics);
        }
        if (session.shouldCapture()) {
            operation.call(bossHealthOverlay, session.captureGraphics());
        }
    }

}
