package com.neep.neepmeat.plc.robot;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class GroupedRobotAction implements RobotAction
{
    protected final Queue<RobotAction> actions;
    protected boolean start;

    public GroupedRobotAction(Queue<RobotAction> actions)
    {
        this.actions = actions;
    }

    public static GroupedRobotAction of(RobotAction... actions)
    {
        return new GroupedRobotAction(new ArrayDeque<>(List.of(actions)));
    }

    @Override
    public boolean finished()
    {
        return actions.isEmpty();
    }

    @Override
    public void start()
    {

    }

    @Override
    public void tick()
    {
        if (!actions.isEmpty())
        {
            if (!start)
            {
                actions.peek().start();
                start = true;
            }
            actions.peek().tick();
        }

        if (!actions.isEmpty())
        {
            if (actions.peek().finished())
            {
                actions.poll();
                start = false;
            }
        }
    }
}
