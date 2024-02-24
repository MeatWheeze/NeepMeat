package com.neep.neepmeat.api.plc.robot;

import com.neep.neepmeat.api.plc.PLC;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class SoundAction implements AtomicAction
{
    private final SoundEvent soundEvent;
    private Supplier<World> world;

    public SoundAction(Supplier<World> world, SoundEvent soundEvent)
    {
        this.world = world;
        this.soundEvent = soundEvent;
    }

    @Override
    public void start(PLC plc)
    {
        var robot = plc.getActuator();
        world.get().playSound(null, robot.getX(), robot.getY(), robot.getZ(), soundEvent, SoundCategory.BLOCKS, 1, 1, 1);
    }
}
