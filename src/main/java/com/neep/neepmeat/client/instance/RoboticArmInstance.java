package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.plc.arm.RoboticArmBlockEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class RoboticArmInstance extends BlockEntityInstance<RoboticArmBlockEntity> implements DynamicInstance
{
    private final ModelData spinnyBit;
    private final ModelData segment1;
    private final ModelData segment2;
    private final MatrixStack matrices = new MatrixStack();

    public RoboticArmInstance(MaterialManager materialManager, RoboticArmBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        this.spinnyBit = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.ROBOTIC_ARM_SPINNY_BIT).createInstance();
        this.segment1 = materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(NMExtraModels.ROBOTIC_ARM_SEGMENT_1).createInstance();
        this.segment2 = materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(NMExtraModels.ROBOTIC_ARM_SEGMENT_2).createInstance();
        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());
    }

    @Override
    protected void remove()
    {
        spinnyBit.delete();
        segment1.delete();
        segment2.delete();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), spinnyBit, segment1, segment2);
    }

    @Override
    public void beginFrame()
    {
        blockEntity.prevX = MathHelper.lerp(0.2, blockEntity.prevX, blockEntity.getX());
        blockEntity.prevY = MathHelper.lerp(0.2, blockEntity.prevY, blockEntity.getY());
        blockEntity.prevZ = MathHelper.lerp(0.2, blockEntity.prevZ, blockEntity.getZ());

//        Vec3d tipTarget = be.getTarget(tickDelta);
        double targetX = blockEntity.prevX;
        double targetY = blockEntity.prevY;
        double targetZ = blockEntity.prevZ;

        double relX = targetX - (blockEntity.getPos().getX() + 0.5);
        double relY = targetY - (blockEntity.getPos().getY() + 1 + 2 / 16f);
        double relZ = targetZ - (blockEntity.getPos().getZ() + 0.5);

        float yaw = (float) Math.toDegrees(Math.atan2(relX, relZ));

        float l = 2 + 5 / 16f;
        double lx = Math.sqrt(relX * relX + relZ * relZ);
        double ly = relY;

        double d5 = Math.sqrt(lx * lx + ly * ly);

        // Constrain
        double x1 = lx / d5 * Math.min(d5, 2 * l - 0.0001);
        double y1 = ly / d5 * Math.min(d5, 2 * l - 0.0001);

        double d = Math.sqrt((x1 * x1 + y1 * y1));

        double x2 = x1 / d;
        double y2 = y1 / d;

        float a = (float) Math.acos((-(d * d)) / (-2 * d * l));

        // Rotation matrix
        double x3 = l * (x2 * MathHelper.cos(a) - y2 * MathHelper.sin(a));
        double y3 = l * (x2 * MathHelper.sin(a) + y2 * MathHelper.cos(a));

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw));
        matrices.translate(-0.5, -0.5, -0.5);

        spinnyBit.setTransform(matrices);
//        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SPINNY_BIT, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);

        matrices.push();
        matrices.translate(0, 2 - 1 / 16f, 0);
        matrices.translate(0.5, -12 / 16f, 0.5);
        double angle1 = Math.atan2(x3, y3);
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) angle1));
        matrices.translate(-0.5, 12 / 16f, -0.5);
        segment1.setTransform(matrices);
//        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SEGMENT_1, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();

        matrices.translate(0, y3 + 2 - 1 / 16f, x3);
        matrices.translate(0.5, -12 / 16f, 0.5);
        double angle2 = Math.atan2((lx - x3), (ly - y3));
        matrices.multiply(RotationAxis.POSITIVE_X.rotation((float) angle2));
        matrices.translate(-0.5, 12 / 16f, -0.5);
        segment2.setTransform(matrices);
//        BERenderUtils.renderModelSmooth(NMExtraModels.ROBOTIC_ARM_SEGMENT_2, matrices, be.getWorld(), be.getPos().up(), be.getCachedState(), vertexConsumers);
        matrices.pop();
    }
}
