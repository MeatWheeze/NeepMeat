package com.neep.neepmeat.plc.robot;

import com.neep.neepmeat.api.plc.PLC;
import com.neep.neepmeat.api.plc.robot.RobotAction;
import net.minecraft.util.math.BlockPos;

public class RobotMoveToAction implements RobotAction
{
    protected final BlockPos target;

    public RobotMoveToAction(BlockPos target)
    {
        this.target = target;
    }

    @Override
    public boolean finished(PLC plc)
    {
        return plc.getActuator().reachedTarget(plc);
    }

    @Override
    public void start(PLC plc)
    {
        plc.getActuator().setTarget(plc, target.up());
    }

    @Override
    public void tick(PLC plc)
    {
    }

    @Override
    public void end(PLC plc)
    {

    }
}
