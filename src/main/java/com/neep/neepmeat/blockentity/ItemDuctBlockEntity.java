package com.neep.neepmeat.blockentity;

import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDuctBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    public ItemDuctBlockEntity(BlockPos pos, BlockState state)
    {
        super(BlockEntityInitialiser.ITEM_DUCT_BLOCK_ENTITY, pos, state);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag)
    {
        super.writeNbt(tag);
        return tag;
    }

    @Override
    public void readNbt(NbtCompound tag)
    {
        super.readNbt(tag);
    }


    @Override
    public void fromClientTag(NbtCompound tag)
    {
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag)
    {
        return tag;
    }

    @Override
    public void sync()
    {
        World world = this.getWorld();
        if (world != null && !world.isClient)
        {
            BlockEntityClientSerializable.super.sync();
        }
    }
}
