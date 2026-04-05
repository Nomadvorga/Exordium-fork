package com.terraformersmc.modmenu.api;

import net.minecraft.network.chat.Component;

public interface UpdateInfo {

    boolean isUpdateAvailable();

    default Component getUpdateMessage() {
        return Component.empty();
    }

    String getDownloadLink();

    UpdateChannel getUpdateChannel();

}
