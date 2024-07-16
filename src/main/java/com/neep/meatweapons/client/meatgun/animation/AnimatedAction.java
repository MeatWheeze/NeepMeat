package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public abstract class AnimatedAction<E, T extends RenderAction<E>> implements RenderAction<E>, MeatgunAnimation
{
    protected final Class<T> clazz;
    protected boolean finished;
    protected Sequence<T> sequence;
    protected int counter;

    public AnimatedAction()
    {
        this.clazz = (Class<T>) getClass();
    }

    public void start(@Nullable PacketByteBuf buf)
    {
        finished = false;
    }

    @Override
    public void tick(MeatgunComponent component)
    {
        ++counter;

        if (sequence != null && !finished)
            sequence.tick(clazz.cast(this), component, counter);
    }

    public boolean applyRender(MatrixStack matrices, float tickDelta)
    {
        if (sequence != null)
            return sequence.applyRender(matrices, counter, tickDelta);

        return true;
    }

    public void setSequence(Sequence<T> sequence)
    {
        // Reset the tick counter and replace the current sequence
        this.sequence = sequence;
        counter = 0; // Jank
    }

    public void markFinished()
    {
        this.finished = true;
    }

    @Override
    public boolean finished()
    {
        return finished;
    }
}
