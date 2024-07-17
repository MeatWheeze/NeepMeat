package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.component.MeatgunComponentImpl;
import com.neep.meatweapons.meatgun.module.HalberdModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.meatgun.module.MeatgunModules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class HalberdChargeAnimation extends AnimatedAction<MeatgunComponent, HalberdChargeAnimation> implements MeatgunAnimation
{
    private boolean started = false;

    private final Sequence<HalberdChargeAnimation> up = new Sequence<>()
    {
        @Override
        public void tick(HalberdChargeAnimation parent, MeatgunComponent component, int counter)
        {
            if (!MinecraftClient.getInstance().player.isSprinting())
                markFinished();

            @Nullable MeatgunModule module = MeatgunComponentImpl.findRecursive(component.getRoot(), MeatgunModules.HALBERD);
            if (module instanceof HalberdModule halberd && halberd.triggerHeld())
            {
                started = true;
            }
            else if (started)
            {
                markFinished();
            }
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta)
        {
            // Let's use trial and error to find the shoulder!
            matrices.translate(9 / 16f, -2 / 16f, 1.5);
//            float r = MathHelper.sin(AnimationTickHolder.getRenderTime() / 5);
//            Vector3f origin = matrices.peek().getPositionMatrix().getTranslation(new Vector3f());
//            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(AnimationTickHolder.getRenderTime() * 2));
            float delta = Math.min(1, (counter + tickDelta) / 10);

            float zOff = delta * 4 / 16f;
            float yOff = delta * 6 / 16f;
            matrices.translate(0, -4 / 16f + yOff, zOff);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
//            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            matrices.translate(-9 / 16f, 2 / 16f, -1.5);
            return true;
        }
    };

    @Override
    public void start(@Nullable PacketByteBuf buf)
    {
        super.start(buf);
        started = false;
        setSequence(up);
    }

    @Override
    public boolean canStop(MeatgunAnimation replace)
    {
        return false;
    }
}
