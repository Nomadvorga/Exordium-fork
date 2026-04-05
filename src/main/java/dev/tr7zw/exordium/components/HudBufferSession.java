package dev.tr7zw.exordium.components;

import net.minecraft.client.gui.GuiGraphics;

public record HudBufferSession(Mode mode, GuiGraphics captureGraphics) {

    public static final HudBufferSession PASS_THROUGH = new HudBufferSession(Mode.PASS_THROUGH, null);

    public boolean shouldRenderLive() {
        return mode == Mode.PASS_THROUGH || mode == Mode.LIVE_AND_CAPTURE;
    }

    public boolean shouldCapture() {
        return mode == Mode.LIVE_AND_CAPTURE || mode == Mode.CACHE_AND_CAPTURE;
    }

    public boolean shouldDrawCache() {
        return mode == Mode.CACHE_ONLY || mode == Mode.CACHE_AND_CAPTURE;
    }

    public enum Mode {
        PASS_THROUGH,
        LIVE_AND_CAPTURE,
        CACHE_ONLY,
        CACHE_AND_CAPTURE
    }

}
