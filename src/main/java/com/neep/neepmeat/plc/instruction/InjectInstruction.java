package com.neep.neepmeat.plc.instruction;

import com.neep.meatlib.recipe.MeatRecipeManager;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.AtomicAction;
import com.neep.neepmeat.api.plc.robot.DelayAction;
import com.neep.neepmeat.api.plc.robot.GroupedRobotAction;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.component.MutateInPlace;
import com.neep.neepmeat.plc.recipe.InjectStep;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import com.neep.neepmeat.plc.recipe.PLCRecipes;
import com.neep.neepmeat.plc.recipe.TransformingToolRecipe;
import com.neep.neepmeat.plc.robot.RobotMoveToAction;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.ResourceAmount;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class InjectInstruction implements Instruction
{
    private final Supplier<World> world;
    private final Argument from;
    private final Argument to;
    private ResourceAmount<FluidVariant> stored;

    private final GroupedRobotAction group;

    public InjectInstruction(Supplier<World> world, List<Argument> arguments)
    {
        this.world = world;
        this.from = arguments.get(0);
        this.to = arguments.get(1);

        group = GroupedRobotAction.of(
                new RobotMoveToAction(from.pos()),
                AtomicAction.of(this::takeFrom),
                new RobotMoveToAction(to.pos()),
                AtomicAction.of(this::playSound),
                new DelayAction(40),
                AtomicAction.of(this::insert)
        );
    }

    private void playSound(PLC plc)
    {
        var robot = plc.getActuator();
        world.get().playSound(null, robot.getX(), robot.getY(), robot.getZ(), NMSounds.INJECT_INSTRUCTION_APPLY, SoundCategory.NEUTRAL, 1, 1, 1);
    }

    public InjectInstruction(Supplier<World> world, NbtCompound compound)
    {
        this(() -> (ServerWorld) world.get(), List.of(
                Argument.fromNbt(compound.getCompound("from")),
                Argument.fromNbt(compound.getCompound("to"))
        ));
        group.readNbt(compound.getCompound("action"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.put("from", from.toNbt());
        nbt.put("to", to.toNbt());
        nbt.put("action", group.writeNbt(new NbtCompound()));
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
    public void start(PLC plc)
    {
        plc.addRobotAction(group, this::finish);
    }

    @Override
    public void cancel(PLC plc)
    {
        group.end(plc);
        // Can't really drop a fluid.
    }

    private void takeFrom(PLC plc)
    {
        var takenAmount = Instructions.takeFluid(from, world, FluidConstants.BUCKET);
        if (takenAmount != null)
        {
            this.stored = takenAmount;
        }
        else
            plc.raiseError(new PLC.Error("No extractable resource at " + from.pos()));
    }

    private void insert(PLC plc)
    {
        var mip = MutateInPlace.ITEM.find(world.get(), to.pos(), null);
        if (mip != null && stored != null)
        {
            ItemStack stack = mip.get();

            var step = new InjectStep(stored.resource());

            var workpiece = NMComponents.WORKPIECE.maybeGet(stack).orElse(null);
            if (workpiece != null && PLCRecipes.isValidStep(PLCRecipes.MANUFACTURE, workpiece, step, stack.getItem()))
            {
                workpiece.addStep(step);

                mip.set(stack);

                ItemManufactureRecipe recipe = MeatRecipeManager.getInstance().getFirstMatch(PLCRecipes.MANUFACTURE, mip).orElse(null);
                if (recipe != null)
                {
                    recipe.ejectOutputs(mip, null);
                    workpiece.clearSteps();
                }
                else
                {
//                     Special handling for transforming tools.
                    TransformingToolRecipe transformingToolRecipe = TransformingToolRecipe.getInstance();
                    if (transformingToolRecipe.matches(mip))
                    {
                        transformingToolRecipe.ejectOutputs(mip, null);
                        workpiece.clearSteps();
                    }
                }

                if (world.get() instanceof ServerWorld serverWorld)
                {
//                    ParticleSpawnS2C.sendNearby(serverWorld, plc.getRobot().getBlockPos(), new BlockStateParticleEffect(ParticleTypes.BLOCK, stored.resource().getFluid().getDefaultState().getBlockState()),
//                            plc.getRobot().getPos(), new Vec3d(0, -0.4, 0), new Vec3d(0.1, 0.1, 0.1), 6);
                }

                stored = null;
            }
        }

        // TODO Dump fluid?
    }

    private void finish(PLC plc)
    {
        plc.advanceCounter();
    }

    @Override
    public @NotNull InstructionProvider getProvider()
    {
        return Instructions.INJECT;
    }
}
