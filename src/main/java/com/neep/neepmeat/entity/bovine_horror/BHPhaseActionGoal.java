package com.neep.neepmeat.entity.bovine_horror;

import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.entity.goal.AnimatedGoal;
import com.neep.neepmeat.init.NMParticles;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class BHPhaseActionGoal extends AnimatedGoal<BovineHorrorEntity, BHPhaseActionGoal>
{
    protected final BovineHorrorEntity mob;

    protected final Sequence<BHPhaseActionGoal> orient = (action, counter) ->
    {
        getMob().setVisibility(1);
        getMob().setInvulnerable(true);
        if (counter > 20)
        {
            getMob().setVisibility(1);
            PlayerLookup.tracking(getMob()).forEach(p ->
            {
                MWGraphicsEffects.syncBeamEffect(p, MWGraphicsEffects.BEAM, getMob().getWorld(),
                        getMob().getPos().add(0, 40, 0), getMob().getPos(), Vec3d.ZERO, 1.8f, 60);
            });
            setSequence(action.particles);
        }
    };

    protected final Sequence<BHPhaseActionGoal> particles = (action, counter) ->
    {
        getMob().setVisibility(1);
        if (counter > 60)
        {
            if (getMob().getWorld() instanceof ServerWorld serverWorld)
            {
                Vec3d mobPos = getMob().getPos();
                serverWorld.spawnParticles(NMParticles.MEAT_BIT, mobPos.x, mobPos.y + 2, mobPos.z, 50, 3, 3, 3, 0.1);
            }
            markFinished();
            getMob().clearGoalsAndTasks();
            getMob().setInvulnerable(false);
            getMob().updateGoals();
        }
    };

    public BHPhaseActionGoal(BovineHorrorEntity mob)
    {
        this.mob = mob;
    }

    @Override
    public void start()
    {
        super.start();
        setSequence(orient);
    }

    @Override
    public boolean canStart()
    {
        return !finished;
    }

    protected BovineHorrorEntity getMob()
    {
        return mob;
    }
}
