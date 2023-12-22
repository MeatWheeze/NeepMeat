package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.network.PacketBufUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record Argument(BlockPos pos, Direction face)
{
    public void writeBuf(PacketByteBuf buf)
    {
        PacketBufUtil.writeBlockPos(buf, pos);
        buf.writeInt(face.ordinal());
    }

    public static Argument fromBuf(PacketByteBuf buf)
    {
        BlockPos pos = PacketBufUtil.readBlockPos(buf);
        Direction face = Direction.values()[buf.readInt()];
        return new Argument(pos, face);
    }
}
