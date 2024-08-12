package com.neep.neepmeat.neepbus.client.instance;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.backend.instancing.blockentity.BlockEntityInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.model.ModelData;
import com.neep.neepmeat.client.NMExtraModels;
import com.neep.neepmeat.neepbus.block.entity.LinearLeverBlockEntity;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

public class LinearLeverInstance extends BlockEntityInstance<LinearLeverBlockEntity> implements DynamicInstance
{
    private float lerpOffset = 0;

    private final ModelData handle;

    public LinearLeverInstance(MaterialManager materialManager, LinearLeverBlockEntity blockEntity)
    {
        super(materialManager, blockEntity);
        handle = materialManager.defaultSolid().material(Materials.TRANSFORMED).getModel(NMExtraModels.LINEAR_LEVER_HANDLE).createInstance();
    }

    @Override
    protected void remove()
    {
        handle.delete();
    }

    @Override
    public void beginFrame()
    {
        float f = 10 / 16f * clamp((float) blockEntity.getValue() / (blockEntity.getMaxValue() - blockEntity.getMinValue()), 0f, 1f);
        lerpOffset = MathHelper.lerp(0.4f, lerpOffset, f);

        Quaternionf facing = rotateThing(blockState.get(WallMountedBlock.FACING), blockState.get(WallMountedBlock.FACE));
        handle.loadIdentity()
                .translate(getInstancePosition())
                .centre()
                .multiply(facing)
                .translate(0, 0, lerpOffset)
                .unCentre()
                ;
    }


    @Override
    public void updateLight()
    {
        relight(getWorldPosition(), handle);
    }

    // What's easier? Work out how to use the same rotation values as in the blockstate file?
    // Or spend an hour trying to get them right through trial and error?
    // Yay! Trial and error again!
    private Quaternionf rotateThing(Direction horFacing, WallMountLocation face)
    {
        if (face == WallMountLocation.WALL)
        {
            return switch (horFacing)
            {
                case NORTH -> euler(-90, 0);
                case SOUTH -> euler(-90, 180);
                case EAST -> euler(-90, -90);
                default -> euler(-90, 90);
            };
        }
        else if (face == WallMountLocation.FLOOR)
        {
            return switch (horFacing)
            {
                case NORTH -> RotationAxis.POSITIVE_Y.rotationDegrees(180);
                case SOUTH -> new Quaternionf();
                case EAST -> euler(0, 90);
                default -> euler(0, -90);
            };
        }
        else
        {
            return switch (horFacing)
            {
                case NORTH -> euler(180, 0);
                case SOUTH -> euler(180, 180);
                case EAST -> euler(180, -90);
                default -> euler(180, 90);
            };
        }
    }

    // Handles NaNs, unlike MathHelper
    public static float clamp(float value, float min, float max)
    {
        if (Float.isNaN(value))
            return min; // We can assume that 0 is safe in this context.

        return value < min ? min : Math.min(value, max);
    }

    private static Quaternionf euler(float xDeg, float yDeg)
    {
//        return new Quaternionf().rotateXYZ(xDeg * MathHelper.RADIANS_PER_DEGREE, yDeg * MathHelper.RADIANS_PER_DEGREE, 0);
        return new Quaternionf().rotateZYX(0, yDeg * MathHelper.RADIANS_PER_DEGREE, xDeg * MathHelper.RADIANS_PER_DEGREE);
    }
}
