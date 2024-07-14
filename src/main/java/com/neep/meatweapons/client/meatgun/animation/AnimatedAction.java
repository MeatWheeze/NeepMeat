package com.neep.meatweapons.client.meatgun.animation;

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
    }

    public void onRender(float tickDelta)
    {
        if (sequence != null && !finished)
            sequence.tick(clazz.cast(this), counter, tickDelta);
    }

    public void setSequence(Sequence<T> sequence)
    {
        // Reset the tick counter and replace the current sequence
        this.sequence = sequence;
        counter = 0;
    }

    public void markFinished()
    {
        this.finished = true;
    }

    @Override
    public boolean finished()
    {
        return true;
    }
}
