package com.neep.neepmeat.machine.small_compressor;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SmallCompressorBlockEntity extends SyncableBlockEntity
{
    public SmallCompressorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        Box box = Box.of(Vec3d.ofCenter(pos), 16, 16, 16);

        List<PlayerEntity> players = world.getPlayers(TargetPredicate.DEFAULT, null, box);


    }
}
