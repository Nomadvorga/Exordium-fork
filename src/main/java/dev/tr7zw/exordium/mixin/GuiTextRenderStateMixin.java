package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import dev.tr7zw.exordium.util.TextColorContext;
import net.minecraft.client.gui.render.state.GuiTextRenderState;

@Mixin(GuiTextRenderState.class)
public class GuiTextRenderStateMixin {

    @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private static int exordium$remapTextColor(int color) {
        return TextColorContext.remapRenderableTextColor(color);
    }

}
