package com.neep.neepmeat.api.plc.instruction;

import com.neep.meatlib.network.PacketBufUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
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

    public NbtCompound toNbt()
    {
        NbtCompound nbt = new NbtCompound();

        nbt.put("pos", NbtHelper.fromBlockPos(pos));
        nbt.putInt("face", face.ordinal());
        return nbt;
    }

    public static Argument fromNbt(NbtCompound nbt)
    {
        return new Argument(NbtHelper.toBlockPos(nbt.getCompound("pos")),
                Direction.values()[nbt.getInt("face")]);
    }
}
