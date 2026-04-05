package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class TextHighlightMixin {

    @Inject(method = "textHighlight", at = @At("HEAD"), cancellable = true)
    private void exordium$customSelectionColor(int x1, int y1, int x2, int y2, boolean invertHighlightedTextColor,
            CallbackInfo ci) {
        if (ExordiumModBase.instance == null || ExordiumModBase.instance.config == null) {
            return;
        }
        GuiGraphics guiGraphics = (GuiGraphics) (Object) this;
        int color = ExordiumModBase.instance.config.getTextSelectionColor();
        if (invertHighlightedTextColor) {
            guiGraphics.fill(RenderPipelines.GUI_INVERT, x1, y1, x2, y2, -1);
        }
        guiGraphics.fill(RenderPipelines.GUI_TEXT_HIGHLIGHT, x1, y1, x2, y2, color);
        ci.cancel();
    }

}
