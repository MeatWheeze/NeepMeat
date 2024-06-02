package com.neep.neepmeat.machine.fabricator;

import com.neep.meatlib.blockentity.SyncableBlockEntity;
import com.neep.meatlib.inventory.ImplementedInventory;
import com.neep.neepmeat.api.machine.MotorisedBlock;
import com.neep.neepmeat.machine.motor.MotorEntity;
import com.neep.neepmeat.transport.util.ItemPipeUtil;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.CombinedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FabricatorBlockEntity extends SyncableBlockEntity implements MotorisedBlock, ExtendedScreenHandlerFactory
{
    private final List<BlockApiCache<Storage<ItemVariant>, Direction>> caches = Arrays.asList(new BlockApiCache[6]);
    private final FabricatorInventory inventory = new FabricatorInventory();

    public FabricatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);
    }

    @Override
    public boolean motorTick(MotorEntity motor)
    {
//        CraftingRecipe recipe = (CraftingRecipe) world.getRecipeManager().get(new Identifier(NeepMeat.NAMESPACE, "building/asbestos")).orElse(null);
        CraftingRecipe recipe = determineRecipe(inventory.getItems());
        if (recipe != null)
        {
            Direction facing = getCachedState().get(FabricatorBlock.FACING);
            Storage<ItemVariant> input = getInput(facing);

            // Mutable list of ingredients
            List<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients());
            List<ItemVariant> takenResources = new ArrayList<>();

            try (Transaction transaction = Transaction.openOuter())
            {
                boolean foundAll = findMatching(input, ingredients, takenResources, transaction);
                if (!foundAll)
                {
                    transaction.abort();
                    return true;
                }

                long ejected = ItemPipeUtil.stackToAny((ServerWorld) world, pos, facing,
                        ItemVariant.of(recipe.getOutput(world.getRegistryManager())), 1, transaction);

                if (ejected != 1)
                {
                    transaction.abort();
                    return true;
                }

                // Eject remainders
                for (var taken : takenResources)
                {
                    ItemStack remainder = taken.getItem().getRecipeRemainder(taken.toStack());
                    if (!remainder.isEmpty())
                    {
                        long ejected1 = ItemPipeUtil.stackToAny((ServerWorld) world, pos, facing,
                                ItemVariant.of(remainder), remainder.getCount(), transaction);

                        if (ejected1 != remainder.getCount())
                        {
                            transaction.abort();
                            return true;
                        }
                    }
                }
                transaction.commit();
                return true;
            }
        }
        return true;
    }

    private boolean findMatching(Storage<ItemVariant> input, List<Ingredient> ingredients, List<ItemVariant> takenResources, TransactionContext transaction)
    {
        for (StorageView<ItemVariant> view : input)
        {
            if (ingredients.isEmpty())
                return true;

            if (view.isResourceBlank() || view.getAmount() <= 0)
                continue;

            ItemVariant resource = view.getResource();

            var it = ingredients.iterator();
            while (it.hasNext())
            {
                Ingredient ingredient = it.next();

                if (ingredient.isEmpty())
                {
                    it.remove();
                    continue;
                }

                if (ingredient.test(view.getResource().toStack()))
                {
                    // Remove the ingredient once it is satisfied
                    long extracted = view.extract(resource, 1, transaction);
                    if (extracted == 1)
                    {
                        takenResources.add(view.getResource());
                        it.remove();
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    private CraftingRecipe determineRecipe(DefaultedList<ItemStack> list)
    {
        return world.getRecipeManager().getFirstMatch(RecipeType.CRAFTING, inventory, world).orElse(null);
    }

    @Override
    public void writeNbt(NbtCompound nbt)
    {
        super.writeNbt(nbt);
        inventory.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt)
    {
        super.readNbt(nbt);
        inventory.readNbt(nbt);
    }

    @Override
    public void setInputPower(float power)
    {

    }

    private CombinedStorage<ItemVariant, Storage<ItemVariant>> getInput(Direction facing)
    {
        List<Storage<ItemVariant>> storages = new ArrayList<>();
        for (Direction direction : Direction.values())
        {
            if (direction == facing.getOpposite() || direction == facing.rotateYClockwise() || direction == facing.rotateYCounterclockwise())
            {
                Storage<ItemVariant> storage = findStorage(direction);
                if (storage != null)
                    storages.add(storage);
            }
        }
        return new CombinedStorage<>(storages);
    }

    @Nullable
    private Storage<ItemVariant> findStorage(Direction face)
    {
        if (caches.get(face.ordinal()) == null)
        {
            caches.set(face.ordinal(), BlockApiCache.create(ItemStorage.SIDED, (ServerWorld) world, pos.offset(face)));
        }

        return caches.get(face.ordinal()).find(face.getOpposite());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player)
    {
        return new FabricatorScreenHandler(playerInventory, inventory, syncId, this);
    }

    @Override
    public Text getDisplayName()
    {
        return Text.of("Enter Pattern");
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf)
    {
        buf.writeBlockPos(getPos());
    }

    @Nullable
    public CraftingRecipe getCurrentRecipe()
    {
        return determineRecipe(inventory.getItems());
    }

    public class FabricatorInventory implements ImplementedInventory, RecipeInputInventory
    {
        private final DefaultedList<ItemStack> items = DefaultedList.ofSize(9, ItemStack.EMPTY);

        @Override
        public DefaultedList<ItemStack> getItems()
        {
            return items;
        }

        @Override
        public void markDirty()
        {
            FabricatorBlockEntity.this.markDirty();
        }

        @Override
        public int getWidth()
        {
            return 3;
        }

        @Override
        public int getHeight()
        {
            return 3;
        }

        @Override
        public List<ItemStack> getInputStacks()
        {
            return items;
        }

        @Override
        public void provideRecipeInputs(RecipeMatcher finder)
        {
            for (ItemStack itemStack : this.items)
            {
                finder.addUnenchantedInput(itemStack);
            }
        }
    }
}
