package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PistolReloadMeatgunAnimation extends AnimatedAction<MeatgunComponent, PistolReloadMeatgunAnimation> implements MeatgunAnimation
{
    private final Sequence<PistolReloadMeatgunAnimation> downn = new Sequence<>()
    {
        @Override
        public void tick(PistolReloadMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
            if (counter > 5)
                setSequence(up);
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            float delta = (counter + tickDelta) / 5;
            matrices.translate(0, -2 * delta, 0);

            return true;
        }
    };

    private final Sequence<PistolReloadMeatgunAnimation> up = new Sequence<>()
    {
        @Override
        public void tick(PistolReloadMeatgunAnimation parent, MeatgunComponent component, int counter)
        {
            if (counter >= 5)
                markFinished();
        }

        @Override
        public boolean applyRender(MatrixStack matrices, int counter, float tickDelta, boolean leftHand)
        {
            float delta = 1 - (counter + tickDelta) / 6;
            matrices.translate(0, -2 * delta, 0);
            return true;
        }
    };

    @Override
    public void start(@Nullable PacketByteBuf buf)
    {
        super.start(buf);
        setSequence(downn);
    }

    @Override
    public boolean canStop(MeatgunAnimation replace)
    {
        return true;
    }
}
