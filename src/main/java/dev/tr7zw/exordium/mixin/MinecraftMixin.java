package dev.tr7zw.exordium.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.pipeline.RenderTarget;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.Minecraft;

/**
 * While rendering the hud/screen, other mods might also use custom render
 * targets. This Mixin ensures that they switch back to the buffer from this mod
 * instead of the vanilla screen.
 * 
 * @author tr7zw
 *
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "getMainRenderTarget", at = @At("HEAD"), cancellable = true)
    public void getMainRenderTarget(CallbackInfoReturnable<RenderTarget> ci) {
        RenderTarget target = ExordiumModBase.instance.getTemporaryScreenOverwrite();
        if (target != null) {
            ExordiumModBase.instance.markTemporaryScreenOverwriteUsed();
            ci.setReturnValue(target);
            ci.cancel();
        }
    }

    @ModifyConstant(method = "runTick", constant = @Constant(intValue = 260))
    private int exordium$raiseVanillaFpsCap(int original) {
        if (ExordiumModBase.instance == null || !ExordiumModBase.instance.isInitialized()
                || ExordiumModBase.instance.config == null) {
            return original;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null && (minecraft.screen != null || minecraft.getOverlay() != null)
                && ExordiumModBase.instance.config.mainMenuFpsLimit >= original) {
            return 361;
        }
        return original;
    }

}
