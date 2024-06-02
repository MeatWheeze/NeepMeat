package com.neep.neepmeat.compat.emi;

import com.google.common.collect.Lists;
import com.neep.neepmeat.machine.fabricator.FabricatorScreenHandler;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FabricatorRecipeHandler implements StandardRecipeHandler<FabricatorScreenHandler>
{
    @Override
    public List<Slot> getInputSources(FabricatorScreenHandler handler)
    {
        List<Slot> list = Lists.newArrayList();
        int invStart = 8;
        for (int i = invStart; i < invStart + 36; i++)
        {
            list.add(handler.getSlot(i));
        }
        return list;
//        return List.of();
    }

    @Override
    public List<Slot> getCraftingSlots(FabricatorScreenHandler handler)
    {
        List<Slot> list = Lists.newArrayList();
        for (int i = 0; i < 9; i++)
        {
            list.add(handler.getSlot(i));
        }
        return list;
    }

    @Override
    public boolean canCraft(EmiRecipe recipe, EmiCraftContext<FabricatorScreenHandler> context)
    {
//        return StandardRecipeHandler.super.canCraft(recipe, context);
        return true;
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<FabricatorScreenHandler> context)
    {
        StandardRecipeHandler.super.craft(recipe, context);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(recipe.getId());
        ClientPlayNetworking.send(FabricatorScreenHandler.FILL_RECIPE_ID, buf);

        return true;
    }

    public void clickSlot(FabricatorScreenHandler handler, int slotId, ItemStack newStack)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        DefaultedList<Slot> defaultedList = handler.slots;
        int size = defaultedList.size();
        List<ItemStack> list = Lists.newArrayListWithCapacity(size);

        int click = 1;

        for (Slot slot : defaultedList)
        {
            list.add(slot.getStack().copy());
        }

//        handler.onSlotClick(slotId, click, SlotActionType.SWAP, client.player);
        handler.slots.get(slotId).setStack(newStack);

        Int2ObjectMap<ItemStack> int2ObjectMap = new Int2ObjectOpenHashMap<>();

        for (int j = 0; j < size; ++j)
        {
            ItemStack itemStack = list.get(j);
            ItemStack itemStack2 = defaultedList.get(j).getStack();
            if (!ItemStack.areEqual(itemStack, itemStack2))
            {
                int2ObjectMap.put(j, itemStack2.copy());
            }
        }

        client.getNetworkHandler()
                .sendPacket(new ClickSlotC2SPacket(handler.syncId, handler.getRevision(), slotId, click, SlotActionType.SWAP, newStack, int2ObjectMap));
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe)
    {
        return recipe.getCategory() == VanillaEmiRecipeCategories.CRAFTING;
    }
}
