package dev.tr7zw.exordium.util;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.render.BufferedComponent;

/**
 * Iris causes issues when trying to switch render buffers during world
 * rendering. This class delays the draws to after the world rendering
 * 
 * @author tr7zw
 *
 */
public class DelayedRenderCallManager {
    private final List<BufferedComponent> componentRenderCalls = new ArrayList<>();

    public void addBufferedComponent(BufferedComponent component) {
        this.componentRenderCalls.add(component);
    }

    public void renderComponents() {
        if (componentRenderCalls.isEmpty()) {
            return;
        }

        CustomShaderManager shaderManager = ExordiumModBase.instance.getCustomShaderManager();

        List<BufferedComponent> crosshairComponents = new ArrayList<>();
        List<BufferedComponent> normalComponents = new ArrayList<>();
        for (BufferedComponent component : this.componentRenderCalls) {
            if (((IBufferedComponent) component).getCrosshair()) {
                crosshairComponents.add(component);
            } else {
                normalComponents.add(component);
            }
        }

        if (!normalComponents.isEmpty()) {
            drawBatch(normalComponents, shaderManager);
        }
        if (!crosshairComponents.isEmpty()) {
            drawBatch(crosshairComponents, shaderManager);
        }

        this.componentRenderCalls.clear();
    }

    private void drawBatch(List<BufferedComponent> components, CustomShaderManager shaderManager) {
        for (BufferedComponent component : components) {
            shaderManager.renderComponent(component);
        }
    }
}
