package com.neep.neepmeat.client.plc;

import com.neep.neepmeat.machine.surgical_controller.SurgicalRobot;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(value = EnvType.CLIENT)
public class PLCMotionController
{
    private final SurgicalRobot robot;

    private final RotationMode rotationMode = RotationMode.FREE;

    public float lerpPitch;
    public float lerpYaw;

    public PLCMotionController(SurgicalRobot robot)
    {
        this.robot = robot;
        this.lerpPitch = robot.getPitch();
        this.lerpYaw = robot.getYaw();
    }

    public void update()
    {
        if (rotationMode == RotationMode.LOCKED)
        {
            robot.setPitchYaw(20, robot.getYaw());
        }

        lerpPitch = MathHelper.lerpAngleDegrees(0.3f, lerpPitch,  robot.getPitch());
        lerpYaw = MathHelper.lerpAngleDegrees(0.3f, lerpYaw,  robot.getYaw());
    }

    public void setPitchYaw(float pitch, float yaw)
    {
        robot.setPitchYaw(MathHelper.wrapDegrees(pitch), MathHelper.wrapDegrees(yaw));
    }

    public float getPitch()
    {
        return robot.getPitch();
    }

    public float getYaw()
    {
        return robot.getYaw();
    }

    enum RotationMode
    {
        LOCKED,
        FREE
    }
}
