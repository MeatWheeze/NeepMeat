package com.neep.neepmeat.plc;

import com.google.common.collect.Queues;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.plc.editor.ImmediateState;
import com.neep.neepmeat.plc.editor.ProgramEditorState;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.program.PlcProgram;
import com.neep.neepmeat.plc.robot.RobotAction;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import it.unimi.dsi.fastutil.Pair;
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
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.function.Consumer;

public class PLCBlockEntity extends SyncableBlockEntity implements PLC, ExtendedScreenHandlerFactory
{
    @Nullable protected PlcProgram program;
    protected Instruction currentInstruction;
    protected int counter;

    protected Queue<Pair<RobotAction, Consumer<PLC>>> robotActions = Queues.newArrayDeque();
    protected Pair<RobotAction, Consumer<PLC>> currentAction;
    protected final SurgicalRobot robot = new SurgicalRobot(this);
    protected boolean overrideController;

    protected final ProgramEditorState editor = new ProgramEditorState(this);
    protected final ImmediateState immediate = new ImmediateState(this);
    private PLCState state;

    private Error error;

    private PLCPropertyDelegate delegate = new PLCPropertyDelegate();

    public PLCBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        this.state = immediate;

//        PLCProgramImpl program1 = new PLCProgramImpl();
//        program1.add(new CombineInstruction(pos.up(3).south(4), pos.up(3).north(5), () -> (ServerWorld) this.getWorld()));
//        program1.add(PLCInstruction.EMPTY);
//
//        this.program = program1;
    }


    @Override
    public RobotAction addRobotAction(RobotAction action, Consumer<PLC> callback)
    {
        robotActions.add(Pair.of(action, callback));
        return action;
    }

    public SurgicalRobot getRobot()
    {
        return robot;
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
    public void advanceCounter(int increment)
    {
        counter += increment;
    }

    @Override
    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    @Override
    public void raiseError(Error error)
    {
        getWorld().getPlayers().forEach(p -> p.sendMessage(Text.of("NOOOOOOOOOOOOO")));
        this.error = error;

        currentInstruction = null;

        robotActions.clear();
        if (currentAction != null)
        {
            currentAction.first().cancel();
            currentAction = null;
        }
        robot.stay();

        if (world instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(ParticleTypes.SMOKE, robot.getX(), robot.getY(), robot.getZ(), 20, 0.25, 0.25, 0.25, 0.1);
        }
    }

    public void resetError()
    {
        error = null;
        counter = 0;
    }

    public @Nullable Error getError()
    {
        return error;
    }

    public void tick()
    {
        if (counter != -1 && program != null && error == null)
        {
            Instruction instruction = program.get(counter);
            if (instruction != currentInstruction)
            {
                execute(instruction);
                sync();
            }
        }

        if (currentAction == null || currentAction.first().finished())
        {
            if (currentAction != null)
                currentAction.second().accept(this);

            if (robotActions.peek() != null)
            {
                currentAction = robotActions.poll();
                currentAction.first().start();
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
            currentAction.first().tick();
        }

        boolean prevOverride = overrideController;
        if (currentAction == null || currentAction.first().finished())
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
                PLCRobotC2S.send(this, serverWorld);
            }
        }

        robot.tick();

//        sync();
    }

    public boolean notExecuting()
    {
        return currentAction == null || currentAction.first().finished();
    }

    public void execute(Instruction instruction)
    {
        robotActions.clear();
        if (currentAction != null)
        {
            currentAction.first().cancel();
            currentAction = null;
        }

        currentInstruction = instruction;
        counter = 0;

        if (instruction.canStart(this))
        {
            instruction.start(program, this);
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
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        robot.readNbt(nbt);
        this.overrideController = nbt.getBoolean("override_controller");
    }

    public void exit()
    {
        getRobot().setController(null);
    }

    public ProgramEditorState getEditor()
    {
        return editor;
    }

    public void runProgram(PlcProgram program)
    {
        resetError();
        setCounter(0);
        this.program = program;
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
            case IMMEDIATE -> state = immediate;
            case RECORD -> state = editor;
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
        return new PLCScreenHandler(syncId, this, delegate);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeBlockPos(pos);
    }

    public void stopRunning()
    {
        program = null;
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
                case EDIT_MODE -> state.getMode().ordinal();
                case RUNNING -> notExecuting() ? 0 : 1;
            };
        }

        @Override
        public void set(int index, int value)
        {
            switch (Names.values()[index])
            {
                case PROGRAM_COUNTER -> counter = value;
                case EDIT_MODE -> setMode(RecordMode.values()[MathHelper.clamp(value, 0, RecordMode.values().length)]);
                case RUNNING -> {}
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
            EDIT_MODE,
            RUNNING,
        }
    }
}
