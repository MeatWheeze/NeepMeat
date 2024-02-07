package com.neep.neepmeat.plc.block.entity;

import com.neep.meatlib.MeatLib;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.api.storage.LazyBlockApiCache;
import com.neep.neepmeat.neepasm.compiler.variable.VariableStack;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.ExecutorBlock;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.robot.PLCActuator;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntStack;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.function.Consumer;

public class ExecutorBlockEntity extends SyncableBlockEntity implements PLC, PLCExecutor
{
    private final LazyBlockApiCache<Void, Void> cache = LazyBlockApiCache.of(MeatLib.VOID_LOOKUP,
            getPos().offset(getCachedState().get(ExecutorBlock.FACING)), this::getWorld, () -> null);

    @Nullable private Pair<RobotAction, Consumer<PLC>> currentAction;

    private final ArrayDeque<Instruction> instructions = new ArrayDeque<>();
    private Instruction currentInstruction;

    private int flag;
    private final IntStack variableStack = new VariableStack(this, 8); // TODO: save
    @Nullable private Error error;

    public ExecutorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public void serverTick()
    {
        if (error != null)
        {
            stop();
            say(error.what());
        }

        if (currentInstruction == null && !instructions.isEmpty())
        {
            execute(instructions.peek());
        }

        if (currentAction != null)
        {
            if (currentAction.first().finished(this))
            {
                currentAction.first().end(this);
                currentAction.second().accept(this);
                currentAction = null;
                markDirty();
            }
            else
                currentAction.first().tick(this);
        }
    }

    public void execute(Instruction instruction)
    {
        if (currentAction != null)
        {
            currentAction.first().cancel(this);
            currentAction = null;
        }

        currentInstruction = instruction;

        if (instruction.canStart(this))
        {
            instruction.start(this);
            if (currentAction != null)
            {
                currentAction.first().start(this);
            }
        }
        markDirty();
    }

    private void say(Text what)
    {
        PlayerLookup.around((ServerWorld) getWorld(), getPos(), 20).forEach(p -> p.sendMessage(
                Text.of("[Executor at " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ() + "] ").copy().append(what)));
    }

    @Override
    public PLCActuator getActuator()
    {
        if (cache.getBlockEntity() instanceof PLCActuator actuator)
            return actuator;

        return PLCActuator.EMPTY;
    }

    @Override
    public void selectActuator(@Nullable BlockPos pos)
    {

    }

    @Override
    public void addRobotAction(RobotAction action, Consumer<PLC> callback)
    {
        currentAction = Pair.of(action, callback);
    }

    @Override
    public int counter()
    {
        return 0;
    }

    @Override
    public void advanceCounter()
    {
        currentInstruction = null;
        instructions.poll();
        markDirty();
    }

    public void stop()
    {
        currentInstruction = null;
        instructions.clear();

        if (currentAction != null)
        {
            currentAction.first().cancel(this);
            currentAction = null;
        }
        markDirty();
    }

    @Override
    public void pushCall(int data) { }

    @Override
    public int popCall() { return 0; }

    @Override
    public IntStack variableStack()
    {
        return variableStack;
    }

    @Override
    public void setCounter(int counter)
    {
        if (counter == -1)
            stop();
    }

    @Override
    public void raiseError(Error error)
    {
        this.error = error;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putInt("flag", flag);

        NbtList list = new NbtList();
        for (Instruction instruction : instructions)
        {
            NbtCompound instructionNbt = new NbtCompound();
            instruction.writeNbt(instructionNbt);
            instructionNbt.putString("id", Instructions.REGISTRY.getId(instruction.getProvider()).toString());
            list.add(instructionNbt);
        }
        nbt.put("instructions", list);

    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.flag = nbt.getInt("flag");

        instructions.clear();
        NbtList list = nbt.getList("instructions", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            NbtCompound instructionNbt = list.getCompound(i);

            Identifier id = Identifier.tryParse(instructionNbt.getString("id"));
            InstructionProvider provider = Instructions.REGISTRY.get(id);
            if (provider != null)
            {
                instructions.add(provider.createFromNbt(this::getWorld, instructionNbt));
            }
        }
    }

    @Override
    public void flag(int i)
    {
        this.flag = i;
        markDirty();
    }

    @Override
    public int flag()
    {
        return flag;
    }

    @Override
    public void receiveInstruction(Instruction instruction)
    {
        instructions.add(instruction);
    }
}
