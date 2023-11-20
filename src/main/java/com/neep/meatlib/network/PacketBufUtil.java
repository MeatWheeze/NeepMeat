package com.neep.meatlib.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class PacketBufUtil
{

    /**
     * Packs a floating-point angle into a {@code byte}.
     *
     * @param angle angle
     * @return packed angle
     */
    public static byte packAngle(float angle)
    {
        return (byte) MathHelper.floor(angle * 256 / 360);
    }

    /**
     * Unpacks a floating-point angle from a {@code byte}.
     *
     * @param angleByte packed angle
     * @return angle
     */
    public static float unpackAngle(byte angleByte)
    {
        return (angleByte * 360) / 256f;
    }

    /**
     * Writes an angle to a {@link PacketByteBuf}.
     *
     * @param buf destination buffer
     * @param angle   angle
     */
    public static void writeAngle(PacketByteBuf buf, float angle)
    {
        buf.writeByte(packAngle(angle));
    }

    /**
     * Reads an angle from a {@link PacketByteBuf}.
     *
     * @param buf source buffer
     * @return angle
     */
    public static float readAngle(PacketByteBuf buf)
    {
        return unpackAngle(buf.readByte());
    }

    /**
     * Writes a {@link Vec3d} to a {@link PacketByteBuf}.
     *
     * @param buf destination buffer
     * @param vec3d   vector
     */
    public static void writeVec3d(PacketByteBuf buf, Vec3d vec3d)
    {
        buf.writeDouble(vec3d.x);
        buf.writeDouble(vec3d.y);
        buf.writeDouble(vec3d.z);
    }

    /**
     * Reads a {@link Vec3d} from a {@link PacketByteBuf}.
     *
     * @param buf source buffer
     * @return vector
     */
    public static Vec3d readVec3d(PacketByteBuf buf)
    {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        return new Vec3d(x, y, z);
    }

    /**
     * Writes a {@link BlockPos} to a {@link PacketByteBuf}.
     *
     * @param buf destination buffer
     * @param pos   pos
     */
    public static void writeBlockPos(PacketByteBuf buf, BlockPos pos)
    {
        buf.writeVarInt(pos.getX());
        buf.writeVarInt(pos.getY());
        buf.writeVarInt(pos.getZ());
    }

    /**
     * Reads a {@link BlockPos} from a {@link PacketByteBuf}.
     *
     * @param buf source buffer
     * @return vector
     */
    public static BlockPos readBlockPos(PacketByteBuf buf)
    {
        return new BlockPos(
            buf.readVarInt(),
            buf.readVarInt(),
            buf.readVarInt());
    }
}
