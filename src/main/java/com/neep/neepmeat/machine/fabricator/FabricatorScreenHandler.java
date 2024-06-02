package com.neep.neepmeat.machine.fabricator;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.init.ScreenHandlerInit;
import com.neep.neepmeat.screen_handler.BasicScreenHandler;
import com.neep.neepmeat.screen_handler.slot.PatternSlot;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;

public class FabricatorScreenHandler extends BasicScreenHandler
{
    public static final Identifier CHANNEL_ID = new Identifier(NeepMeat.NAMESPACE, "fabricator");
    public static final Identifier FILL_RECIPE_ID = new Identifier(NeepMeat.NAMESPACE, "fabricator_fill_recipe");

    private final FabricatorBlockEntity be;

    public FabricatorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf)
    {
        this(inventory, new SimpleRecipeInputInventory(9), syncId,
                (FabricatorBlockEntity) inventory.player.getWorld().getBlockEntity(buf.readBlockPos()));
    }

    public FabricatorScreenHandler(PlayerInventory playerInventory, RecipeInputInventory inventory, int syncId, FabricatorBlockEntity be)
    {
        super(ScreenHandlerInit.FABRICATOR, playerInventory, inventory, syncId, null);
        this.be = be;

        createSlotBlock(19, 19, 3, 3, inventory, 0, PatternSlot::new);
        createPlayerSlots(8, 91 + 8, playerInventory);

        if (playerInventory.player instanceof ServerPlayerEntity serverPlayerEntity)
        {
            ServerPlayNetworking.registerReceiver(serverPlayerEntity.networkHandler, FILL_RECIPE_ID, this::receive);
        }
    }

    void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        Identifier id = buf.readIdentifier();

        server.execute(() ->
        {
            if (server.getRecipeManager().get(id).orElse(null) instanceof CraftingRecipe craftingRecipe)
            {
                fillRecipe(craftingRecipe);
            }
        });
    }

    void fillRecipe(CraftingRecipe recipe)
    {
        List<Ingredient> ingredients = recipe.getIngredients();

        for (int i = 0; i < 9; ++i)
        {
            slots.get(i).setStack(ItemStack.EMPTY);
        }

        if (recipe instanceof ShapedRecipe shapedRecipe)
        {
            int height = shapedRecipe.getHeight();
            int width = shapedRecipe.getWidth();

            for (int j = 0; j < height; ++j)
            {
                for (int i = 0; i < width; ++i)
                {
                    int slotIdx = j * 3 + i;
                    int idx = j * width + i;
                    if (idx < ingredients.size())
                    {
                        Ingredient ingredient = ingredients.get(idx);

                        if (!ingredient.isEmpty())
                            slots.get(slotIdx).setStack(firstValidMatchingStack(ingredient));
                    }
                }
            }
        }
        else
        {
            int slotIdx = 0;
            for (var ingredient : ingredients)
            {
                Slot slot = slots.get(slotIdx);

                if (!ingredient.isEmpty())
                    slot.setStack(ingredient.getMatchingStacks()[0]);

                slotIdx++;
            }
        }
    }

    private ItemStack firstValidMatchingStack(Ingredient ingredient)
    {
        return ingredient.getMatchingStacks()[0].copy();
//        // For some reason, empty stacks can be present in this array.
//        ItemStack[] stacks = ingredient.getMatchingStacks();
//        for (ItemStack stack : stacks)
//        {
//            if (!stack.isEmpty())
//                return stack;
//        }
//        return ItemStack.EMPTY;
    }

    @Override
    public void onClosed(PlayerEntity player)
    {
        if (playerInventory.player instanceof ServerPlayerEntity serverPlayerEntity)
        {
            ServerPlayNetworking.unregisterReceiver(serverPlayerEntity.networkHandler, FILL_RECIPE_ID);
        }
    }

    @Override
    public void sendContentUpdates()
    {
        super.sendContentUpdates();

        if (playerInventory.player.getWorld().getTime() % 5 == 0 && playerInventory.player instanceof ServerPlayerEntity player)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            CraftingRecipe recipe = be.getCurrentRecipe();

            if (recipe != null)
            {
                buf.writeBoolean(true);
                buf.writeIdentifier(recipe.getId());
            }
            else
            {
                buf.writeBoolean(false);
                buf.writeIdentifier(new Identifier("minecraft", "none"));
            }

            ServerPlayNetworking.send(player, CHANNEL_ID, buf);
        }
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player)
    {
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    private static class SimpleRecipeInputInventory extends SimpleInventory implements RecipeInputInventory
    {
        public SimpleRecipeInputInventory(int size)
        {
            super(size);
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
            return stacks;
        }
    }
}
