package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.live_machine.block.LargeTrommelBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.LargeTrommelBlockEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class LargeTrommelInstance extends BlockEntityInstance<LargeTrommelBlockEntity> implements DynamicInstance
{
    private final ModelData mesh;

    public LargeTrommelInstance(MaterialManager materialManager, LargeTrommelBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        this.mesh = materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(NMExtraModels.LARGE_TROMMEL_MESH).createInstance();
    }

    @Override
    public void beginFrame()
    {
        float rot = blockEntity.getCachedState().get(LargeTrommelBlock.FACING).asRotation()+ 180;
        mesh.loadIdentity().translate(getInstancePosition())
                .rotateCentered(Direction.DOWN, rot * MathHelper.PI / 180)
                .translate(1, 1, 0)
                .rotate(Direction.NORTH, (world.getTime() + AnimationTickHolder.getPartialTicks()) / 10)
                .translate(-1, -1, 0)
        ;
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), mesh);
    }

    @Override
    protected void remove()
    {
        this.mesh.delete();
    }
}
