package com.neep.neepmeat.machine.flywheel;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;

public class FlywheelInstance extends BlockEntityInstance<FlywheelBlockEntity> implements DynamicInstance
{
    private final ModelData rotor;

    public FlywheelInstance(MaterialManager materialManager, FlywheelBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        rotor = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.FLYWHEEL).createInstance();
        rotor.loadIdentity().translate(getInstancePosition());
    }

    @Override
    protected void remove()
    {
        rotor.delete();
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), rotor);
    }

    @Override
    public void beginFrame()
    {

    }
}
