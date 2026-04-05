package dev.tr7zw.exordium.util.rendersystem;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ShaderColorHolder implements StateHolder {

    @Getter
    private boolean fetched = false;

    public void fetch() {
        fetched = true;
    }

    public void apply() {
        // Shader color is part of the new render pipeline state in 1.21.11.
    }

}
