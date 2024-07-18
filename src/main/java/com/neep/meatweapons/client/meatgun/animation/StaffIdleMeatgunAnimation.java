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
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            apply(matrices, 0, 0, 0, leftHand);
            return true;
        }
    };

    public static void apply(MatrixStack matrices, float xRot, float yRot, float zRot, boolean leftHand)
    {
        float i = leftHand ? -1 : 1;
        float d = leftHand ? 16 / 16f : 0;
        matrices.translate(0, -4 / 16f, 0 / 16f);
        matrices.translate(d, 0, 1.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (5 + yRot)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(xRot));
        matrices.translate(-d, 0, -1.5);

        matrices.translate(d, 0, d);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * (-20 + zRot)));
        matrices.translate(-d, 0, -d);
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
