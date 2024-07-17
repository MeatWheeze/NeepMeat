package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class StaffIdleMeatgunAnimation extends AnimatedAction<MeatgunComponent, StaffIdleMeatgunAnimation> implements MeatgunAnimation
{
    private final Sequence<StaffIdleMeatgunAnimation> idle = new Sequence<>()
    {
        @Override
        public void tick(StaffIdleMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta)
        {
            apply(matrices, 0, 0, 0);
            return true;
        }
    };

    public static void apply(MatrixStack matrices, float xRot, float yRot, float zRot)
    {
        matrices.translate(0, -4 / 16f, 0 / 16f);
        matrices.translate(0, 0, 1.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5 + yRot));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xRot));
        matrices.translate(0, 0, -1.5);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20 + zRot));
    }

    @Override
    public void start(@Nullable PacketByteBuf buf)
    {
        super.start(buf);
        setSequence(idle);
    }

    @Override
    public boolean canStop(MeatgunAnimation replace)
    {
        return true;
    }
}
