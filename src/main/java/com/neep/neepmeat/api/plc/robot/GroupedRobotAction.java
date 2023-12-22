package com.neep.neepmeat.api.plc.robot;

import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.api.plc.PLC;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class GroupedRobotAction implements RobotAction, NbtSerialisable
{
    protected final List<RobotAction> actions;
    protected int index = 0;
    private boolean start;
    private boolean justLoaded;

    public GroupedRobotAction(List<RobotAction> actions)
    {
        this.actions = actions;
    }

    public static GroupedRobotAction of(RobotAction... actions)
    {
        return new GroupedRobotAction(List.of(actions));
    }

    @Override
    public boolean finished(PLC plc)
    {
        return index >= actions.size();
    }

    @Override
    public void start(PLC plc)
    {
        start = true;

        if (!justLoaded)
        {
            // The action must be reset to its initial previous state.
            index = 0;
        }
        else
        {
            // If the action has been loaded from NBT, use the stored index.
            justLoaded = false;
        }
    }

    @Override
    public void tick(PLC plc)
    {
        if (index >= actions.size())
            return;

        var action = actions.get(index);

        if (start)
        {
            action.start(plc);
            start = false;
        }

        action.tick(plc);

        if (action.finished(plc))
        {
            index++;
            start = true;
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("index", index);
//        nbt.putBoolean("start", start);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.index = nbt.getInt("index");
//        this.start = nbt.getBoolean("start");
        this.justLoaded = true;
    }
}
