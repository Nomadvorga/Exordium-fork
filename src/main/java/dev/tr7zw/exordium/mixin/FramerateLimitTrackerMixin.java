package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.platform.FramerateLimitTracker;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.Minecraft;

@Mixin(FramerateLimitTracker.class)
public abstract class FramerateLimitTrackerMixin {

    @Inject(method = "getFramerateLimit", at = @At("HEAD"), cancellable = true)
    private void exordium$overrideMainMenuFps(CallbackInfoReturnable<Integer> cir) {
        if (ExordiumModBase.instance == null || !ExordiumModBase.instance.isInitialized()
                || ExordiumModBase.instance.config == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null || (minecraft.screen == null && minecraft.getOverlay() == null)) {
            return;
        }
        cir.setReturnValue(Math.max(60, Math.min(360, ExordiumModBase.instance.config.mainMenuFpsLimit)));
    }

}
