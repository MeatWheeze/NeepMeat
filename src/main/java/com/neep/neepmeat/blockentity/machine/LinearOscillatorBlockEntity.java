package com.neep.neepmeat.blockentity.machine;

import com.neep.meatlib.block.BaseFacingBlock;
import com.neep.neepmeat.init.NMBlockEntities;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class LinearOscillatorBlockEntity extends BlockEntity implements BlockEntityClientSerializable
{
    public static final String NBT_COOLDOWN = "cooldown";
    public static final String NBT_MAX_COOLDOWN = "max_cooldown";

    public int maxCooldown = 40;
    public int cooldown = 0;
    public float prevExtension = 0f;
    public float extension = 0f;
    public boolean extended = false;

    // Rendering only
    public long extensionTime = 0;
    public float clientExtension = 0;

    public LinearOscillatorBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.LINEAR_OSCILLATOR, pos, state);
    }

    public LinearOscillatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    public static <E extends BlockEntity> void serverTick(World world, BlockPos pos, BlockState state, LinearOscillatorBlockEntity be)
    {
        be.tick();
    }

    public void tick()
    {
        cooldown = Math.max(0, cooldown - 1);
        maxCooldown = 40;

        if (cooldown <= 0)
        {
            extend();
        }
        else
        {
            extended = false;
        }

        prevExtension = extension;
        extension = cooldown / (float) maxCooldown;
        sync();
    }

    public void extend()
    {
        this.cooldown = this.maxCooldown;
        this.extended = true;

        Direction facing = getCachedState().get(BaseFacingBlock.FACING);
        BlockPos facingPos = getPos().offset(facing);
        getWorld().breakBlock(facingPos, true);

        Box box = Box.from(BlockBox.create(facingPos, facingPos));
        List<LivingEntity> entities = world.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class), box, entity -> true);
        entities.forEach(entity ->
        {
            entity.damage(DamageSource.GENERIC, 2);
            double mult = 0.5;
            entity.addVelocity(facing.getOffsetX() * mult, facing.getOffsetY() * mult, facing.getOffsetZ() * mult);
        });
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        this.maxCooldown = nbt.getInt(NBT_MAX_COOLDOWN);
        this.cooldown = nbt.getInt(NBT_COOLDOWN);
    }

    public NbtCompound writeNbt(NbtCompound nbt)
    {
        nbt.putInt(NBT_MAX_COOLDOWN, maxCooldown);
        nbt.putInt(NBT_COOLDOWN, cooldown);
        return nbt;
    }

    @Override
    public void fromClientTag(NbtCompound nbt)
    {
        this.maxCooldown = nbt.getInt(NBT_MAX_COOLDOWN);
        this.cooldown = nbt.getInt(NBT_COOLDOWN);
        this.prevExtension = nbt.getFloat("prev_extension");
        this.extension= nbt.getFloat("extension");

        if (this.cooldown == this.maxCooldown)
        {
            this.extensionTime = nbt.getLong("world_time");
        }
    }

    @Override
    public NbtCompound toClientTag(NbtCompound nbt)
    {
        nbt.putInt(NBT_MAX_COOLDOWN, maxCooldown);
        nbt.putInt(NBT_COOLDOWN, cooldown);
        nbt.putLong("world_time", getWorld().getTime());
        nbt.putFloat("prev_extension", prevExtension);
        nbt.putFloat("extension", extension);
        return nbt;
    }
}
