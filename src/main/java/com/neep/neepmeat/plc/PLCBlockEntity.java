package com.neep.neepmeat.plc;

import com.google.common.collect.Queues;
import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import com.neep.neepmeat.network.plc.PLCRobotC2S;
import com.neep.neepmeat.network.plc.PLCRobotEnterS2C;
import com.neep.neepmeat.plc.editor.ImmediateState;
import com.neep.neepmeat.plc.editor.ProgramEditorState;
import com.neep.neepmeat.plc.program.MutableProgram;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.program.PLCProgramImpl;
import com.neep.neepmeat.plc.program.PlcProgram;
import com.neep.neepmeat.plc.robot.RobotAction;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.function.Consumer;

public class PLCBlockEntity extends SyncableBlockEntity implements PLC
{
    @Nullable protected MutableProgram editingProgram;

    @Nullable protected PlcProgram program;
    protected Instruction currentInstruction;
    protected int counter;

    protected Queue<Pair<RobotAction, Consumer<PLC>>> robotActions = Queues.newArrayDeque();
    protected Pair<RobotAction, Consumer<PLC>> currentAction;
    protected final SurgicalRobot robot = new SurgicalRobot(this);

    protected final ProgramEditorState editor = new ProgramEditorState(this);
    protected final ImmediateState immediate = new ImmediateState(this);

    private PLCState state;

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
        }
    }

    @Override
    public void advanceCounter()
    {
        counter++;
    }

    @Override
    public void raiseError()
    {
        getWorld().getPlayers().forEach(p -> p.sendMessage(Text.of("NOOOOOOOOOOOOO")));
    }

    public void setCounter(int counter)
    {
        this.counter = counter;
    }

    public void tick()
    {
        if (program != null)
        {
            Instruction instruction = program.get(counter);
            if (instruction != currentInstruction)
            {
                currentInstruction = instruction;

                if (instruction.canStart(this))
                {
                    instruction.start(program, this);
                }
            }
        }

            if (currentAction == null || currentAction.first().finished())
            {
                if (currentAction != null)
                    currentAction.second().accept(this);

                currentAction = null;
                if (robotActions.peek() != null)
                {
                    currentAction = robotActions.poll();
                    currentAction.first().start();
                }
                else
                {
                    robot.returnToBase();
                    currentAction = null;
                }
            }
            else
            {
                currentAction.first().tick();

                if (world instanceof ServerWorld serverWorld)
                {
                    if (robot.shouldUpdatePosition(serverWorld))
                    {
                        PLCRobotC2S.send(this, serverWorld);
                    }
                }
            }

            robot.tick();

//        sync();
    }

    public void clientTick()
    {
        robot.tick();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        robot.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        robot.readNbt(nbt);
    }

    public void exit()
    {
        getRobot().setController(null);
    }

    public MutableProgram getEditProgram()
    {
        if (editingProgram == null)
        {
            editingProgram = new PLCProgramImpl(this::getWorld);
        }

        return editingProgram;
    }

    public ProgramEditorState getEditor()
    {
        return editor;
    }

    public void runProgram(PlcProgram program)
    {
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
}
