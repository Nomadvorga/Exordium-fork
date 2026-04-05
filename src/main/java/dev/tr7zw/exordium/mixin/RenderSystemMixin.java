package dev.tr7zw.exordium.mixin;

import java.util.concurrent.locks.LockSupport;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.tr7zw.exordium.ExordiumModBase;
import net.minecraft.client.Minecraft;

@Mixin(value = RenderSystem.class, remap = false)
public abstract class RenderSystemMixin {

    private static final long MENU_LIMIT_SPIN_THRESHOLD_NS = 1_000_000L;
    private static long exordium$lastMenuFrameTimeNs = -1L;

    @Inject(method = "limitDisplayFPS", at = @At("HEAD"), cancellable = true, remap = false)
    private static void exordium$limitMainMenuFps(int fpsLimit, CallbackInfo ci) {
        if (ExordiumModBase.instance == null || !ExordiumModBase.instance.isInitialized()
                || ExordiumModBase.instance.config == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null || (minecraft.screen == null && minecraft.getOverlay() == null)) {
            return;
        }
        int targetFps = Math.max(60, Math.min(360, ExordiumModBase.instance.config.mainMenuFpsLimit));
        long frameTimeNs = 1_000_000_000L / targetFps;
        long currentTime = System.nanoTime();
        if (exordium$lastMenuFrameTimeNs < 0L || currentTime - exordium$lastMenuFrameTimeNs > frameTimeNs * 4L) {
            exordium$lastMenuFrameTimeNs = currentTime;
            ci.cancel();
            return;
        }
        long nextFrameTime = exordium$lastMenuFrameTimeNs + frameTimeNs;
        long spinThresholdNs = targetFps >= 240 ? frameTimeNs : MENU_LIMIT_SPIN_THRESHOLD_NS;
        while (currentTime < nextFrameTime) {
            long remainingNs = nextFrameTime - currentTime;
            if (remainingNs > spinThresholdNs) {
                LockSupport.parkNanos(remainingNs - spinThresholdNs);
            } else {
                Thread.onSpinWait();
            }
            currentTime = System.nanoTime();
        }
        exordium$lastMenuFrameTimeNs = nextFrameTime;
        ci.cancel();
    }

}
