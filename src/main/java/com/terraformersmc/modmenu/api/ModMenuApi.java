package com.terraformersmc.modmenu.api;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public interface ModMenuApi {

    static Screen createModsScreen(Screen parent) {
        return parent;
    }

    static Component createModsButtonText() {
        return Component.literal("Mods");
    }

    default ConfigScreenFactory<?> getModConfigScreenFactory() {
        return null;
    }

    default UpdateChecker getUpdateChecker() {
        return null;
    }

    default Map<String, ConfigScreenFactory<?>> getProvidedConfigScreenFactories() {
        return Collections.emptyMap();
    }

    default Map<String, UpdateChecker> getProvidedUpdateCheckers() {
        return Collections.emptyMap();
    }

    default void attachModpackBadges(Consumer<String> consumer) {
    }

}
