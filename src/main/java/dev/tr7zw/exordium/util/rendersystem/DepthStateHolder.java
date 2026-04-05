package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.opengl.GlStateManager;
import lombok.Getter;
import lombok.ToString;

@ToString
public class DepthStateHolder implements StateHolder {

    @Getter
    private boolean fetched = false;

    public void fetch() {
        fetched = true;
    }

    public void apply() {
        if (fetched) {
            GlStateManager._enableDepthTest();
            GlStateManager._depthMask(true);
        }
    }

}
