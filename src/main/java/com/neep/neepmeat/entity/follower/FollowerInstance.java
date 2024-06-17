package com.neep.neepmeat.entity.follower;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.TickableInstance;
import com.jozufozu.flywheel.backend.instancing.entity.EntityInstance;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FollowerInstance extends EntityInstance<FollowerEntity> implements TickableInstance
{
//    private final List<ModelData> bits = new ArrayList<>();

    public FollowerInstance(MaterialManager materialManager, FollowerEntity entity)
    {
        super(materialManager, entity);
    }

    @Override
    protected void remove()
    {

    }

    @Override
    public void tick()
    {

    }
}
