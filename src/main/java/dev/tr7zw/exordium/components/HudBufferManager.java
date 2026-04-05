package dev.tr7zw.exordium.components;

import java.util.EnumMap;

import com.mojang.blaze3d.buffers.GpuBufferSlice;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiRenderer;

public class HudBufferManager {

    private final EnumMap<HudCategory, HudBuffer> buffers = new EnumMap<>(HudCategory.class);

    public HudBufferManager() {
        for (HudCategory category : HudCategory.values()) {
            buffers.put(category, new HudBuffer(category));
        }
    }

    public void beginFrame() {
        for (HudBuffer buffer : buffers.values()) {
            buffer.beginFrame();
        }
    }

    public HudBufferSession prepare(HudCategory category, GuiGraphics guiGraphics) {
        return buffers.get(category).prepare(guiGraphics);
    }

    public void drawCached(HudCategory category, GuiGraphics guiGraphics) {
        buffers.get(category).drawCached(guiGraphics);
    }

    public void renderPending(GuiRenderer sourceRenderer, GpuBufferSlice fogBuffer) {
        for (HudBuffer buffer : buffers.values()) {
            buffer.renderPending(sourceRenderer, fogBuffer);
        }
    }

}
