package com.neep.neepmeat.machine.large_crusher;

import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.block.entity.MotorisedMachineBlockEntity;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LargeCrusherBlockEntity extends MotorisedMachineBlockEntity implements MotorisedBlock
{
    private final LargeCrusherStorage storage = new LargeCrusherStorage(this);
    private final Random jrandom = new Random();

    public LargeCrusherBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 0.2f, 0.1f, 2);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        storage.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        storage.readNbt(nbt);
    }

    public void serverTick(ServerWorld world)
    {
        if (world.getTime() % 4 == 0)
        {
            Direction facing = getCachedState().get(LargeCrusherBlock.FACING);
            Direction clockwise = facing.rotateYClockwise();
            Direction aclockwise = facing.rotateYCounterclockwise();
            Box box = new Box(pos.up(2))
                    .stretch(facing.getOffsetX(), facing.getOffsetY(), facing.getOffsetZ())
                    .stretch(clockwise.getOffsetX(), clockwise.getOffsetY(), clockwise.getOffsetZ())
                    .stretch(aclockwise.getOffsetX(), aclockwise.getOffsetY(), aclockwise.getOffsetZ());

            List<Entity> entities = world.getEntitiesByClass(Entity.class, box, e -> true);
            entities.stream().filter(e -> e instanceof ItemEntity).map(ItemEntity.class::cast).findFirst().ifPresent(item ->
            {
                try (Transaction transaction = Transaction.openOuter())
                {
                    ItemVariant variant = ItemVariant.of(item.getStack());
                    long inserted = storage.inputStorage.insert(variant, item.getStack().getCount(), transaction);
                    item.getStack().decrement((int) inserted);
                    transaction.commit();
                }
            });

            if (world.getTime() % 8 == 0)
            {
                float damageAmount = power * 20;
                DamageSource damageSource = world.getDamageSources().generic();
                List<ItemStack> resultStacks = new ArrayList<>();

                entities.stream().filter(e -> e instanceof LivingEntity).map(LivingEntity.class::cast).forEach(entity ->
                {
                    if (entity.isAlive() && entity.canTakeDamage() && entity.hurtTime == 0)
                    {
                        if (entity.getHealth() <= damageAmount)
                        {
                            dropLoot(world, entity, damageSource, resultStacks);
                            entity.setDropsLoot(false);
                            entity.kill();
                        }
                        entity.damage(damageSource, 4);
                    }
                });

                if (!resultStacks.isEmpty())
                {
                    try (Transaction transaction = Transaction.openOuter())
                    {
                        // Destroy items that don't fit in the output. We don't care.
                        resultStacks.forEach(stack -> storage.outputStorage.insert(ItemVariant.of(stack), stack.getCount(), transaction));
                        transaction.commit();
                    }
                }
            }
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            for (var view : storage.outputStorage)
            {
                if (!view.isResourceBlank())
                {
                    int extracted = ItemPipeUtil.stackToAny(world, pos, Direction.DOWN, view.getResource(), view.getAmount(), transaction);
                    view.extract(view.getResource(), extracted, transaction);
                }
            }
            transaction.commit();
        }

        try (Transaction transaction = Transaction.openOuter())
        {
            int occupied = 0;
            for (var slot : storage.slots)
            {
                if (slot.getRecipe() != null)
                    occupied++;
            }

            // Divide the progress increment evenly across occupied slots.
            float progressIncrement = progressIncrement() * 4 / occupied;
            for (var slot : storage.slots)
            {
                slot.tick(progressIncrement, transaction);
            }
            transaction.commit();
        }
    }

    private void dropLoot(ServerWorld world, LivingEntity entity, DamageSource damageSource, List<ItemStack> resultStacks)
    {
        Identifier identifier = entity.getLootTable();
        LootTable lootTable = world.getServer().getLootManager().getLootTable(identifier);
        LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder(world))
                .add(LootContextParameters.THIS_ENTITY, entity)
                .add(LootContextParameters.ORIGIN, entity.getPos())
                .add(LootContextParameters.DAMAGE_SOURCE, damageSource)
                .addOptional(LootContextParameters.KILLER_ENTITY, damageSource.getAttacker())
                .addOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damageSource.getSource());

//        if (causedByPlayer && this.attackingPlayer != null)
//        {
//            builder = builder.add(LootContextParameters.LAST_DAMAGE_PLAYER, this.attackingPlayer).luck(this.attackingPlayer.getLuck());
//        }

        LootContextParameterSet lootContextParameterSet = builder.build(LootContextTypes.ENTITY);
        lootTable.generateLoot(lootContextParameterSet, entity.getLootTableSeed(), resultStacks::add);
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
        return false;
    }

    public Storage<ItemVariant> getInputStorage(Direction unused)
    {
        return storage.inputStorage;
    }

    public List<LargeCrusherStorage.InputSlot> getSlots()
    {
        return storage.slots;
    }

    public void clientTick()
    {
        float intensity = progressIncrement / maxIncrement;
        Direction facing = getCachedState().get(LargeCrusherBlock.FACING);

        // Particles will be more frequent at higher power. Clamp above 1 to prevent / 0.
        int tickInterval = (int) MathHelper.clamp(1, 1 / (intensity * 2), 100);

        if ((world.getTime() % tickInterval) == 0 && progressIncrement() >= minIncrement())
        {
            // Find a random non-empty slot
            int[] indices = jrandom.ints(storage.slots.size(), 0, storage.slots.size()).toArray();
            for (int idx : indices)
            {
                LargeCrusherStorage.InputSlot slot = storage.slots.get(idx);
                if (slot.isEmpty() || slot.getRecipe() == null)
                    continue;

                double px = getPos().getX() + facing.getOffsetX() * 0.5 + 0.5 + (jrandom.nextFloat() - 0.5) * 1;
                double py = getPos().getY() + 2.5 + (jrandom.nextFloat() - 0.5) * 0.5;
                double pz = getPos().getZ() + facing.getOffsetZ() * 0.5 + 0.5 + (jrandom.nextFloat() - 0.5) * 1;

                double vx = (jrandom.nextFloat() - 0.5) * 0.2;
                double vy = jrandom.nextFloat() * Math.max(0.3, intensity);
                double vz = (jrandom.nextFloat() - 0.5) * 0.2;

                world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, slot.getAsStack()),
                        px, py, pz, vx, vy, vz);
            }
        }
    }
}
