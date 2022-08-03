package com.neep.neepmeat.machine.alloy_kiln;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.recipe.RecipeOutput;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.NMBlockEntities;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.IHeatable;
import com.neep.neepmeat.machine.motor.IMotorBlockEntity;
import com.neep.neepmeat.recipe.AlloyKilnRecipe;
import com.neep.neepmeat.screen_handler.AlloyKilnScreenHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Recipe;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class AlloyKilnBlockEntity extends SyncableBlockEntity implements IHeatable, NamedScreenHandlerFactory
{
    protected int fuelTime;
    protected int burnTime;
    protected int cookTime;
    protected int cookTimeTotal;

    protected Identifier currentRecipeId;
    protected AlloyKilnRecipe currentRecipe;
    protected IMotorBlockEntity connectedMotor;

    protected AlloyKilnStorage storage;

    protected float heatMultiplier;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
    {
        @Override
        public int get(int index)
        {
            switch (index)
            {
                case 0 -> {
                    return burnTime;
                }
                case 1 -> {
                    return fuelTime;
                }
                case 2 -> {
                    return cookTime;
                }
                case 3 -> {
                    return cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value)
        {
            switch (index)
            {
                case 0 -> burnTime = value;
                case 1 -> fuelTime = value;
                case 2 -> cookTime = value;
                case 3 -> cookTimeTotal = value;
            }
        }

        @Override
        public int size()
        {
            return 4;
        }
    };

    public AlloyKilnBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
        this.storage = new AlloyKilnStorage(this);
    }

    public AlloyKilnBlockEntity(BlockPos pos, BlockState state)
    {
        this(NMBlockEntities.ALLOY_KILN, pos, state);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, AlloyKilnBlockEntity be)
    {
        be.tick(world, pos, state);
    }

    public void tick(World world, BlockPos pos, BlockState state)
    {
        boolean wasBurning = isBurning();

        if (currentRecipe == null)
            readCurrentRecipe();

        this.burnTime = Math.max(0, this.burnTime - 1);

        if (isBurning())
        {
            if (isCooking())
            {
                int tickIncrement = (int) Math.floor(heatMultiplier * 3) + 1;
                this.cookTime = Math.min(this.cookTimeTotal, this.cookTime + tickIncrement);
            }
            else
            {
                startCooking();
            }
        }
        else
        {
            int time;
            if (detectRecipe() && (time = storage.decrementFuel()) > 0)
            {
                this.fuelTime = time;
                this.burnTime = time;
                updateState(world, pos, state);
            }
            else
            {
                this.cookTime = Math.max(0, this.cookTime - 1);

                if (wasBurning)
                    updateState(world, pos, state);
            }
        }

        if (this.cookTime == this.cookTimeTotal)
        {
            finishCooking();
        }
    }

    protected boolean detectRecipe()
    {
        return this.currentRecipe != null
                || world.getRecipeManager().getFirstMatch(NMrecipeTypes.ALLOY_SMELTING, storage, world).isPresent();
    }

    protected void startCooking()
    {
        AlloyKilnRecipe recipe = world.getRecipeManager().getFirstMatch(NMrecipeTypes.ALLOY_SMELTING, storage, world).orElse(null);
        if (canAcceptRecipeOutput(recipe, storage.inventory.getItems()))
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (recipe.takeInputs(storage, transaction))
                {
                    this.currentRecipe = recipe;
                    this.currentRecipeId = recipe.getId();
                    this.cookTimeTotal = recipe.getTime();
                    transaction.commit();
                    sync();
                    return;
                }
                transaction.abort();
            }
        }
        this.currentRecipe = null;
        this.currentRecipeId = null;
        this.cookTimeTotal = -1;
        this.cookTime = 0;
    }

    public static boolean canAcceptRecipeOutput(@Nullable AlloyKilnRecipe recipe, DefaultedList<ItemStack> slots)
    {
        if (slots.get(AlloyKilnStorage.INPUT_1).isEmpty()
                || slots.get(AlloyKilnStorage.INPUT_2).isEmpty() || recipe == null)
        {
            return false;
        }
        RecipeOutput<Item> output = recipe.getItemOutput();
        ItemStack outputStack = slots.get(AlloyKilnStorage.OUTPUT);
        if (outputStack.isEmpty())
        {
            return true;
        }
        if (!outputStack.isOf(output.resource()))
        {
            return false;
        }
        if (outputStack.getCount() < outputStack.getMaxCount())
        {
            return true;
        }
        return outputStack.getCount() < output.maxAmount();
    }

    protected void finishCooking()
    {
        if (currentRecipe != null)
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                if (currentRecipe.ejectOutput(storage, transaction))
                {
                    transaction.commit();
                }
                else
                {
                    transaction.abort();
                }
            }
            sync();
        }

        this.currentRecipe = null;
        this.currentRecipeId = null;
        this.cookTimeTotal = -1;
        this.cookTime = 0;
    }

    protected boolean isCooking()
    {
        return cookTimeTotal != -1;
    }

    public boolean isBurning()
    {
        return burnTime > 0;
    }

    public void readCurrentRecipe()
    {
        if (world != null)
        {
            Optional<? extends Recipe<?>> optional = getWorld().getRecipeManager().get(currentRecipeId);
            optional.ifPresentOrElse(recipe -> this.currentRecipe = (AlloyKilnRecipe) recipe,
                    () -> this.currentRecipe = null);
        }
    }

    @Override
    public void setBurning()
    {
        this.burnTime = 2;
    }

    @Override
    public void updateState(World world, BlockPos pos, BlockState oldState)
    {
        BlockState state = getCachedState();
        if (state.get(AlloyKilnBlock.LIT) != this.isBurning())
        {
            state = state.with(AlloyKilnBlock.LIT, this.isBurning());
            getWorld().setBlockState(pos, state, Block.NOTIFY_ALL);
        }
    }

    @Override
    public void setHeatMultiplier(float multiplier)
    {
        this.heatMultiplier = multiplier;
    }

    @Override
    public float getHeatMultiplier()
    {
        return heatMultiplier;
    }

    @Override
    public int getCookTimeTotal()
    {
        return cookTimeTotal;
    }

    @Override
    public int getCookTime()
    {
        return cookTime;
    }

    @Override
    public void setCookTime(int cookTime)
    {
        this.cookTime = cookTime;
    }

    @Override
    public Text getDisplayName()
    {
        return new TranslatableText("container." + NeepMeat.NAMESPACE + ".alloy_kiln");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player)
    {
        return new AlloyKilnScreenHandler(syncId, inv, storage.inventory, propertyDelegate);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        nbt.putShort("fuelTime", (short) fuelTime);
        nbt.putShort("burnTime", (short) burnTime);
        nbt.putShort("cookTime", (short) cookTime);
        nbt.putShort("cookTimeTotal", (short) cookTimeTotal);

        storage.writeNbt(nbt);

        if (currentRecipe != null)
            nbt.putString("current_recipe", currentRecipe.getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        this.fuelTime = nbt.getShort("fuelTime");
        this.burnTime = nbt.getShort("burnTime");
        this.cookTime = nbt.getShort("cookTime");
        this.cookTimeTotal = nbt.getShort("cookTimeTotal");

        storage.readNbt(nbt);

        this.currentRecipeId = new Identifier(nbt.getString("current_recipe"));
        readCurrentRecipe();
    }

    public AlloyKilnStorage getStorage()
    {
        return storage;
    }
}
