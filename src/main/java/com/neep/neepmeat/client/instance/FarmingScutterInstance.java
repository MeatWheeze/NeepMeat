package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.entity.scutter.FarmingScutter;
import net.minecraft.util.math.Direction;

public class FarmingScutterInstance extends EntityInstance<FarmingScutter> implements DynamicInstance
{
    private final ModelData body;

    public FarmingScutterInstance(MaterialManager materialManager, FarmingScutter entity)
    {
        super(materialManager, entity);
        body = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.FARMING_SCUTTER).createInstance();
    }

    @Override
    public void beginFrame()
    {
        float tickDelta = AnimationTickHolder.getPartialTicks();
        body
                .loadIdentity()
                .translate(getInstancePosition(tickDelta))
                .rotate(Direction.UP, (float) Math.toRadians(180 - entity.getYaw(tickDelta)))
                .translate(-0.5,  0, -0.5)
        ;
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), body);
    }

    @Override
    protected void remove()
    {
        body.delete();
    }
}
