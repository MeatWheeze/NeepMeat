package com.neep.neepmeat.machine.synthesiser;

import com.neep.meatlib.block.multi.TallBlock;
import com.neep.meatlib.registry.BlockRegistry;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.util.ItemUtils;
import com.neep.neepmeat.util.MiscUtils;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SynthesiserBlock extends TallBlock implements BlockEntityProvider
{
    public static final BooleanProperty FULL = BooleanProperty.of("full");

    public SynthesiserBlock(String registryName, Settings settings)
    {
        super(registryName, settings.nonOpaque());
        this.setDefaultState(getStateManager().getDefaultState().with(FULL, false));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (ItemUtils.playerHoldingPipe(player, hand)) return ActionResult.PASS;

        if (!world.isClient() && world.getBlockEntity(pos) instanceof SynthesiserBlockEntity be)
        {
            if (player.isSneaking())
            {
                Text name = be.getEntityType() != null ? be.getEntityType().getName() : Text.of("Empty");
                player.sendMessage(Text.translatable("message.neepmeat.synthesiser.template", name), true);
                return ActionResult.SUCCESS;
            }

            ItemStack stack = player.getStackInHand(hand);
            if (be.changeEntityType(player, stack))
            {
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 1, 1);
                world.setBlockState(pos, state.with(SynthesiserBlock.FULL, true));
            }
            else
            {
                world.setBlockState(pos, state.with(SynthesiserBlock.FULL, false));
            }

        }

        return ActionResult.SUCCESS;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        builder.add(FULL);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof SynthesiserBlockEntity be && be.getEntityType() != null)
        {
            ItemScatterer.spawn(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, be.createItem());
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected Structure createStructure()
    {
        return BlockRegistry.queue(new Structure(getRegistryName() + "_structure", FabricBlockSettings.copyOf(this.settings)));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return NMBlockEntities.SYNTHESISER.instantiate(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
    {
        return MiscUtils.checkType(type, NMBlockEntities.SYNTHESISER, (world1, pos, state1, blockEntity) -> blockEntity.tick(), null, world);
    }
}
