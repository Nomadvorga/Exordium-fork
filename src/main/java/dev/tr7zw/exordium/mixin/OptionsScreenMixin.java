package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {

    @Unique
    private Button exordium$configButton;

    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void exordium$addConfigButton(CallbackInfo ci) {
        if (exordium$configButton != null) {
            exordium$positionConfigButton();
            return;
        }
        exordium$configButton = this.addRenderableWidget(Button.builder(Component.literal("Exordium"),
                button -> this.minecraft.setScreen(ExordiumModBase.instance.createConfigScreen((Screen) (Object) this)))
                .size(200, 20).build());
        exordium$positionConfigButton();
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    private void exordium$repositionConfigButton(CallbackInfo ci) {
        exordium$positionConfigButton();
    }

    @Unique
    private void exordium$positionConfigButton() {
        if (exordium$configButton == null) {
            return;
        }
        exordium$configButton.setPosition(this.width / 2 - 100, this.height - 52);
    }

}
