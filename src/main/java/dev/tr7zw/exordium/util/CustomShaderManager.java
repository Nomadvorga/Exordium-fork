package dev.tr7zw.exordium.util;

import java.util.OptionalInt;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import dev.tr7zw.exordium.render.BufferedComponent;
import net.minecraft.client.Minecraft;

public class CustomShaderManager {

    private static final RenderPipeline HUD_BLIT = createBlitPipeline("pipeline/exordium_hud_blit",
            BlendFunction.TRANSLUCENT);
    private static final RenderPipeline CROSSHAIR_BLIT = createBlitPipeline("pipeline/exordium_crosshair_blit",
            BlendFunction.INVERT);

    private static RenderPipeline createBlitPipeline(String location, BlendFunction blendFunction) {
        return RenderPipeline.builder().withLocation(location).withVertexShader("core/screenquad")
                .withFragmentShader("core/blit_screen").withSampler("InSampler").withBlend(blendFunction)
                .withDepthWrite(false).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withVertexFormat(DefaultVertexFormat.EMPTY, Mode.TRIANGLES).build();
    }

    public void renderComponent(BufferedComponent component) {
        renderTarget(component.getRenderTarget(), component.getCrosshair());
    }

    private void renderTarget(RenderTarget renderTarget, boolean crosshair) {
        if (renderTarget.getColorTextureView() == null) {
            return;
        }
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder()
                .createRenderPass(() -> "Exordium component blit", mainTarget.getColorTextureView(),
                        OptionalInt.empty())) {
            renderPass.setPipeline(crosshair ? CROSSHAIR_BLIT : HUD_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("InSampler", renderTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.draw(0, 3);
        }
    }

}
