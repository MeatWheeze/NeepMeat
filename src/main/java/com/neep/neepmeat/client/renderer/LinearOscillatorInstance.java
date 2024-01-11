package com.neep.neepmeat.client.renderer;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.breaker.LinearOscillatorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(value = EnvType.CLIENT)
public class LinearOscillatorInstance extends BlockEntityInstance<LinearOscillatorBlockEntity> implements DynamicInstance
{
    private final ModelData armature;
    private float extension = 0;

    public LinearOscillatorInstance(MaterialManager materialManager, LinearOscillatorBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);

        armature = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LO_ARMATURE).createInstance();
    }

    @Override
    protected void remove()
    {
        armature.delete();
    }

    @Override
    public void beginFrame()
    {
        Direction facing = blockEntity.getCachedState().get(BaseFacingBlock.FACING);
        extension = (float) MathHelper.lerp(0.3, extension, blockEntity.extension);
        float maxExtension = 1 / 16f * 9;
        Quaternion yaw = Vec3f.NEGATIVE_Y.getDegreesQuaternion(facing.asRotation());

        armature.loadIdentity()
                .translate(getInstancePosition())
                .centre()
                .multiply(yaw)
                .unCentre()
                .translate(0, 0, extension * maxExtension);


//        BERenderUtils.rotateFacingSouth(facing, matrices);
//        matrices.translate(0.5, 0.5, 0.5);
//        matrices.translate(0, 0, blockEntity.clientExtension * maxExtension);
//        matrices.translate(-0.5, -0.5, -0.5);
//        BERenderUtils.renderModel(NMExtraModels.LO_ARMATURE, matrices, blockEntity.getWorld(), blockEntity.getPos(), blockEntity.getCachedState(), vertexConsumers);
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), armature);
    }
}
