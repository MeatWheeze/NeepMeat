package com.neep.neepmeat.machine.live_machine.block;

import com.neep.meatlib.block.MeatlibBlock;
import com.neep.meatlib.block.MeatlibBlockSettings;
import com.neep.meatlib.item.ItemSettings;
import com.neep.meatlib.registry.RegistrationContext;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockPattern;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import com.neep.neepmeat.api.big_block.BigBlockStructureEntity;
import com.neep.neepmeat.api.live_machine.LivingMachineStructure;
import com.neep.neepmeat.api.live_machine.StructureProperty;
import com.neep.neepmeat.machine.live_machine.LivingMachines;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class StomachBlock extends BigBlock<BigBlockStructure<BigBlockStructureEntity>> implements MeatlibBlock, BlockEntityProvider
{
    private final BigBlockPattern pattern = new BigBlockPattern().oddCylinder(1, 0, 1, () -> this.getStructure().getDefaultState());

    public StomachBlock(RegistrationContext ctx, ItemSettings itemSettings, Settings settings)
    {
        super(ctx, settings);
        ctx.append(this, itemSettings.create(this, ctx, itemSettings));
    }

    @Override
    protected BigBlockStructure<BigBlockStructureEntity> registerStructureBlock(RegistrationContext ctx)
    {
        BigBlockStructure.BlockEntityRegisterererer<BigBlockStructureEntity> registerererer = b -> Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "stomach_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new BigBlockStructureEntity(b.getBlockEntityType(), p, s), b).build());

        return ctx.append(this, new StomachStructureBlock(this, MeatlibBlockSettings.copyOf(settings), registerererer), MeatlibBlock::structure);
    }

    @Override
    public BigBlockPattern getVolume(BlockState blockState)
    {
        return pattern;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return LivingMachines.STOMACH_BE.instantiate(pos, state);
    }

    private static class StomachStructureBlock extends BigBlockStructure<BigBlockStructureEntity> implements LivingMachineStructure
    {
        public StomachStructureBlock(BigBlock<?> parent, Settings settings, BlockEntityRegisterererer<BigBlockStructureEntity> registerBlockEntity)
        {
            super(parent, settings, registerBlockEntity);
        }

        @Override
        public EnumMap<StructureProperty, StructureProperty.Entry> getProperties()
        {
            return StructureProperty.EMPTY;
        }
    }
}
