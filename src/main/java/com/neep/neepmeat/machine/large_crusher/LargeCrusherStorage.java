package com.neep.neepmeat.machine.large_crusher;

import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.meatlib.recipe.MeatlibRecipes;
import com.neep.meatlib.util.NbtSerialisable;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.storage.WritableStackStorage;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.grinder.IGrinderStorage;
import com.neep.neepmeat.recipe.GrindingRecipe;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LargeCrusherStorage implements NbtSerialisable
{
    protected final List<InputSlot> slots;

    protected final CombinedStorage<ItemVariant, InputSlot> inputStorage;

    final ImplementedInventory outputInventory = ImplementedInventory.ofSize(8);
    final InventoryStorage outputStorage = InventoryStorage.of(outputInventory, null);

    private final IGrinderStorage.XpStorage xpStorage = new IGrinderStorage.XpStorage();

    public LargeCrusherStorage(LargeCrusherBlockEntity parent)
    {
        slots = List.of(
                new InputSlot(parent::sync),
                new InputSlot(parent::sync),
                new InputSlot(parent::sync),
                new InputSlot(parent::sync));

        inputStorage = new CombinedStorage<>(slots);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt)
    {
        NbtList list = new NbtList();
        for (InputSlot slot : slots)
        {
            list.add(slot.toNbt(new NbtCompound()));
        }
        nbt.put("input", list);
        nbt.put("output", outputInventory.writeNbt(new NbtCompound()));
        nbt.put("xp", xpStorage.writeNbt(new NbtCompound()));
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        NbtList list = nbt.getList("input", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < list.size(); ++i)
        {
            slots.get(i).readNbt(list.getCompound(i));
        }

        outputInventory.readNbt(nbt.getCompound("output"));
        xpStorage.readNbt(nbt.getCompound("xp"));
    }

    protected class InputSlot extends WritableStackStorage implements IGrinderStorage
    {
        @Nullable
        private GrindingRecipe recipe;
        private float progress;

        public InputSlot(@Nullable Runnable parent)
        {
            super(parent);
        }

        public void tick(float progressIncrement, TransactionContext transaction)
        {
            if (recipe != null)
            {
                progress = progress + progressIncrement;
                if (progress >= recipe.getTime())
                {
                    if (recipe.takeInputs(this, transaction))
                    {
                        recipe.ejectOutputs(this, transaction);
                    }
                    recipe = null;
                    progress = 0;
                    syncIfPossible();
                }
            }
            else if (!isEmpty())
            {
                GrindingRecipe foundRecipe = MeatlibRecipes.getInstance().getFirstMatch(NMrecipeTypes.GRINDING, this).orElse(null);
                if (foundRecipe != null)
                {
                    recipe = foundRecipe;
                }
                else if (!isEmpty())
                {
                    recipe = MeatlibRecipes.getInstance().get(NMrecipeTypes.GRINDING, new Identifier(NeepMeat.NAMESPACE, "grinding/destroy")).orElse(null);
                }
                syncIfPossible();
            }
        }

        @Override
        public void writeNbt(NbtCompound nbt)
        {
            if (recipe != null)
                nbt.putString("recipe", recipe.getId().toString());
            super.writeNbt(nbt);
        }

        @Override
        public void readNbt(NbtCompound nbt)
        {
            super.readNbt(nbt);
            if (nbt.contains("recipe"))
                this.recipe = MeatlibRecipes.getInstance().get(NMrecipeTypes.GRINDING, Identifier.tryParse(nbt.getString("recipe"))).orElse(null);
            else
                this.recipe = null;
        }

        @Override
        public Storage<ItemVariant> getInputStorage()
        {
            return this;
        }

        @Override
        public Storage<ItemVariant> getOutputStorage()
        {
            return outputStorage;
        }

        @Override
        public Storage<ItemVariant> getExtraStorage()
        {
            return outputStorage;
        }

        @Override
        public XpStorage getXpStorage()
        {
            return xpStorage;
        }

        @Nullable
        public GrindingRecipe getRecipe()
        {
            return recipe;
        }
    }
}
