package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tr7zw.exordium.util.TextColorContext;
import net.minecraft.network.chat.TextColor;

@Mixin(TextColor.class)
public class TextColorMixin {

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    private void exordium$remapFormattedTextColor(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(TextColorContext.remapTextRgb(cir.getReturnValue()));
    }

}
