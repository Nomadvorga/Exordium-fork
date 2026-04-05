package dev.tr7zw.util;

import java.util.Map.Entry;
import java.util.Set;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

public class NMSHelper {

    private static final Minecraft MC = Minecraft.getInstance();
    public static final float PI = (float) Math.PI;
    public static final float HALF_PI = (float) (Math.PI / 2);
    public static final float TWO_PI = (float) (Math.PI * 2);
    public static final float DEG_TO_RAD = (float) (Math.PI / 180.0);

    public static Axis XN = f -> new Quaternionf().rotationX(-f);
    public static Axis XP = f -> new Quaternionf().rotationX(f);
    public static Axis YN = f -> new Quaternionf().rotationY(-f);
    public static Axis YP = f -> new Quaternionf().rotationY(f);
    public static Axis ZN = f -> new Quaternionf().rotationZ(-f);
    public static Axis ZP = f -> new Quaternionf().rotationZ(f);

    public static Identifier getResourceLocation(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Identifier getResourceLocation(String key) {
        return Identifier.parse(key);
    }

    public static Item getItem(Identifier key) {
        return BuiltInRegistries.ITEM.get(key).map(net.minecraft.core.Holder.Reference::value).orElse(Items.AIR);
    }

    public static Set<Entry<ResourceKey<Item>, Item>> getItems() {
        return BuiltInRegistries.ITEM.entrySet();
    }

    public static float getXRot(Entity ent) {
        return ent.getXRot();
    }

    public static float getYRot(Entity ent) {
        return ent.getYRot();
    }

    public static void setXRot(Entity ent, float xRot) {
        ent.setXRot(xRot);
    }

    public static void setYRot(Entity ent, float yRot) {
        ent.setYRot(yRot);
    }

    public static Identifier getPlayerSkin(AbstractClientPlayer player) {
        return player.getSkin().body().texturePath();
    }

    public static Identifier getPlayerSkin(GameProfile gameprofile) {
        PlayerSkin playerSkin = Minecraft.getInstance().getSkinManager().createLookup(gameprofile, false).get();
        if (playerSkin == null || playerSkin.body() == null) {
            return null;
        }
        return playerSkin.body().texturePath();
    }

    public static Identifier getPlayerCape(AbstractClientPlayer player) {
        try {
            return player.getSkin().cape() == null ? null : player.getSkin().cape().texturePath();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public static GameProfile getGameProfile(ItemStack itemStack) {
        if (itemStack.getComponents().has(DataComponents.CUSTOM_MODEL_DATA)) {
            return null;
        }
        if (itemStack.getComponents().has(DataComponents.PROFILE)) {
            ResolvableProfile resolvableProfile = (ResolvableProfile) itemStack.get(DataComponents.PROFILE);
            if (resolvableProfile != null) {
                return resolvableProfile.partialProfile();
            }
        }
        return null;
    }

    public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v,
            int lightmapUV) {
        addVertex(cons, matrix4f, x, y, z, u, v, lightmapUV & 65535, lightmapUV >> 16 & 65535);
    }

    public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v,
            int u2, int v2) {
        cons.addVertex(matrix4f, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setUv2(u2, v2);
    }

    public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v) {
        cons.addVertex(matrix4f, x, y, z).setColor(255, 255, 255, 255).setUv(u, v);
    }

    public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v,
            int overlay, int lightmapUV, float nx, float ny, float nz) {
        addVertex(cons, matrix4f, x, y, z, u, v, overlay, lightmapUV & 65535, lightmapUV >> 16 & 65535, nx, ny, nz);
    }

    public static void addVertex(VertexConsumer cons, Matrix4f matrix4f, float x, float y, float z, float u, float v,
            int overlay, int u2, int v2, float nx, float ny, float nz) {
        cons.addVertex(matrix4f, x, y, z).setColor(255, 255, 255, 255).setUv(u, v).setUv2(u2, v2)
                .setOverlay(overlay).setNormal(nx, ny, nz);
    }

    public static void sendChatMessage(Component message) {
        MC.getChatListener().handleSystemMessage(message, false);
    }

    public static boolean isSame(ItemStack a, ItemStack b) {
        return ItemStack.isSameItemSameComponents(a, b);
    }

    public static boolean hasCustomName(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_NAME);
    }

}
