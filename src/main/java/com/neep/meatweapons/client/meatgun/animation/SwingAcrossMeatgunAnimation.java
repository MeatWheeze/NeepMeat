package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.neepmeat.util.Easing;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class SwingAcrossMeatgunAnimation extends AnimatedAction<MeatgunComponent, SwingAcrossMeatgunAnimation> implements MeatgunAnimation
{
    private final Sequence<SwingAcrossMeatgunAnimation> down = new Sequence<>()
    {
        @Override
        public void tick(SwingAcrossMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
            if (counter >= 10)
                markFinished();
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            float time = counter + tickDelta;
            float delta = time / 10;
            float ease = (float) Easing.easeInOutBack(delta);
            float rx = -40 * ease;
            float ry = 120 * ease;

            StaffIdleMeatgunAnimation.apply(matrices, rx, ry, 0, leftHand);
            return false;
        }
    };

//    private final Sequence<SwingAcrossMeatgunAnimation> up = new Sequence<>()
//    {
//        @Override
//        public void tick(SwingAcrossMeatgunAnimation parent, MeatgunComponent component, int counter)
//        {
//            if (counter >= 10)
//                markFinished();
//        }
//
//        @Override
//        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta)
//        {
//            float time = counter + tickDelta;
//            float delta = time / 11;
//            float ease = (float) (1 - Easing.easeInBack(delta));
//            float rx = -40 * ease;
//            float ry = 50 * ease;
//            StaffIdleMeatgunAnimation.apply(matrices, rx, ry, 0);
//            return true;
//        }
//    };

    @Override
    public void start(@Nullable PacketByteBuf buf)
    {
        super.start(buf);
        setSequence(down);
    }

    @Override
    public boolean canStop(MeatgunAnimation replace)
    {
        return replace == this;
    }
}
