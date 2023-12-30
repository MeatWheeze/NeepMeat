package com.neep.neepmeat.machine.surgical_controller;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.plc.PLCHudRenderer;
import com.neep.neepmeat.plc.PLCBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
public class PLCInstance extends BlockEntityInstance<PLCBlockEntity> implements DynamicInstance
{
    private final ModelData robotModel;
    private final MatrixStack matrixStack = new MatrixStack();

    public PLCInstance(MaterialManager materialManager, PLCBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        matrixStack.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());

        robotModel = materialManager.defaultCutout().material(Materials.TRANSFORMED)
                .getModel(NMExtraModels.P_PLC_ROBOT).createInstance();
    }

    @Override
    protected void remove()
    {
        robotModel.delete();
    }

    @Override
    public void beginFrame()
    {
        var robot = blockEntity.getRobot();
        robot.prevX = robot.clientX;
        robot.prevY = robot.clientY;
        robot.prevZ = robot.clientZ;

        // Smooth the robot's position
        robot.clientX = MathHelper.lerp(0.1d, robot.clientX, robot.getX());
        robot.clientY = MathHelper.lerp(0.1d, robot.clientY, robot.getY());
        robot.clientZ = MathHelper.lerp(0.1d, robot.clientZ, robot.getZ());
        robot.clientYaw = MathHelper.lerpAngleDegrees(0.1f, robot.clientYaw, robot.getYaw());

        double vx = robot.clientX - robot.prevX;
        double vy = robot.clientY - robot.prevY;
        double vz = robot.clientZ - robot.prevZ;

        double speed = (float) Math.sqrt(vx * vx + vz * vz);

        // Only render the robot in 3rd person
        PLCHudRenderer plcHudRenderer = PLCHudRenderer.getInstance();
        if (plcHudRenderer != null && plcHudRenderer.getBlockEntity() == blockEntity)
        {
            robotModel.loadIdentity().scale(0, 0, 0);
        }
        else
        {
            matrixStack.push();
            matrixStack.translate(
                    robot.clientX - blockEntity.getPos().getX() - 0.5,
                    robot.clientY - blockEntity.getPos().getY() - 0.5,
                    robot.clientZ - blockEntity.getPos().getZ() - 0.5);

            float tickDelta = MinecraftClient.getInstance().getTickDelta();
            matrixStack.translate(0, 0.5 + (robot.isActive() ? 0.05 * Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 10f) : 0), 0);

            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(robot.clientYaw + 180));
            matrixStack.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion((float) (100 * speed)));
            matrixStack.translate(-0.5, -0.5, -0.5);

            robotModel.setTransform(matrixStack);

            matrixStack.pop();
        }
    }

    @Override
    public void updateLight()
    {
        relight(getInstancePosition(), robotModel);
    }
}