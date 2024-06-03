package com.neep.neepmeat.machine.live_machine.block.entity;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineComponent;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TreeVacuumBlockEntity extends SyncableBlockEntity implements LivingMachineComponent
{
    public static final Identifier CHANNEL_ID = new Identifier("tree_vacuum_anim");

    public float progress;
    public final int maxProgress = 8;

    // Client-only jank
    public int animationTicks;

    public TreeVacuumBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public void setController(BlockPos pos)
    {

    }

    @Override
    public boolean componentRemoved()
    {
        return isRemoved();
    }

    public void syncAnimation()
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(getPos());
        for (var player : PlayerLookup.tracking(this))
        {
            ServerPlayNetworking.send(player, CHANNEL_ID, buf);
        }
    }

    @Override
    public ComponentType<? extends LivingMachineComponent> getComponentType()
    {
        return LivingMachineComponents.TREE_VACUUM;
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, TreeVacuumBlockEntity be)
    {
        be.animationTicks = Math.max(0, be.animationTicks - 1);
    }
}
