package com.neep.neepmeat.transport.fluid_network.node;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NodePos extends BlockPos
{
    protected final Direction face;

    public NodePos(BlockPos pos, Direction face)
    {
        super(pos);
        this.face = face;
    }

    public NbtCompound toNbt(NbtCompound nbt)
    {
        nbt.putLong("pos", asLong());
        nbt.putInt("face", face.getId());
        return nbt;
    }

    public static NodePos fromNbt(NbtCompound nbt)
    {
        BlockPos pos = BlockPos.fromLong(nbt.getLong("pos"));
        Direction face = Direction.byId(nbt.getInt("face"));
        return new NodePos(pos, face);
    }

    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).add("face", face).toString();
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof NodePos nodePos))
        {
            return false;
        }
        return super.equals(nodePos) && nodePos.face == face;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(3, 19)
                .append(super.hashCode())
                .append(face)
                .toHashCode();
    }

    public ChunkPos toChunkPos()
    {
        return ChunkSectionPos.from(this).toChunkPos();
    }

    public BlockPos facingBlock()
    {
        return offset(face);
    }

    public Direction face()
    {
        return face;
    }
}
