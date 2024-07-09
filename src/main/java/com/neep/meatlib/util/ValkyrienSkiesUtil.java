package com.neep.meatlib.util;

import kotlin.jvm.functions.Function0;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4dc;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.valkyrienskies.mod.common.PlayerUtil;
import org.valkyrienskies.mod.common.VSClientGameUtils;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

/**
 * This class should never be initialized if VS2 is not present. It exists to serve as a buffer such that things don't explode if VS2 isn't present, that
 * way it is not a hard dependency.
 */
public class ValkyrienSkiesUtil {

    @Environment(EnvType.CLIENT)
    public Client CLIENT;

    public ValkyrienSkiesUtil() {
        if (!FabricLoader.getInstance().isModLoaded("valkyrienskies")) {
            throw new IllegalStateException("This class should never be initialized if VS2 is not present.");
        }
    }

    /**
     * Gets the transformation matrix from Shipspace to Worldspace. Returns null if there is no ship at that position.
     * @param pos Position
     * @param level Clientlevel or Serverlevel
     * @return The Matrix, or Null
     * @see ValkyrienSkiesUtil#getWorldToShipMatrix(Vector3d, World)
     */
    public Matrix4dc getShipToWorldMatrix(Vector3d pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos).getShipToWorld();
    }

    public Matrix4dc getShipToWorldMatrix(BlockPos pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos).getShipToWorld();
    }

    /**
     * Inverse of [getShipToWorldMatrix]
     * @param pos Position
     * @param level Clientlevel or Serverlevel
     * @return The Matrix, or Null
     * @see ValkyrienSkiesUtil#getShipToWorldMatrix(Vector3d, World)
     */
    public Matrix4dc getWorldToShipMatrix(Vector3d pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos).getWorldToShip();
    }

    public Matrix4dc getWorldToShipMatrix(BlockPos pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos).getWorldToShip();
    }

    /**
     * Checks if a ship exists at a given position.
     * @param pos Position
     * @param level Clientlevel or Serverlevel
     * @return True if a ship exists at the position.
     */
    public boolean hasShipAtPosition(Vector3d pos, World level) {
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos) != null;
    }

    public boolean hasShipAtPosition(Vec3d pos, World level) {
        Vector3d realPos = new Vector3d(pos.x, pos.y, pos.z);
        return VSGameUtilsKt.getShipObjectManagingPos(level, realPos) != null;
    }

    public boolean hasShipAtPosition(BlockPos pos, World level) {
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos) != null;
    }

    public Quaterniondc getShipToWorldRotation(Vector3d pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos).getTransform().getShipToWorldRotation();
    }

    public Quaterniondc getShipToWorldRotation(Vec3d pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        Vector3d realPos = new Vector3d(pos.x, pos.y, pos.z);
        return VSGameUtilsKt.getShipObjectManagingPos(level, realPos).getTransform().getShipToWorldRotation();
    }

    public Quaterniondc getShipToWorldRotation(BlockPos pos, World level) {
        if (!hasShipAtPosition(pos, level)) {
            return null;
        }
        return VSGameUtilsKt.getShipObjectManagingPos(level, pos).getTransform().getShipToWorldRotation();
    }

    public <T> T transformPlayerTemporarily(PlayerEntity player, World world, BlockPos blockInShip, Function0<T> inside)
    {
        return PlayerUtil.transformPlayerTemporarily(player, world, blockInShip, inside);
    }

    @Environment(net.fabricmc.api.EnvType.CLIENT)
    public static class Client {
        public void transformRenderIfOnShip(MatrixStack matrixStack, Vector3d renderOffset) {
            VSClientGameUtils.transformRenderIfInShipyard(matrixStack, renderOffset.x, renderOffset.y, renderOffset.z);
        }
    }
}
