package com.neep.neepmeat.machine.large_motor;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.big_block.BigBlock;
import com.neep.neepmeat.api.big_block.BigBlockStructure;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LargeMotorStructureBlock extends BigBlockStructure<LargeMotorStructureEntity>
{
    public LargeMotorStructureBlock(BigBlock<?> parent, Settings settings)
    {
        super(parent, settings);
    }

    @Override
    protected BlockEntityType<LargeMotorStructureEntity> registerBlockEntity()
    {
        return Registry.register(
                Registries.BLOCK_ENTITY_TYPE, new Identifier(NeepMeat.NAMESPACE, "large_motor_structure"),
                FabricBlockEntityTypeBuilder.create(
                        (p, s) -> new LargeMotorStructureEntity(getBlockEntityType(), p, s),
                        this).build());
    }
}
