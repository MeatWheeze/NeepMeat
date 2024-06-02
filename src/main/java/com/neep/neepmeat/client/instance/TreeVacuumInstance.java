package com.neep.neepmeat.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.api.instance.TickableInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.machine.live_machine.block.TreeVacuumBlock;
import com.neep.neepmeat.machine.live_machine.block.entity.TreeVacuumBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TreeVacuumInstance extends BlockEntityInstance<TreeVacuumBlockEntity> implements DynamicInstance
{
    private final ModelData lungL;
    private final ModelData lungR;
    private final ModelData plunger;

    public TreeVacuumInstance(MaterialManager materialManager, TreeVacuumBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        lungL = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.TREE_VACUUM_LUNG).createInstance();
        lungR = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.TREE_VACUUM_LUNG).createInstance();
        plunger = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.TREE_VACUUM_PLUNGER).createInstance();
    }

    @Override
    protected void remove()
    {
        lungL.delete();
        lungR.delete();
        plunger.delete();
    }

    private float easeOutSine(float x)
    {
        return MathHelper.sin(x * MathHelper.PI / 2f);
    }

    @Override
    public void beginFrame()
    {
        Direction facing = blockEntity.getCachedState().get(TreeVacuumBlock.FACING);

        int animationTicks = blockEntity.animationTicks;
        float t = MathHelper.lerp(AnimationTickHolder.getPartialTicks(), animationTicks, animationTicks + 1);
//        float t = animationTicks + AnimationTickHolder.getPartialTicks();

        float plungerOffset;
//        if (animationTicks < 3)
//            plungerOffset = easeOutSine(1 - t / 3);
//        else
//            plungerOffset = easeOutSine( t / 7);
        if (t > 7)
            plungerOffset = easeOutSine(1 - (t - 7) / 3);
        else
            plungerOffset = easeOutSine((t) / 7);

        float tRad = 2 * MathHelper.PI * t / 10 + MathHelper.PI;
        float sf = 1 + 0.2f * (MathHelper.cos(tRad) + 1) / 2;

        plunger.loadIdentity()
                .translate(getInstancePosition())
                .centre()
                .rotateToFace(facing)
                .unCentre()
//                .translate(0, 0, 10 / 16f * (MathHelper.cos(tRad) + 1) / 2)
                .translate(0, 0, 7 / 16f * plungerOffset)
        ;

        lungL.loadIdentity()
                .translate(getInstancePosition())
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .translate(-2 / 16f, 0.5, 0.5)
                .scale(sf, 1f, sf)
                .translate(2 / 16f, -0.5, -0.5)
        ;

        // Look at all these numbers that I arrived at through trial and error!
        lungR.loadIdentity().translate(getInstancePosition())
                .centre()
                .rotateToFace(facing)
                .unCentre()
                .translate(13 / 16f, 0, 4 / 16f)
                .rotate(Direction.UP, MathHelper.PI)
                .translate(-13 / 16f, 0, -4 / 16f)
                .translate(10 / 16f, 0, 0)
                .translate(-2 / 16f, 0.5, 0 / 16f)
                .scale(sf, 1f, sf)
                .translate(2 / 16f, -0.5, -0 / 16f)
        ;
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), lungL, lungR, plunger);
    }

    static
    {
        // I wish I knew how chests sync their animations. This is horrible.
        ClientPlayNetworking.registerGlobalReceiver(TreeVacuumBlockEntity.CHANNEL_ID, (client, handler, buf, responseSender) ->
        {
            BlockPos pos = buf.readBlockPos();
            client.execute(() ->
            {
                if (client.world.getBlockEntity(pos) instanceof TreeVacuumBlockEntity be)
                {
                    be.animationTicks = 10;
                }
            });
        });
    }
}
