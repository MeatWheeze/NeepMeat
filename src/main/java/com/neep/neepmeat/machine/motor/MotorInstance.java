package com.neep.neepmeat.machine.motor;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class MotorInstance extends BlockEntityInstance<MotorBlockEntity> implements DynamicInstance
{
    private final ModelData rotor;
    private final MatrixStack matrices = new MatrixStack();

    public MotorInstance(MaterialManager materialManager, MotorBlockEntity blockEntity)
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
    public void beginFrame()
    {
        matrices.push();
        Direction facing = blockEntity.getCachedState().get(BaseFacingBlock.FACING);

        float delta = MinecraftClient.getInstance().isPaused() ? 0 : MinecraftClient.getInstance().getLastFrameDuration();
        blockEntity.currentSpeed = (float) (blockEntity.rotorSpeed * MathHelper.lerp(0.1, blockEntity.currentSpeed, blockEntity.getSpeed()));
        blockEntity.angle = MathHelper.wrapDegrees(blockEntity.angle + blockEntity.currentSpeed * delta);

        rotor.loadIdentity().translate(getInstancePosition()).centre()
            .multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(facing.asRotation()))
            .multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(facing == Direction.UP ? 90 : facing == Direction.DOWN ? -90 : 0))
            .multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(blockEntity.angle))
            .unCentre();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), rotor);
    }
}
