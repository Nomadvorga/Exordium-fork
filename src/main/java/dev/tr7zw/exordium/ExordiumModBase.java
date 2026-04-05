package dev.tr7zw.exordium;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.lwjgl.opengl.GL11;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.opengl.GlStateManager;

import dev.tr7zw.exordium.components.BufferManager;
import dev.tr7zw.exordium.components.HudBufferManager;
import dev.tr7zw.exordium.components.MainBufferInstance;
import dev.tr7zw.exordium.config.ExordiumConfigScreen;
import dev.tr7zw.exordium.util.CustomShaderManager;
import dev.tr7zw.exordium.util.DelayedRenderCallManager;
import dev.tr7zw.exordium.versionless.config.Config;
import dev.tr7zw.exordium.versionless.config.ConfigUpgrader;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;

public abstract class ExordiumModBase {

    public static final Logger LOGGER = LogManager.getLogger("Exordium");
    public static ExordiumModBase instance;
    @Getter
    private static boolean forceBlend;

    public Config config;
    private final File settingsFile = new File("config", "exordium.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private RenderTarget temporaryScreenOverwrite = null;
    private boolean temporaryScreenOverwriteUsed = false;
    @Getter
    private final DelayedRenderCallManager delayedRenderCallManager = new DelayedRenderCallManager();
    @Getter
    private final CustomShaderManager customShaderManager = new CustomShaderManager();
    private HudBufferManager hudBufferManager;
    private final BufferManager bufferManager = new BufferManager();
    private MainBufferInstance mainBuffer;
    @Getter
    private boolean initialized = false;
    private boolean lateInit = true;

    void onInitialize() {
        instance = this;
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                LOGGER.error("Error while loading config! Creating a new one!", ex);
            }
        }
        if (config == null) {
            config = new Config();
            //#if MC >= 12104
            config.hotbarSettings.setForceUpdates(true);
            //#endif
            writeConfig();
        } else {
            if (ConfigUpgrader.upgradeConfig(config)) {
                writeConfig(); // Config got modified
            }
        }
        if (config.globalSettings == null) {
            config.globalSettings = new Config.ComponentSettings(true, 30);
        }
        initModloader();
    }

    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.writeString(settingsFile.toPath(), gson.toJson(config));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public abstract void initModloader();

    public void markInitialized() {
        initialized = true;
    }

    public Screen createConfigScreen(Screen parent) {
        return new ExordiumConfigScreen(parent);
    }

    public static void setForceBlend(boolean forceBlend) {
        ExordiumModBase.forceBlend = forceBlend;
    }

    public static void correctBlendMode() {
        GlStateManager._enableBlend();
        GlStateManager._blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
                GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void beginTemporaryScreenOverwrite(RenderTarget target) {
        temporaryScreenOverwrite = target;
        temporaryScreenOverwriteUsed = false;
    }

    public void markTemporaryScreenOverwriteUsed() {
        if (temporaryScreenOverwrite != null) {
            temporaryScreenOverwriteUsed = true;
        }
    }

    public boolean endTemporaryScreenOverwrite() {
        boolean used = temporaryScreenOverwriteUsed;
        temporaryScreenOverwrite = null;
        temporaryScreenOverwriteUsed = false;
        return used;
    }

    public BufferManager getBufferManager() {
        if (lateInit) {
            bufferManager.initialize();
            lateInit = false;
        }
        return bufferManager;
    }

    public MainBufferInstance getMainBuffer() {
        if (mainBuffer == null) {
            mainBuffer = new MainBufferInstance(() -> config.inventorySettings);
        }
        return mainBuffer;
    }

    public HudBufferManager getHudBufferManager() {
        if (hudBufferManager == null) {
            hudBufferManager = new HudBufferManager();
        }
        return hudBufferManager;
    }

}
