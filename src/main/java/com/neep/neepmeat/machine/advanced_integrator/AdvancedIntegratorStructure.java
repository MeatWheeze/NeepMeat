package com.neep.neepmeat.machine.advanced_integrator;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.block.entity.AdvancedIntegratorStructureBlockEntity;
import com.neep.neepmeat.transport.api.pipe.DataCable;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class AdvancedIntegratorStructure extends BigBlockStructure<AdvancedIntegratorStructureBlockEntity> implements DataCable
{
    public AdvancedIntegratorStructure(BigBlock parent, Settings settings)
    {
        super(parent, settings);
    }

    @Override
    protected BlockEntityType<AdvancedIntegratorStructureBlockEntity> registerBlockEntity()
    {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "advanced_integrator_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new AdvancedIntegratorStructureBlockEntity(getBlockEntityType(), p, s),
                        this).build());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
    {
        if (!player.getStackInHand(hand).isEmpty())
            return super.onUse(state, world, pos, player, hand, hit);

        if (!world.isClient() && world.getBlockEntity(pos) instanceof AdvancedIntegratorStructureBlockEntity be)
        {
            var parent = be.getParent();
            if (parent != null)
            {
                parent.onUse(player);
            }
        }
        return ActionResult.SUCCESS;
    }
}
