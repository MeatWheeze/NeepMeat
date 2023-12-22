package com.neep.neepmeat.plc;

import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.api.plc.instruction.ArgumentPredicate;
import com.neep.neepmeat.api.plc.instruction.InstructionException;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;

public class ArgumentPredicates
{
    public static final ArgumentPredicate ANY = ((world, argument) -> true);

    public static final ArgumentPredicate IS_ITEM_STORAGE = (world, argument) ->
    {
        if (ItemStorage.SIDED.find(world, argument.pos(), argument.face()) != null)
            return true;

        throw new InstructionException("No item storage at " + argument.pos().getX() + ", " + argument.pos().getY() + ", " + argument.pos().getZ() + ", " + argument.face().name());
    };

    public static final ArgumentPredicate IS_FLUID_STORAGE = (world, argument) ->
    {
        if (FluidStorage.SIDED.find(world, argument.pos(), argument.face()) != null)
            return true;

        throw new InstructionException("No fluid storage at " + argument.pos().getX() + ", " + argument.pos().getY() + ", " + argument.pos().getZ() + ", " + argument.face().name());
    };

    public static final ArgumentPredicate IS_ITEM_MIP = (world, argument) ->
    {
        if (MutateInPlace.ITEM.find(world, argument.pos(), null) != null)
            return true;

        throw new InstructionException("No item workbench at " + argument.pos().getX() + ", " + argument.pos().getY() + ", " + argument.pos().getZ());
    };

    public static final ArgumentPredicate IS_ENTITY_MIP = (world, argument) ->
    {
        if (MutateInPlace.ENTITY.find(world, argument.pos(), null) != null)
            return true;

        throw new InstructionException("No entity workbench at " + argument.pos().getX() + ", " + argument.pos().getY() + ", " + argument.pos().getZ());
    };
}
