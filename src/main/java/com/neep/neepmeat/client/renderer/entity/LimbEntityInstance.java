package com.neep.neepmeat.client.renderer.entity;

import com.google.common.collect.Lists;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.PartialModel;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.entity.LimbEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

import java.util.List;

public class LimbEntityInstance extends EntityInstance<LimbEntity> implements DynamicInstance
{
    private final ModelData model;
    private final MatrixStack matrices = new MatrixStack();

    private static final List<PartialModel> LIMBS = Lists.newArrayList();

    static
    {
        LIMBS.add(NMExtraModels.COW_LIMB);
        LIMBS.add(NMExtraModels.COW_HEAD);
        LIMBS.add(NMExtraModels.PIG_HEAD);
        LIMBS.add(NMExtraModels.PIG_HEAD_1);
        LIMBS.add(NMExtraModels.UNKNOWN_LIMB_1);
        LIMBS.add(NMExtraModels.UNKNOWN_LIMB_2);
        LIMBS.add(NMExtraModels.UNKNOWN_BODY_1);
    }

    public LimbEntityInstance(MaterialManager materialManager, LimbEntity entity)
    {
        super(materialManager, entity);

        model = createModel();
    }

    private ModelData createModel()
    {
        int id = (int) Math.round(Math.random() * (LIMBS.size() - 1));

        return materialManager.defaultSolid()
                .material(Materials.TRANSFORMED)
                .getModel(LIMBS.get(id))
//                .model(entity.getType(), LimbEntityInstance::createModel)
                .createInstance();
    }

//    private static ModelPart createModel()
//    {
//        return ModelPart.builder("bell", 64, 64)
//                .cuboid()
//                .start(6, 0.0F, 2.0F)
//                .size(4, 4, 14F)
//                .textureOffset(0, 0)
//                .endCuboid()
//                .build();
//    }

    @Override
    protected void remove()
    {
        model.delete();
    }

    @Override
    public void beginFrame()
    {
        matrices.push();
        float tickDelta = AnimationTickHolder.getPartialTicks();
        var pos = getInstancePosition(tickDelta);
        matrices.translate(pos.getX(), pos.getY(), pos.getZ());

        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(entity.getYaw(tickDelta)));
        matrices.translate(-0.5, 0, -0.5);

        model.setTransform(matrices);
        matrices.pop();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), model);
    }
}
