package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import com.neep.neepmeat.machine.live_machine.block.entity.TestLivingMachineBE;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;

public class TestLivingMachineBlock extends LivingMachineBlock implements MeatlibBlock
{
    private final String name;

    public TestLivingMachineBlock(String name, Settings settings)
    {
        super(settings);
        this.name = name;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (world.getBlockEntity(pos) instanceof TestLivingMachineBE be)
        {
            if (!world.isClient())
            {
                if (player.isSneaking())
                {
                    player.sendMessage(Text.of("Rated power: ").copy().append(PowerUtils.perUnitToText(be.getRatedPower())));
                    player.sendMessage((PowerUtils.perUnitToLabelText(be.getPower())));
                    player.sendMessage(Text.of("Efficiency: " + Math.round(be.getEfficiency() * 100) + "%"));
                    player.sendMessage(Text.of("RUL: " + new DecimalFormat("###.##").format(be.getRulHours()) + "hr"));
                }
                else
                {
                    player.openHandledScreen(be);
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
    {
        if (world.getBlockEntity(pos) instanceof LivingMachineBlockEntity be)
        {
            be.onBlockRemoved();
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public String getRegistryName()
    {
        return name;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.TEST_LIVING_MACHINE_BE.instantiate(pos, state);
    }
}
