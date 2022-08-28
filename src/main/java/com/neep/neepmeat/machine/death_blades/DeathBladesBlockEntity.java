package com.neep.neepmeat.machine.death_blades;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class DeathBladesBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    protected float angularSpeed;
    protected float multiplier;
    protected float angle;
    protected float clientAngle;

    public DeathBladesBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public DeathBladesBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.DEATH_BLADES, pos, state);
    }

    @Override
    public boolean tick(IMotorBlockEntity motor)
    {
        this.angularSpeed = multiplier * 20;
        this.angle += angularSpeed;
        sync();

        Vec3d centre = Vec3d.ofCenter(pos);
        Vec3d bladeEnd = new Vec3d(Math.cos(angle * Math.PI / 180) * 1.5, 0, Math.sin(angle * Math.PI / 180) * 1.5);
        Vec3d startPos = centre.subtract(bladeEnd);
        Vec3d endPos = centre.add(bladeEnd);

        bladeEnd.rotateY(angle);

        Box box = new Box(pos.add(-1, 0, -1), pos.add(2, 1, 2));

        world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, e -> true).stream()
                .filter(entity ->
                {
                    Optional<Vec3d> optional = entity.getBoundingBox().raycast(centre.subtract(bladeEnd), centre.add(bladeEnd));
                    return optional.isPresent();
                }).filter(e -> e.canTakeDamage()).findFirst().ifPresent(e -> e.damage(DamageSource.GENERIC, 1));

        ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, startPos.getX(), startPos.getY(), startPos.getZ(), 2, 0.01, 0, 0, 0);
        ((ServerWorld) world).spawnParticles(ParticleTypes.SMOKE, endPos.getX(), endPos.getY(), endPos.getZ(), 2, 0.01, 0, 0, 0);

        return true;
    }

    @Override
    public void setWorkMultiplier(float multiplier)
    {
        this.multiplier = multiplier;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putFloat("angle", angle);
        nbt.putFloat("angularSpeed", angularSpeed);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.angle = nbt.getFloat("angle");
        this.angularSpeed = nbt.getFloat("angularSpeed");
    }
}