package com.neep.neepmeat.machine.surgery_platform;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.transfer.EntityVariant;
import com.neep.neepmeat.recipe.surgery.TableComponent;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SurgeryPlatformBlockEntity extends SyncableBlockEntity
{
    protected final Component tableComponent = new Component();

    public SurgeryPlatformBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public TableComponent<?> getTableComponent(Void ctx)
    {
        return tableComponent;
    }

    protected class Component implements TableComponent<EntityVariant<?>>
    {
        @Override
        public Storage<EntityVariant<?>> getStorage()
        {
            return null;
        }

        @Override
        public Identifier getType()
        {
            return RecipeInputs.ENTITY_MUTATE_ID;
        }
    };
}
