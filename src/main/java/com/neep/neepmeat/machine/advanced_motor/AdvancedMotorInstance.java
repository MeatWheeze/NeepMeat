package com.neep.neepmeat.machine.advanced_motor;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.client.renderer.BERenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

import java.util.Objects;

public class AdvancedMotorInstance extends BlockEntityInstance<AdvancedMotorBlockEntity> implements DynamicInstance
{
    private final ModelData rotor;
    private final MatrixStack matrices = new MatrixStack();

    public AdvancedMotorInstance(MaterialManager materialManager, AdvancedMotorBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        matrices.translate(getInstancePosition().getX(), getInstancePosition().getY(), getInstancePosition().getZ());

        rotor = materialManager.defaultSolid().material(Materials.TRANSFORMED)
                .getModel(NMExtraModels.P_MOTOR_ROTOR).createInstance();
    }

    @Override
    protected void remove()
    {
        rotor.delete();
    }

    @Override
    public void updateLight()
    {
        relight(getInstancePosition(), rotor);
    }

    @Override
    public void beginFrame()
    {
        matrices.push();
        Direction facing = blockEntity.getCachedState().get(BaseFacingBlock.FACING);

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
        blockEntity.currentSpeed = (float) (blockEntity.rotorSpeed * MathHelper.lerp(0.1, blockEntity.currentSpeed, blockEntity.getSpeed()));
        blockEntity.angle = MathHelper.wrapDegrees(blockEntity.angle + blockEntity.currentSpeed * delta);

        BERenderUtils.rotateFacingSouth(facing, matrices);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(blockEntity.angle));
        matrices.translate(-0.5, -0.5, -0.5);

        rotor.setTransform(matrices);

        matrices.pop();
    }
}
