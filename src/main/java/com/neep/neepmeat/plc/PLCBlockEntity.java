package com.neep.neepmeat.plc;

import com.google.common.collect.Queues;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.program.PlcProgram;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.neepasm.compiler.variable.Variable;
import com.neep.neepmeat.neepasm.compiler.variable.VariableStack;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.plc.editor.ProgramEditor;
import com.neep.neepmeat.plc.editor.ShellState;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.robot.PLCActuator;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.Stack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PLCBlockEntity extends SyncableBlockEntity implements PLC, ExtendedScreenHandlerFactory
{
    @NotNull protected Supplier<PlcProgram> programSupplier;

    protected Instruction currentInstruction;
    protected int counter;
    private boolean paused;

    protected Queue<Pair<RobotAction, Consumer<PLC>>> robotActions = Queues.newArrayDeque();
    protected Pair<RobotAction, Consumer<PLC>> currentAction;
    protected final SurgicalRobot robot = new SurgicalRobot(this);
    protected boolean overrideController;

    protected final ShellState shell = new ShellState(this);
    private PLCState state;

    private final ProgramEditor editor = new ProgramEditor(this);
    
    private Error error;

    private final PLCPropertyDelegate delegate = new PLCPropertyDelegate();

    private final int maxStackSize = 32;
    private final IntArrayList callStack = new IntArrayList(); // For function level
    private final VariableStack variableStack = new VariableStack(this, maxStackSize); // TODO: save
    private int flag; // TODO: save

    public PLCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        this.state = shell;
        this.programSupplier = () -> null;
    }

    @Override
    public void addRobotAction(RobotAction action, Consumer<PLC> callback)
    {
        robotActions.add(Pair.of(action, callback));
    }

    @Override
    public PLCActuator getRobot()
    {
        if (world.getBlockEntity(pos.up()) instanceof PLCActuator actuator)
            return actuator;

        return robot;
    }

    public SurgicalRobot getSurgeryRobot()
    {
        return robot;
    }

    @Override
    public int counter()
    {
        return counter;
    }

    public void enter(PlayerEntity player)
    {
        if (robot.getController() == null)
        {
            robot.setController(player);
            if (!world.isClient())
            {
                PLCRobotEnterS2C.send(player, this);
            }
            player.openHandledScreen(this);
        }
    }

    @Override
    public void advanceCounter()
    {
        counter++;
    }

    @Override
    public void pushCall(int data)
    {
        if (callStack.size() >= maxStackSize)
        {
            raiseError(new Error("Call stack overflow"));
            return;
        }

        callStack.push(data);
    }

    @Override
    public int popCall()
    {
        if (callStack.isEmpty())
        {
            raiseError(new Error("Calls stack underflow"));
            return 0;
        }

        return callStack.popInt();
    }

    @Override
    public Stack<Variable<?>> variableStack()
    {
        return variableStack;
    }

    @Override
    public void setCounter(int counter)
    {
        if (counter == -1)
            stop();
        else
            this.counter = counter;
    }

    @Override
    public void raiseError(Error error)
    {
        getWorld().getPlayers().forEach(p -> p.sendMessage(error.what()));
        this.error = error;

        paused = true;

        robotActions.clear();
        if (currentAction != null)
        {
            currentAction.first().cancel(this);
            currentAction = null;
        }
        robot.stay();

        callStack.clear();

        if (world instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, robot.getX(), robot.getY(), robot.getZ(), 20, 0.25, 0.25, 0.25, 0.1);
        }
    }

    @Override
    public void flag(int i) { this.flag = i; }

    @Override
    public int flag() { return flag; }

    public void resetError()
    {
        error = null;
    }

    public @Nullable Error getError()
    {
        return error;
    }

    public void tick()
    {
        PlcProgram program = programSupplier.get();
        if (!paused && counter != -1 && program != null && error == null)
        {
            Instruction instruction = program.get(counter);
            if (instruction != currentInstruction)
            {
                execute(instruction);
                sync();
            }
        }

        if (currentAction == null || currentAction.first().finished(this))
        {
            if (currentAction != null)
            {
                // Finish current action
                currentAction.second().accept(this);
                currentAction.first().end(this);
            }

            if (robotActions.peek() != null)
            {
                currentAction = robotActions.poll();
                currentAction.first().start(this);
                sync();
            }
            else
            {
                if (currentAction != null)
                {
                    robot.stay();
                    currentAction = null;
                    sync();
                }
            }
        }
        else
        {
            currentAction.first().tick(this);
        }

        // Set override
        boolean prevOverride = overrideController;
        if (currentAction == null || currentAction.first().finished(this))
        {
            overrideController = false;
        }
        else
        {
            overrideController = currentAction.first().blocksController();
        }
        if (overrideController != prevOverride)
        {
            sync();
        }

        if (world instanceof ServerWorld serverWorld)
        {
            if (robot.shouldUpdatePosition(serverWorld))
            {
                robot.syncPosition(serverWorld);
            }
        }

        robot.tick();

//        sync();
    }

    public boolean notExecuting()
    {
        return currentAction == null || currentAction.first().finished(this);
    }

    public void execute(Instruction instruction)
    {
        robotActions.clear();
        if (currentAction != null)
        {
            currentAction.first().cancel(this);
            currentAction = null;
        }

        currentInstruction = instruction;

        if (instruction.canStart(this))
        {
            instruction.start(this);
        }
    }

    public boolean overrideController()
    {
        return overrideController;
    }

    public void clientTick()
    {
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        robot.writeNbt(nbt);
        nbt.putBoolean("override_controller", overrideController);
        nbt.putInt("counter", counter);
        nbt.putBoolean("paused", paused);

        nbt.put("editor", editor.writeNbt(new NbtCompound()));

        nbt.putBoolean("has_program", programSupplier.get() != null);

        nbt.putIntArray("call_stack", callStack);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        robot.writeNbt(nbt);
        nbt.putBoolean("override_controller", overrideController);
        nbt.putInt("counter", counter);
        nbt.putBoolean("paused", paused);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        robot.readNbt(nbt);
        this.overrideController = nbt.getBoolean("override_controller");
        this.counter = nbt.getInt("counter");
        this.paused = nbt.getBoolean("paused");

        editor.readNbt(nbt.getCompound("editor"));

        callStack.clear();
        callStack.addAll(IntList.of(nbt.getIntArray("call_stack")));

        if (nbt.getBoolean("has_program"))
        {
            programSupplier = editor::getProgram;
        }
    }

    public void exit()
    {
        robot.setController(null);
    }

    public PLCState getState()
    {
        return state;
    }

    public boolean actionBlocksController()
    {
        return currentAction != null && currentAction.first().blocksController();
    }

    public void setMode(RecordMode value)
    {
        switch (value)
        {
            case IMMEDIATE -> state = shell;
//            case RECORD -> state = editor;
        }
        markDirty();
    }

    @Override
    public Text getDisplayName()
    {
        return Text.empty();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new PLCScreenHandler(syncId, this, delegate, editor.getSource());
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeBlockPos(pos);
        buf.writeString(editor.getSource());
    }

    public void runProgram(@Nullable PlcProgram program)
    {
        resetError();
        currentInstruction = null;
        paused = false;
        this.programSupplier = () -> program;
    }

    public void hardStop()
    {
        if (currentInstruction != null)
        {
            currentInstruction.cancel(this);
            currentInstruction = null;
        }
        if (currentAction != null)
        {
            currentAction = null;
            robotActions.clear();
        }
        stop();
    }

    public void stop()
    {
        programSupplier = () -> null;
        counter = 0;
        paused = true;
        callStack.clear();
    }

    public void pause()
    {
        paused = true;
    }

    public ProgramEditor getProgramEditor()
    {
        return editor;
    }

    public class PLCPropertyDelegate implements PropertyDelegate
    {
        public static final int SIZE = Names.values().length;

        @Override
        public int get(int index)
        {
            return switch (Names.values()[index])
            {
                case PROGRAM_COUNTER -> counter;
                case HAS_PROGRAM -> programSupplier.get() != null ? 1 : 0;
                case EDIT_MODE -> state.getMode().ordinal();
                case RUNNING -> (programSupplier.get() != null && !paused) ? 1 : 0;
                case ARGUMENT -> state.getArgumentCount();
                case MAX_ARGUMENTS -> state.getMaxArguments();
                case SELECTED_INSTRUCTION -> 0;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (Names.values()[index])
            {
                case PROGRAM_COUNTER -> counter = value;
                case EDIT_MODE -> setMode(RecordMode.values()[MathHelper.clamp(value, 0, RecordMode.values().length)]);
                case SELECTED_INSTRUCTION -> {}
            }
        }

        @Override
        public int size()
        {
            return SIZE;
        }

        public enum Names
        {
            PROGRAM_COUNTER,
            HAS_PROGRAM,
            EDIT_MODE,
            RUNNING,
            ARGUMENT,
            MAX_ARGUMENTS,
            SELECTED_INSTRUCTION,
        }
    }
}
