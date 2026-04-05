package dev.tr7zw.exordium.util.rendersystem;

import com.mojang.blaze3d.opengl.GlStateManager;

import dev.tr7zw.exordium.ExordiumModBase;

import lombok.Getter;
import lombok.ToString;

@ToString
public class BlendStateHolder implements StateHolder {

    @Getter
    private boolean fetched = false;

    public void fetch() {
        fetched = true;
    }

    public void apply() {
        if (fetched) {
            ExordiumModBase.correctBlendMode();
        }
    }

}
