package com.neep.neepmeat.machine.multitank;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatlib.transfer.MultiFluidBuffer;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.Heatable;
import com.neep.neepmeat.recipe.FluidHeatingRecipe;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MultiTankBlockEntity extends SyncableBlockEntity implements Heatable
{
    protected MultiFluidBuffer buffer;
    protected float heat;
    protected Identifier currentRecipeId;
    protected float recipeProgress;
    protected float increment;

    public MultiTankBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.buffer = new MultiFluidBuffer(8 * FluidConstants.BUCKET, variant -> true, this::sync);
    }

    public MultiTankBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.MULTI_TANK, pos, state);
    }

    public MultiFluidBuffer getStorage()
    {
        return buffer;
    }

    public boolean onUse(PlayerEntity player, Hand hand)
    {
        if (buffer.handleInteract(world, player, hand))
        {
            return true;
        }
        if (!world.isClient())
        {
            showContents((ServerPlayerEntity) player, world, getPos(), getStorage());
            return true;
        }
        return true;
    }

    public static void showContents(ServerPlayerEntity player, World world, BlockPos pos, MultiFluidBuffer buffer)
    {
        world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCKS, 1f, 1.5f);
        long mb = Math.floorDiv(buffer.getTotalAmount(), FluidConstants.BUCKET / 1000);
        player.sendMessage(Text.of("Fluid" + ": " + mb + "mb"), true);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        buffer.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        buffer.readNbt(nbt);
    }

    @Override
    public void setBurning()
    {

    }

    @Override
    public void setHeat(float heat)
    {
        this.heat = heat;
        this.increment = heat;
    }

//    @Override
//    public float getHeat()
//    {
//        return heat;
//    }

    public static void serverTick(World world, BlockPos pos, BlockState state, MultiTankBlockEntity be)
    {
        if (be.currentRecipeId != null)
        {
            be.recipeProgress = Math.max(0, be.recipeProgress - be.increment);
            if (be.recipeProgress == 0)
            {
                MeatlibRecipes.getInstance().get(NMrecipeTypes.HEATING, be.currentRecipeId).ifPresent(recipe ->
                {
                    try (Transaction transaction = Transaction.openOuter())
                    {
                        if (recipe.takeInputs(be, transaction) && recipe.ejectOutputs(be, transaction))
                        {
                            transaction.commit();
                            be.currentRecipeId = null;
                            return;
                        }
                        transaction.abort();
                    }
                });
            }
        }

        if (be.heat > 0 && be.currentRecipeId == null)
        {
            FluidHeatingRecipe recipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.HEATING, be).orElse(null);
            if (recipe != null)
            {
                be.currentRecipeId = recipe.getId();
                be.recipeProgress = recipe.getTime();
            }
        }
    }
}