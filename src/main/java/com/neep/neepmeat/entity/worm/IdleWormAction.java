package com.neep.neepmeat.entity.worm;

import com.neep.neepmeat.NeepMeat;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class IdleWormAction implements WormAction
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "idle");

    protected final WormEntity entity;
    protected int maxTicks;
    protected int age;
    protected boolean finished;

    public IdleWormAction(WormEntity entity)
    {
        this.entity = entity;
        this.maxTicks = entity.getRandom().nextInt(100);
    }

    @Override
    public Identifier getId()
    {
        return ID;
    }

    @Override
    public void tick()
    {
        ++age;
        if (age >= maxTicks) this.finished = true;
    }

    @Override
    public boolean isFinished()
    {
        return finished;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt("maxTicks", maxTicks);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.maxTicks = nbt.getInt("maxTicks");
    }
}
