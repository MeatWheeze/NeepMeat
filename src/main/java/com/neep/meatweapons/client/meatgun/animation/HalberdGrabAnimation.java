package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.neepmeat.util.NMMaths;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class HalberdGrabAnimation extends AnimatedAction<MeatgunComponent, HalberdGrabAnimation> implements MeatgunAnimation
{
    private Vec3d offset = Vec3d.ZERO;

    private final Sequence<HalberdGrabAnimation> up = new Sequence<>()
    {
        @Override
        public void tick(HalberdGrabAnimation parent, MeatgunComponent component, int counter)
        {
            if (counter > 20)
                markFinished();
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            float playerYawDeg = MathHelper.wrapDegrees(MinecraftClient.getInstance().player.getYaw(tickDelta));
            float targetYawDeg = NMMaths.rectToPol(offset).y;

            matrices.translate(0 / 16f, -16 / 16f, 0);

            float angle = MathHelper.wrapDegrees((float) (playerYawDeg - targetYawDeg));

            float zOff = 4 / 16f;
            float yOff = 6 / 16f;
            matrices.translate(-10 / 16f, -10 / 16f, 0);
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angle));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            matrices.translate(0 / 16f, 16 / 16f, 0);
            return false;
        }
    };

    @Override
    public void start(@Nullable PacketByteBuf buf)
    {
        super.start(buf);

        if (buf != null)
            offset = PacketBufUtil.readVec3d(buf);
        else
            offset = Vec3d.ZERO;

        setSequence(up);
    }

    @Override
    public boolean canStop(MeatgunAnimation replace)
    {
        return false;
    }
}
