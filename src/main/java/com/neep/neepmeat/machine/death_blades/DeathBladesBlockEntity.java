package com.neep.neepmeat.machine.death_blades;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.neepmeat.api.machine.IMotorisedBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.machine.synthesiser.MobSynthesisRegistry;
import com.neep.neepmeat.transport.block.fluid_transport.entity.FluidDrainBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DeathBladesBlockEntity extends SyncableBlockEntity implements IMotorisedBlock
{
    protected static final int MAX_COOLDOWN = 10;
    public static final float MIN_INCREMENT = 0.2f;
    public static final float MAX_INCREMENT = 1.5f;

    protected float angularSpeed;
    protected float multiplier;
    protected float angle;
    protected float clientAngle;
    protected float cooldown;
    protected float increment = 1;

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
        this.cooldown = Math.min(MAX_COOLDOWN, cooldown + increment);

        this.angle = MathHelper.wrapDegrees(this.angle + angularSpeed);

        if (cooldown >= MAX_COOLDOWN && angularSpeed > 0)
        {
            cooldown = 0;

//            Vec3d centre = Vec3d.ofCenter(pos);
//            Vec3d bladeEnd = new Vec3d(Math.cos(angle * Math.PI / 180) * 1.5, 0, Math.sin(angle * Math.PI / 180) * 1.5);
//            Vec3d startPos = centre.subtract(bladeEnd);
//            Vec3d endPos = centre.add(bladeEnd);

            Box box = new Box(0, 0, 0, 0, 0, 0);
            switch (getCachedState().get(DeathBladesBlock.FACING))
            {
                case UP, DOWN -> box = new Box(pos.add(-1, 0, -1), pos.add(2, 1, 2));
                case NORTH, SOUTH -> box = new Box(pos.add(-1, -1, 0), pos.add(2, 2, 1));
                case EAST, WEST -> box = new Box(pos.add(0, -1, -1), pos.add(1, 2, 2));
            }

            int damageAmount = (int) (4 * multiplier);
            world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, e -> true).stream()
                    .filter(e -> e.hurtTime == 0 && !e.isDead()).forEach(e ->
                    {
                        if (e.getHealth() > damageAmount)
                            e.damage(DamageSource.GENERIC, damageAmount);
                        else killEntity((ServerWorld) world, e);
                    });
        }
        return true;
    }

    public static void killEntity(ServerWorld world, LivingEntity entity)
    {
        if (world.getBlockEntity(entity.getBlockPos().down()) instanceof FluidDrainBlockEntity be)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                be.getBuffer(null).insert(FluidVariant.of(NMFluids.STILL_TISSUE_SLURRY), getEntityAmount(entity), transaction);
                world.spawnParticles(NMParticles.MEAT_SPLASH, entity.getX(), entity.getY(), entity.getZ(), 20, 0.4, 0.4, 0.4, 0.01);
                entity.setDropsLoot(false);
                transaction.commit();
            }
        }
        entity.kill();
    }

    public static long getEntityAmount(LivingEntity entity)
    {
        MobSynthesisRegistry.Entry entry = MobSynthesisRegistry.get(entity);
        if (entry != null)
        {
            return entry.meat() / 2;
        }
        return MobSynthesisRegistry.meatForEntity(entity) / 2;
    }

    @Override
    public void setInputPower(float power)
    {
        this.multiplier = power;
        this.angularSpeed = power * 20;
        sync();
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
//        nbt.putFloat("angle", angle);
        nbt.putFloat("angularSpeed", angularSpeed);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
//        this.angle = nbt.getFloat("angle");
        this.angularSpeed = nbt.getFloat("angularSpeed");
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, DeathBladesBlockEntity be)
    {
        be.angle = MathHelper.wrapDegrees(be.angle + be.angularSpeed);
    }
}