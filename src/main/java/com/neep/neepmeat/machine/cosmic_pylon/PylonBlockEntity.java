package com.neep.neepmeat.machine.cosmic_pylon;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.FluidIngredient;
import com.neep.meatlib.recipe.ImplementedRecipe;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.blockentity.DisplayPlatformBlockEntity;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMParticles;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.particle.SwirlingParticleEffect;
import com.neep.neepmeat.recipe.EnlighteningRecipe;
import com.neep.neepmeat.storage.WritableStackStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PylonBlockEntity extends SyncableBlockEntity
{
    protected final RecipeBehaviour recipeBehaviour;
    protected boolean powered;
    protected boolean hasRecipe;

    public PylonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.recipeBehaviour = new PylonBlockEntity.RecipeBehaviour();
    }

    public PylonBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.COSMIC_PYLON, pos, state);
    }

    public void update(boolean redstone)
    {
        if (!powered && redstone)
        {
            recipeBehaviour.update();
            // Activate
        }
        powered = redstone;
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        recipeBehaviour.writeNbt(nbt);
        nbt.putBoolean("hasRecipe", hasRecipe);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        recipeBehaviour.readNbt(nbt);
        this.hasRecipe = nbt.getBoolean("hasRecipe");
    }

    public void spawnParticles(int count, double dy, double speed)
    {
        if (world instanceof ServerWorld serverWorld)
        {
            serverWorld.spawnParticles(new SwirlingParticleEffect(NMParticles.BLOCK_SWIRL,
                    Blocks.AMETHYST_BLOCK.getDefaultState(), 0.4, speed), pos.getX() + 0.5, pos.getY() + 0.5 + 1, pos.getZ() + 0.5, count, 0, dy, 0, 0.1);
        }
    }

    public class RecipeBehaviour extends com.neep.meatlib.recipe.RecipeBehaviour<EnlighteningRecipe> implements ImplementedRecipe.DummyInventory, NbtSerialisable
    {
        public WritableStackStorage getStorage()
        {
            if (world.getBlockEntity(pos.down(2)) instanceof DisplayPlatformBlockEntity platform)
                return platform.getStorage(null);

            return null;
        }

        @Override
        public void startRecipe(EnlighteningRecipe recipe)
        {
            setRecipe(recipe);
            hasRecipe = true;
            world.createAndScheduleBlockTick(pos, getCachedState().getBlock(), 50);
            spawnParticles(10, 0.4, 0.1);
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
            try (Transaction transaction = Transaction.openOuter())
            {
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
            if (currentRecipe != null)
            {
                tag.putString("recipe", currentRecipe.getId().toString());
            }
        }

        @Override
        public void readNbt(NbtCompound tag)
        {
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
    }
}
