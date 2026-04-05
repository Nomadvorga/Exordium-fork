package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.gui.components.StringWidget;

@Mixin(targets = "net.minecraft.client.gui.components.OptionsList$HeaderEntry")
public class OptionsListHeaderEntryMixin {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/StringWidget;setPosition(II)V"))
    private void exordium$centerHeaderTitle(StringWidget widget, int x, int y) {
        int centeredX = x + 155 - widget.getWidth() / 2;
        widget.setPosition(centeredX, y);
    }

}
