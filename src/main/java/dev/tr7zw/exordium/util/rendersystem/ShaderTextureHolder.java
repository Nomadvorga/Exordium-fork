package dev.tr7zw.exordium.util.rendersystem;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ShaderTextureHolder implements StateHolder {

    @Getter
    private boolean fetched = false;

    public void fetch() {
        fetched = true;
    }

    public void apply() {
        // Textures are bound explicitly on each render pass in 1.21.11.
    }

}
