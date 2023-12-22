package com.neep.neepmeat.block.vat;

import com.neep.meatlib.block.BaseBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VatWindowBlock extends BaseBlock implements IVatComponent, BlockEntityProvider
{
    public VatWindowBlock(String registryName, int itemMaxStack, boolean hasLore, Settings settings)
    {
        super(registryName, itemMaxStack, hasLore, settings.nonOpaque().solidBlock(VatWindowBlock::never));
    }

    @Override
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0f;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world.getBlockEntity(pos) instanceof Entity be && !world.isClient())
        {
            be.onParentBreak((ServerWorld) world);
        }
    }

    public static boolean never(BlockState state, BlockView world, BlockPos pos)
    {
        return false;
    }

    public static boolean never(BlockState blockState, BlockView blockView, BlockPos blockPos, EntityType<?> entityType)
    {
        return false;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.VAT_WINDOW.instantiate(pos, state);
    }
}
