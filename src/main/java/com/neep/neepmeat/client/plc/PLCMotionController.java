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

    private float pitch;
    private float yaw;

    public PLCMotionController(SurgicalRobot robot)
    {
        this.robot = robot;
    }

    public void update()
    {
        if (rotationMode == RotationMode.LOCKED)
        {
            pitch = 20;
        }

        lerpPitch = MathHelper.lerpAngleDegrees(0.3f, lerpPitch,  pitch);
        lerpYaw = MathHelper.lerpAngleDegrees(0.3f, lerpYaw,  yaw);
    }

    public void setPitchYaw(float pitch, float yaw)
    {
        this.pitch = MathHelper.wrapDegrees(pitch);
        this.yaw = MathHelper.wrapDegrees(yaw);
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    enum RotationMode
    {
        LOCKED,
        FREE
    }
}
