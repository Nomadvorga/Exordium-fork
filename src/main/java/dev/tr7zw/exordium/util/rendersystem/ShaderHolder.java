package dev.tr7zw.exordium.util.rendersystem;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ShaderHolder implements StateHolder {

    @Getter
    private boolean fetched = false;

    public void fetch() {
        fetched = true;
    }

    public void apply() {
        // Shader selection is handled per render pass in 1.21.11.
    }

}
