package com.neep.meatweapons.entity;

import com.neep.meatweapons.MWItems;
import com.neep.meatweapons.MeatWeapons;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.joml.Vector3d;

public class BounceGrenadeEntity extends MeatgunProjectileEntity
{
    public static final ExplosionBehavior DESTROY_BEHAVIOUR = new ExplosionBehavior();
    public static final ExplosionBehavior KEEP_BEHAVIOUR = new ExplosionBehavior()
    {
        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power)
        {
            return false;
        }
    };

    protected float explosionPower = 1;
    protected boolean destructive;
    protected int fuse = 100;

    public BounceGrenadeEntity(EntityType<BounceGrenadeEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public BounceGrenadeEntity(World world, float explosionPower, int fuse, boolean destructive)
    {
        super(MeatWeapons.BOUNCE_GRENADE, world);
        this.setNoGravity(false);
        this.fuse = fuse;
        this.explosionPower = explosionPower;
        this.destructive = destructive;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket()
    {
        return super.createSpawnPacket();
    }

    @Override
    public void tick()
    {
        if (collisions > 4)
        {
            inGround = true;
        }

        super.tick();
        if (this.getWorld().isClient)
        {
            this.spawnParticles(1);
        }
        else if (this.age > fuse)
        {
            explode();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult)
    {
//        HitResult.Type type = hitResult.getType();
//        if (type == HitResult.Type.ENTITY)
//        {
//            this.onEntityHit((EntityHitResult) hitResult);
//            this.getWorld().emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, (BlockState) null));
//        }
//        else if (type == HitResult.Type.BLOCK)
//        {
//            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
//            this.onBlockHit(blockHitResult);
//            BlockPos blockPos = blockHitResult.getBlockPos();
//
//            Vec3d vel = getVelocity();
//            Vector3d normal = new Vector3d(blockHitResult.getSide().getUnitVector())
//                    .mul(vel.x, vel.y, vel.z)
//                    .mul(-2);
//
//            setVelocity(vel.x + normal.x, vel.y + normal.y, vel.z + normal.z);
//        }
//
//        if (!getWorld().isClient)
//        {
//            if (hitResult.getType() == HitResult.Type.BLOCK)
//            {
//            }
//        }
        super.onCollision(hitResult);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);

        Vec3d vel = getVelocity();
        Vector3d normal = new Vector3d(blockHitResult.getSide().getUnitVector().absolute())
                .mul(vel.x, vel.y, vel.z)
                .mul(-1.5);

        setVelocity(vel.x + normal.x, vel.y + normal.y, vel.z + normal.z);
    }

    @Override
    public boolean shouldRender(double distance)
    {
        return distance <= 200;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        if (!getWorld().isClient)
        {
            explode();
        }
    }

    @Override
    protected void onBlockCollision(BlockState state)
    {
        super.onBlockCollision(state);
    }

    @Override
    protected SoundEvent getHitSound()
    {
        return SoundEvents.BLOCK_STONE_HIT;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat("ExplosionPower", this.explosionPower);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        this.explosionPower = nbt.getFloat("ExplosionPower");
    }

    protected void explode()
    {
        boolean mobGriefing = getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && destructive;

        this.getWorld().createExplosion(null, null, mobGriefing ? DESTROY_BEHAVIOUR : KEEP_BEHAVIOUR,
                this.getX(), this.getY(), this.getZ(), this.explosionPower, mobGriefing, World.ExplosionSourceType.BLOCK);

        remove(RemovalReason.DISCARDED);
        this.getWorld().sendEntityStatus(this, (byte) 0);
    }

    private void spawnParticles(int amount)
    {
//        Random rand = new Random((long) getX());
//        System.out.println(distanceTraveled);
        double x = 0.05;
        for (int i = 0; i < amount; ++i)
            this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY(), this.getZ(), x * random.nextGaussian(), x * random.nextGaussian(), x * random.nextGaussian());
    }

    protected void onHit(LivingEntity target)
    {
        super.onHit(target);
    }

    protected ItemStack asItemStack()
    {
        return new ItemStack(MWItems.BALLISTIC_CARTRIDGE);
    }
}
