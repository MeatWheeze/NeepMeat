package com.neep.neepmeat.machine.pedestal;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.meatweapons.init.GraphicsEffects;
import com.neep.meatweapons.network.BeamPacket;
import com.neep.meatweapons.network.MWNetwork;
import com.neep.meatweapons.particle.MWParticles;
import com.neep.neepmeat.machine.integrator.IntegratorBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.init.SoundInitialiser;
import com.neep.neepmeat.recipe.EnlighteningRecipe;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PedestalBlockEntity extends SyncableBlockEntity
{
    protected WritableStackStorage storage;
    protected final RecipeBehaviour recipeBehaviour;
    protected boolean powered;
    protected boolean hasRecipe;

    public PedestalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.recipeBehaviour = new PedestalBlockEntity.RecipeBehaviour();
        this.storage = new WritableStackStorage(this::sync, 1)
        {
            @Override
            protected void onFinalCommit()
            {
                super.onFinalCommit();
                world.updateComparators(pos, getCachedState().getBlock());
            }
        };
    }

    public PedestalBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.PEDESTAL, pos, state);
    }

    public void update(boolean redstone)
    {
        if (!powered && redstone)
        {
            recipeBehaviour.update();
        }
        powered = redstone;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        recipeBehaviour.writeNbt(nbt);
        storage.writeNbt(nbt);
        nbt.putBoolean("hasRecipe", hasRecipe);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        recipeBehaviour.readNbt(nbt);
        storage.readNbt(nbt);
        this.hasRecipe = nbt.getBoolean("hasRecipe");
    }

    public void spawnParticles(int count, double dy, double speed)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(MWParticles.PLASMA_PARTICLE, pos.getX() + 0.5, pos.getY() + 0.5 - 2, pos.getZ() + 0.5, count, 0, dy, 0, 0.1);
        }
    }

    public static void spawnBeam(ServerWorld world, BlockPos startPos, BlockPos endPos)
    {
        Vec3d start = Vec3d.ofCenter(startPos, 0.9);
        Vec3d end = Vec3d.ofCenter(endPos, 0.8f);
        for (ServerPlayerEntity player : PlayerLookup.around(world, start, 32d))
        {
            Packet<?> packet = BeamPacket.create(world, GraphicsEffects.BEAM, start, end, new Vec3d(0, 0, 0), 0.5f, 50, MWNetwork.EFFECT_ID);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, packet);
        }
    }

    public Storage<ItemVariant> getStorage(@Nullable Direction dir)
    {
        return storage;
    }

    public class RecipeBehaviour extends com.neep.meatlib.recipe.RecipeBehaviour<EnlighteningRecipe> implements ImplementedRecipe.DummyInventory, NbtSerialisable
    {
        public WritableStackStorage getStorage()
        {
            return storage;
        }
        protected BlockPos integrator = BlockPos.ORIGIN;

        @Override
        public void startRecipe(EnlighteningRecipe recipe)
        {

            setRecipe(recipe);
            hasRecipe = true;

            getIntegrator().setLookPos(pos);
            world.createAndScheduleBlockTick(pos, getCachedState().getBlock(), 50);
            world.playSound(null, pos, SoundInitialiser.COSMIC_BEAM, SoundCategory.BLOCKS, 10, 0.8f);
            spawnBeam((ServerWorld) world, integrator.up(), pos);
            sync();
        }

        @Override
        public void interrupt()
        {
            setRecipe(null);
            hasRecipe = false;
        }

        @Override
        public void finishRecipe()
        {
            load(world);
            IntegratorBlockEntity integrator = IntegratorBlockEntity.findIntegrator(world, pos, 10);
            try (Transaction transaction = Transaction.openOuter())
            {
                if (currentRecipe != null && integrator != null)
                    currentRecipe.craft(this, transaction);
                transaction.commit();
            }
            setRecipe(null);
            hasRecipe = false;
            sync();
        }

        public void update()
        {
            load(world);
            if (currentRecipe == null)
            {
                IntegratorBlockEntity integrator = IntegratorBlockEntity.findIntegrator(world, pos, 10);
                if (integrator == null || !integrator.isMature()) return;
                this.integrator = integrator.getPos();

                EnlighteningRecipe recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.ENLIGHTENING, this, world).orElse(null);
                if (recipe != null)
                {
                    startRecipe(recipe);
                }
            }
            else interrupt();
            sync();
        }

        @Override
        public void writeNbt(NbtCompound tag)
        {
            tag.put("integrator", NbtHelper.fromBlockPos(integrator));
            if (currentRecipe != null)
            {
                tag.putString("recipe", currentRecipe.getId().toString());
            }
        }

        @Override
        public void readNbt(NbtCompound tag)
        {
            this.integrator = NbtHelper.toBlockPos(tag.getCompound("integrator"));
            String id = tag.getString("recipe");
            if (id != null)
            {
                this.recipeId = new Identifier(id);
            }
            else this.recipeId = null;
        }

        public void load(World world)
        {
            if (currentRecipe == null && recipeId != null)
            {
                currentRecipe = (EnlighteningRecipe) world.getRecipeManager().get(recipeId).orElse(null);
            }
            recipeId = null;
        }

        public IntegratorBlockEntity getIntegrator()
        {
            if (world.getBlockEntity(integrator) instanceof IntegratorBlockEntity be) return be;
            return null;
        }
    }

    public void extractFromItem(ItemEntity itemEntity)
    {
        ItemStack itemStack = itemEntity.getStack();
        if (itemStack.isEmpty())
            return;

        try (Transaction transaction = Transaction.openOuter())
        {
            int transferred = (int) storage.insert(ItemVariant.of(itemStack), itemStack.getCount(), transaction);
            itemStack.decrement(transferred);
            if (itemStack.getCount() <= 0)
            {
                itemEntity.discard();
            }

            transaction.commit();
        }
    }
}
