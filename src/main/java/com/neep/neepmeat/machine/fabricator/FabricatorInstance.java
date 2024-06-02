package com.neep.neepmeat.machine.fabricator;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.stream.IntStream;

public class FabricatorInstance extends BlockEntityInstance<FabricatorBlockEntity> implements DynamicInstance
{
    private final ModelData rotor;
    private final List<ModelData> segments;

    private float speed;
    private float mainAngle;

    private final MinecraftClient client;

    public FabricatorInstance(MaterialManager materialManager, FabricatorBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        this.client = MinecraftClient.getInstance();
        rotor = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.FABRICATOR_ROTOR).createInstance();
        segments = IntStream.range(0, 8).mapToObj(i ->
                materialManager.defaultCutout().material(Materials.TRANSFORMED).getModel(NMExtraModels.FABRICATOR_SEGMENT).createInstance()).toList();
    }

    @Override
    protected void remove()
    {
        rotor.delete();
        for (var segment : segments)
        {
            segment.delete();
        }
    }

    @Override
    public void beginFrame()
    {

        boolean paused = client.isPaused();
        speed = MathHelper.lerp(paused ? 0 : 0.1f, speed, blockEntity.animation ? 10 : 0);

        if (blockEntity.animation)
            blockEntity.animation = false;

        mainAngle += paused ? 0 : client.getLastFrameDuration() * speed;

        rotor.loadIdentity()
                .translate(getInstancePosition())
                .translate(0, 1, 0)
                .rotateCentered(Direction.UP, mainAngle)
        ;

        for (int i = 0; i < segments.size(); ++i)
        {
            float angle = MathHelper.RADIANS_PER_DEGREE * (((i / 8f) * 360) % 360) + mainAngle;

//            float yOffset = 0.05f * (MathHelper.sin(angle * MathHelper.sin(angle * 3)) + 1);
//            float yOffset = 0.10f * (MathHelper.sin(angle * i * i * MathHelper.sin(angle / 100f)));
//            float i1 = i + 8 * MathHelper.sin(mainAngle / 10f);
            float yOffset = 0.1f * ((MathHelper.sin(mainAngle * (i - 8 * MathHelper.sin(mainAngle / 100f) - 4))));

            segments.get(i)
                    .loadIdentity()
                    .translate(getInstancePosition())
                    .translate(0, 1 - yOffset , 0)
                    .rotateCentered(Direction.UP, angle)
                    ;
        }
    }

    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), segments.stream());
        relight(getWorldPosition(), rotor);
    }

    static
    {
        ClientPlayNetworking.registerGlobalReceiver(FabricatorBlockEntity.CHANNEL_ID, (client, handler, buf, responseSender) ->
        {
            BlockPos pos = buf.readBlockPos();
            client.execute(() ->
            {
                if (client.world.getBlockEntity(pos) instanceof FabricatorBlockEntity fabricator)
                {
                    fabricator.animation = true;
                }
            });
        });
    }
}
