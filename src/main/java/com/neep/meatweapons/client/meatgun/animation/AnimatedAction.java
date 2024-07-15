package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import net.minecraft.client.util.math.MatrixStack;

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

    public void start()
    {
        finished = false;
    }

    @Override
    public void tick()
    {
        ++counter;

        if (sequence != null && !finished)
            sequence.tick(clazz.cast(this), counter);
    }

    public void applyRender(MatrixStack matrices, float tickDelta)
    {
        if (sequence != null)
            sequence.applyRender(matrices, counter, tickDelta);
        else
            System.out.println("else");
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
