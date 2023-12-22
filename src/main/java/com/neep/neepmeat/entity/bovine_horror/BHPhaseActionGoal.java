package com.neep.neepmeat.entity.bovine_horror;

import com.neep.meatweapons.particle.MWGraphicsEffects;
import com.neep.neepmeat.entity.goal.AnimatedGoal;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMSounds;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class BHPhaseActionGoal extends AnimatedGoal<BovineHorrorEntity, BHPhaseActionGoal>
{
    protected final BovineHorrorEntity mob;

    protected final Sequence<BHPhaseActionGoal> orient = (action, counter) ->
    {
        var mob = action.mob;
        mob.setVisibility(1);
        mob.setInvulnerable(true);
        if (counter > 20)
        {
            getMob().setVisibility(1);
            PlayerLookup.tracking(getMob()).forEach(p ->
            {
                MWGraphicsEffects.syncBeamEffect(p, MWGraphicsEffects.BEAM, getMob().getWorld(),
                        getMob().getPos().add(0, 40, 0), getMob().getPos(), Vec3d.ZERO, 1.8f, 60);
            });
            mob.world.playSound(null, mob.getX(), mob.getY(), mob.getZ(), NMSounds.BH_PHASE2, SoundCategory.PLAYERS, 5, 0.7f);
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

                serverWorld.createExplosion(action.mob, DamageSource.mob(action.mob), new Behaviour(),
                        mobPos.x, mobPos.y, mobPos.z, 3, false, Explosion.DestructionType.NONE);
            }
            markFinished();
            getMob().setInvulnerable(false);
            getMob().updateGoals = true;
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

    private static class Behaviour extends ExplosionBehavior
    {
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power)
        {
            return false;
        }
    }
}
