package com.neep.neepmeat.block.machine;

import com.neep.neepmeat.block.base.BaseBlock;
import com.neep.neepmeat.block.base.BaseFacingBlock;
import com.neep.neepmeat.block.base.NMBlock;
import com.neep.neepmeat.blockentity.ItemDuctBlockEntity;
import com.neep.neepmeat.blockentity.fluid.TankBlockEntity;
import com.neep.neepmeat.blockentity.machine.HeaterBlockEntity;
import com.neep.neepmeat.init.BlockEntityInitialiser;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HeaterBlock extends BaseFacingBlock implements BlockEntityProvider
{
    public HeaterBlock(String registryName, int itemMaxStack, boolean hasLore, FabricBlockSettings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque());
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return BlockEntityInitialiser.HEATER.instantiate(pos, state);
    }

    @Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A>
    checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<E> ticker, World world)
    {
        return expectedType == givenType && !world.isClient ? (BlockEntityTicker<A>) ticker : null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        System.out.println(type.supports(state));
        return checkType(type, BlockEntityInitialiser.HEATER, HeaterBlockEntity::serverTick, world);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!world.isClient)
        {
            System.out.println(world.getBlockEntity(pos));
            if (world.getBlockEntity(pos) instanceof TankBlockEntity be)
            {
                player.sendMessage(Text.of(Float.toString(be.getBuffer(null).getAmount() / (float) FluidConstants.BUCKET)), true);
            }
        }
        return ActionResult.SUCCESS;
    }
}
