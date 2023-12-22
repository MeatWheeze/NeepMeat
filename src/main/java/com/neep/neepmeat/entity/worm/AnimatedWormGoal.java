package com.neep.neepmeat.entity.worm;

import net.minecraft.entity.ai.goal.Goal;

public abstract class AnimatedWormGoal<T extends WormAction> extends Goal implements WormAction
{
    protected final Class<T> clazz;
    protected boolean finished;
    protected Sequence<T> sequence;
    protected int counter;

    public AnimatedWormGoal()
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
}