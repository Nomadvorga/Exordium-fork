package dev.tr7zw.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ComponentProvider {

    public static final Component EMPTY = literal("");

    public static MutableComponent literal(String string) {
        return Component.literal(string);
    }

    public static MutableComponent translatable(String string) {
        return Component.translatable(string);
    }

    public static MutableComponent translatable(String string, Object... objects) {
        return Component.translatable(string, objects);
    }

    public static MutableComponent empty() {
        return Component.empty();
    }

}
