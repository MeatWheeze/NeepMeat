package com.neep.neepmeat.entity.bovine_horror;

import com.neep.meatlib.maths.Vec2d;
import com.neep.neepmeat.init.NMEntities;
import com.neep.neepmeat.init.NMParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class AcidSprayEntity extends ProjectileEntity
{
    public AcidSprayEntity(EntityType<? extends ProjectileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public void target(Vec3d origin, Vec3d targetPos, float hSpeed, float divergence)
    {

        double dx = targetPos.x - origin.x;
        double dz = targetPos.z - origin.z;
        double dy = targetPos.y - origin.y;

        double distance = Math.sqrt(dx * dx + dz * dz);

        double time = distance / hSpeed;

        // s = ut + at^2/2
        double uy = (dy - (-getGravity() * time * time) / 2) / time;

        // Normalise and multiply by hSpeed
        double ux = dx / distance * hSpeed;
        double uz = dz / distance * hSpeed;

        setVelocity(ux, uy, uz);
        setPosition(origin.getX(), origin.getY(), origin.getZ());
        this.setYaw((float) (MathHelper.atan2(ux, uz) * 57.2957763671875));
        this.setPitch((float) (MathHelper.atan2(uy, hSpeed) * 57.2957763671875));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    @Override
    protected void initDataTracker()
    {

    }

    @Override
    public void tick()
    {
        super.tick();

        super.tick();
        HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
        boolean bl = false;
        if (hitResult.getType() != HitResult.Type.MISS && !bl)
        {
            this.onCollision(hitResult);
        }

        checkBlockCollision();
        Vec3d vec3d = this.getVelocity();
        double d = this.getX() + vec3d.x;
        double e = this.getY() + vec3d.y;
        double f = this.getZ() + vec3d.z;
        this.updateRotation();

//        float multiplier;
//        if (this.isTouchingWater())
//        {
//            multiplier = 0.8f;
//        }
//        else
//        {
//            multiplier = 0.99f;
//        }
//        this.setVelocity(vec3d.multiply(multiplier));

        if (!this.hasNoGravity())
        {
            Vec3d vec3d2 = this.getVelocity();
            this.setVelocity(vec3d2.x, vec3d2.y - this.getGravity(), vec3d2.z);
        }
        this.setPosition(d, e, f);

        if (world.isClient())
        {
            int count = 10;

            for (int i = 0; i < count; ++i)
            {
                double px = getPos().x + (Math.random() - 0.5) * 2;
                double py = getEyeY() + (Math.random() - 0.5) * 2;
                double pz = getPos().z + (Math.random() - 0.5) * 2;
                Vec3d vel = getVelocity();
                world.addParticle(NMParticles.BODY_COMPOUND_SHOWER, px, py, pz, vel.x, vel.y, vel.z);
            }
        }
    }

    @Override
    public boolean hasNoGravity()
    {
        return false;
    }

    protected void onCollision(HitResult hitResult)
    {
        HitResult.Type type = hitResult.getType();
//        if (type == HitResult.Type.ENTITY)
//        {
//            this.onEntityHit((EntityHitResult)hitResult);
//            this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, hitResult.getPos(), GameEvent.Emitter.of(this, null));
//        }
        if (type == HitResult.Type.BLOCK)
        {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            this.onBlockHit(blockHitResult);
            BlockPos blockPos = blockHitResult.getBlockPos();
            this.world.emitGameEvent(GameEvent.PROJECTILE_LAND, blockPos, GameEvent.Emitter.of(this, this.world.getBlockState(blockPos)));
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
       onHit();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        onHit();
    }

    protected float getGravity()
    {
        return 0.03f;
    }

    protected void onHit()
    {
        world.getOtherEntities(this, getBoundingBox().expand(2), e -> e instanceof LivingEntity
                && !e.getType().equals(NMEntities.BOVINE_HORROR)).forEach(e ->
        {
            float dist = e.distanceTo(this);
            float damage =  6;
            if (dist > 1.5)
                damage = 4;
            else if (dist > 2.5)
                damage = 2;

            e.damage(DamageSource.mob((LivingEntity) getOwner()), damage);
        });

        if (world.isClient())
        {
            var effect = NMParticles.BODY_COMPOUND_SHOWER;
            for (int i = 0; i < 100; ++i)
            {
                double r = random.nextFloat() * 2;
                double yaw = (random.nextFloat() + 0.5) * 2 * Math.PI;
                double px = getX() + r * Math.sin(yaw);
                double pz = getZ() + r * Math.cos(yaw);
                double py = getY();

                double vx = (px - getX()) * 0.2;
                double vy = r * r * 0.1;
                double vz = (pz - getZ()) * 0.2;

               world.addParticle(effect, px, py, pz, vx, vy, vz);
            }
        }

        discard();
    }
}
