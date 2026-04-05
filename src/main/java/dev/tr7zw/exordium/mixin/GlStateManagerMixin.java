package dev.tr7zw.exordium.mixin;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.opengl.GlStateManager;

import dev.tr7zw.exordium.ExordiumModBase;

/**
 * Restores a sane blend function while Exordium is capturing GUI elements.
 */
@Mixin(value = GlStateManager.class, remap = false)
public class GlStateManagerMixin {

    @Inject(method = "_blendFuncSeparate", at = @At("HEAD"), cancellable = true)
    private static void _blendFuncSeparate(int i, int j, int k, int l, CallbackInfo ci) {
        if (ExordiumModBase.isForceBlend()) {
            GlStateManager.glBlendFuncSeparate(i, j, k, l);
            ci.cancel();
        }
    }

    @Inject(method = "glBlendFuncSeparate", at = @At("HEAD"), cancellable = true)
    private static void glBlendFuncSeparate(int i, int j, int k, int l, CallbackInfo ci) {
        if (ExordiumModBase.isForceBlend()) {
            GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
                    GL11.GL_ONE_MINUS_SRC_ALPHA);
            ci.cancel();
        }
    }

}
