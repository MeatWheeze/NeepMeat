package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.instruction.Argument;
import com.neep.neepmeat.api.plc.instruction.Instruction;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import com.neep.neepmeat.api.plc.program.PlcProgram;
import com.neep.neepmeat.api.plc.recipe.Workpiece;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.api.plc.robot.SingleAction;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.recipe.EntityManufactureRecipe;
import com.neep.neepmeat.plc.recipe.ImplantStep;
import com.neep.neepmeat.plc.recipe.PLCRecipes;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Supplier;

public class ImplantInstruction implements Instruction
{
    private final Supplier<World> world;
    private final LazyBlockApiCache<Storage<ItemVariant>, Direction> fromCache;
    private final LazyBlockApiCache<MutateInPlace<Entity>, Void> toCache;
    private final Argument from;
    private final Argument to;
    private ResourceAmount<ItemVariant> stored;

    public ImplantInstruction(Supplier<ServerWorld> world, List<Argument> arguments)
    {
        this.world = world::get;
        this.from = arguments.get(0);
        this.to = arguments.get(1);
        this.fromCache = LazyBlockApiCache.itemSided(from, world);
        this.toCache = LazyBlockApiCache.of(MutateInPlace.ENTITY, to.pos(), world, () -> null);
    }

    public ImplantInstruction(Supplier<World> world, NbtCompound compound)
    {
        this.world = world;
        this.from = Argument.fromNbt(compound.getCompound("from"));
        this.to = Argument.fromNbt(compound.getCompound("to"));
        this.fromCache = LazyBlockApiCache.itemSided(from, () -> (ServerWorld) world.get());
        this.toCache = LazyBlockApiCache.of(MutateInPlace.ENTITY, to.pos(), () -> (ServerWorld) world.get(), () -> null);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("from", from.toNbt());
        nbt.put("to", to.toNbt());
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {

    }

    @Override
    public boolean canStart(PLC plc)
    {
        return true;
    }

    @Override
    public void start(PlcProgram program, PLC plc)
    {
        plc.addRobotAction(GroupedRobotAction.of(
                new RobotMoveToAction(plc.getRobot(), from.pos()),
                SingleAction.of(() -> this.takeFrom(plc)),
                new RobotMoveToAction(plc.getRobot(), getEntityPos(plc)),
                SingleAction.of(() -> this.install(plc))
        ), this::finish);
    }

    private void takeFrom(PLC plc)
    {
        var takenAmount = Instructions.takeItem(from, world, 1);
        if (takenAmount != null)
        {
            this.stored = takenAmount;
        }
    }

    private void install(PLC plc)
    {
        var mip = toCache.find();
        if (mip != null)
        {
            Entity entity = mip.get();
            ImplantStep step = new ImplantStep(stored.resource().getItem());
            Workpiece workpiece = NMComponents.WORKPIECE.getNullable(entity);
//            if (workpiece != null && PLCRecipes.isValidStep(PLCRecipes.ENTITY_MANUFACTURE, workpiece, step, entity.getType()))
            if (workpiece != null)
            {
                step.mutate(entity);

                EntityManufactureRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(PLCRecipes.ENTITY_MANUFACTURE, mip).orElse(null);
                if (recipe != null)
                {
                    recipe.ejectOutputs(mip, null);
                    workpiece.clearSteps();
                }

                return;
            }
        }

        if (stored != null)
            plc.getRobot().spawnItem(stored);
    }

    private void finish(PLC plc)
    {

    }

    private BlockPos getEntityPos(PLC plc)
    {
        var mip = toCache.find();
        if (mip != null)
        {
            Entity entity = mip.get();
            return new BlockPos(entity.getBlockX(),
                    Math.ceil(entity.getY() + entity.getHeight()),
                    entity.getBlockZ());
        }
        else
        {
            plc.raiseError(new PLC.Error("No entity station found"));
        }
        return toCache.pos();
    }

    @Override
    public InstructionProvider getProvider()
    {
        return Instructions.IMPLANT;
    }
}
