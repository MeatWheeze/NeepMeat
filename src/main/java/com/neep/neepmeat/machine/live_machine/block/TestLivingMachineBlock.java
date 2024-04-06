package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineBlock;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
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
        if (world.getBlockEntity(pos) instanceof LivingMachineBlockEntity be)
        {
            if (!world.isClient())
            {
                player.sendMessage(Text.of("Rated power: ").copy().append(PowerUtils.perUnitToText(be.getRatedPower())));
                player.sendMessage((PowerUtils.perUnitToLabelText(be.getPower())));
                player.sendMessage(Text.of("Efficiency: " + Math.round(be.getEfficiency() * 100) + "%"));
            }
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
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
