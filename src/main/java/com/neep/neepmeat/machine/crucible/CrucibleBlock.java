package com.neep.neepmeat.machine.crucible;

import com.neep.meatlib.block.BaseBlock;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.api.storage.WritableSingleFluidStorage;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrucibleBlock extends BaseBlock implements BlockEntityProvider
{

    protected static final VoxelShape OUTLINE_SHAPE;
    private static final VoxelShape RAYCAST_SHAPE = createCuboidShape(2.0, 4.0, 2.0, 14.0, 16.0, 14.0);

    public CrucibleBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, itemSettings, settings.nonOpaque());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof CrucibleBlockEntity be && !world.isClient)
        {
            WritableSingleFluidStorage.handleInteract(be.getStorage().fluidStorage, world, player, hand);
        }
        return FluidStorage.ITEM.find(player.getStackInHand(hand), ContainerItemContext.ofPlayerHand(player, hand)) != null ? ActionResult.SUCCESS : ActionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.CRUCIBLE.instantiate(pos, state);
    }


    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity)
    {

        super.onEntityCollision(state, world, pos, entity);
        if (!world.isClient() && entity instanceof ItemEntity item &&  entity.isOnGround() && world.getBlockEntity(pos) instanceof CrucibleBlockEntity be)
        {
            be.receiveItemEntity(item);
        }
    }

    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    static {
        OUTLINE_SHAPE = VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), VoxelShapes.union(createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0), new VoxelShape[]{createCuboidShape(4.0, 0.0, 0.0, 12.0, 3.0, 16.0), createCuboidShape(2.0, 0.0, 2.0, 14.0, 3.0, 14.0), RAYCAST_SHAPE}), BooleanBiFunction.ONLY_FIRST);
    }
}
