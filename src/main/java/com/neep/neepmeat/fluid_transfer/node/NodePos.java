package com.neep.neepmeat.fluid_transfer.node;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class NodePos
{
    public final BlockPos pos;
    public final Direction face;

    public NodePos(BlockPos pos, Direction face)
    {
        this.pos = pos;
        this.face = face;
    }

    public NodePos(FluidNode node)
    {
        this.pos = node.getPos();
        this.face = node.getFace();
    }

    @Override
    public String toString()
    {
        return pos + ", " + face;
    }

    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof NodePos nodePos))
        {
            return false;
        }
        return nodePos.pos.equals(pos) && nodePos.face.equals(face);
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(3, 19)
                .append(pos)
                .append(face)
                .toHashCode();
    }

    public ChunkPos toChunkPos()
    {
        return ChunkSectionPos.from(pos).toChunkPos();
    }

    public BlockPos facingBlock()
    {
        return pos.offset(face);
    }
}
