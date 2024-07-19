package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class StaffReloadMeatgunAnimation extends AnimatedAction<MeatgunComponent, StaffReloadMeatgunAnimation> implements MeatgunAnimation
{
    private int length = 10;

    private final Sequence<StaffReloadMeatgunAnimation> down = new Sequence<>()
    {
        @Override
        public void tick(StaffReloadMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
            if (counter > 5)
            {
                int waitTime = length - 10;

                if (waitTime != 0)
                    setSequence(wait);
                else
                    setSequence(up);
            }
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            float delta = (counter + tickDelta) / 5;
            matrices.translate(0, -2 * delta, 0);

            StaffIdleMeatgunAnimation.apply(matrices, 0, 0, 0, leftHand);
            return true;
        }
    };

    private final Sequence<StaffReloadMeatgunAnimation> wait = new Sequence<>()
    {
        @Override
        public void tick(StaffReloadMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
            int waitTime = length - 10;
            if (counter >= waitTime)
                setSequence(up);

        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            matrices.translate(0, -2, 0);
            StaffIdleMeatgunAnimation.apply(matrices, 0, 0, 0, leftHand);
            return true;
        }
    };

    private final Sequence<StaffReloadMeatgunAnimation> up = new Sequence<>()
    {
        @Override
        public void tick(StaffReloadMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
            if (counter >= 5)
                markFinished();
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            float delta = 1 - (counter + tickDelta) / 6;
            matrices.translate(0, -2 * delta, 0);
            StaffIdleMeatgunAnimation.apply(matrices, 0, 0, 0, leftHand);
            return true;
        }
    };

    @Override
    public void start(@Nullable PacketByteBuf buf)
    {
        super.start(buf);

        if (buf != null)
            length = buf.readVarInt();
        else
            length = 10;

        setSequence(down);
    }

    @Override
    public boolean canStop(MeatgunAnimation replace)
    {
        return true;
    }
}
