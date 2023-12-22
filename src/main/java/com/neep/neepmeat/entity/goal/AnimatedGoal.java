package com.neep.neepmeat.entity.goal;

import net.minecraft.entity.ai.goal.Goal;

public abstract class AnimatedGoal<E, T extends Action<E>> extends Goal implements Action<E>
{
    protected final Class<T> clazz;
    protected boolean finished;
    protected Sequence<E, T> sequence;
    protected int counter;

    public AnimatedGoal()
    {
        this.clazz = (Class<T>) getClass();
    }

    @Override
    public void start()
    {
        finished = false;
    }

    @Override
    public void tick()
    {
        if (sequence != null) sequence.tick(clazz.cast(this), counter);

        ++counter;
    }

    @Override
    public boolean shouldRunEveryTick()
    {
        return true;
    }

    @Override
    public boolean shouldContinue()
    {
        return !finished;
    }

    public void setSequence(Sequence<E, T> sequence)
    {
        // Reset the tick counter and replace the current sequence
        this.sequence = sequence;
        counter = 0;
    }

    public void markFinished()
    {
        this.finished = true;
    }
}