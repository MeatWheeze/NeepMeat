package com.neep.neepmeat.plc.recipe;

import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class EntityMutateRecipe implements ManufactureRecipe<MutateInPlace<Entity>, EntityType<?>>
{
    protected final Identifier id;
    protected final EntityType<?> base;
    protected final List<ManufactureStep<?>> steps;

    protected EntityMutateRecipe(Identifier id, EntityType<?> base, List<ManufactureStep<?>> steps)
    {
        this.id = id;
        this.base = base;
        this.steps = steps;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }
}
