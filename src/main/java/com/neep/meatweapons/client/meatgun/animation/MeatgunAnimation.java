package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface MeatgunAnimation
{
    void start(@Nullable PacketByteBuf buf);

    boolean canStop(MeatgunAnimation replace);

    boolean finished();

    void tick(MeatgunComponent component);

    boolean applyRender(MatrixStack matrices, float tickDelta, boolean leftHand);

    MeatgunAnimation EMPTY = new MeatgunAnimation()
    {
        @Override
        public void start(@Nullable PacketByteBuf buf) { }

        @Override
        public boolean canStop(MeatgunAnimation replace)
        {
            return true;
        }

        @Override
        public boolean finished()
        {
            return false;
        }

        @Override
        public void tick(MeatgunComponent component)
        {

        }

        @Override
        public boolean applyRender(MatrixStack matrices, float tickDelta, boolean leftHand)
        {
            return true;
        }
    };

}
