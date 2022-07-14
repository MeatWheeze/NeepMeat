package com.neep.neepmeat.machine.mixer;

import com.google.gson.JsonObject;
import com.neep.meatlib.recipe.FluidIngredient;
import com.neep.meatlib.recipe.ItemIngredient;
import com.neep.neepmeat.init.NMrecipeTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.NotImplementedException;

@SuppressWarnings("UnstableApiUsage")
public class MixingRecipe implements Recipe<MixerInventory>
{
    protected ItemIngredient itemInput;
    protected FluidIngredient fluidInput1;
    protected FluidIngredient fluidInput2;
    protected FluidIngredient fluidOutput;
    protected int processTime;

    public MixingRecipe(Identifier id, ItemIngredient itemInput, FluidIngredient fluidInput1, FluidIngredient fluidInput2, FluidIngredient fluidOutput, int processTime)
    {
        this.itemInput = itemInput;
        this.fluidInput1 = fluidInput1;
        this.fluidInput2 = fluidInput2;
        this.fluidOutput = fluidOutput;
        this.processTime = processTime;
    }

//    public static class Type implements RecipeType<MixingRecipe>
//    {
//        private Type() {}
//        public static final Type INSTANCE = new Type();
//        public static final String ID = "mixing";
//    }

    @Override
    public boolean matches(MixerInventory inventory, World world)
    {
        throw new NotImplementedException();
    }

    @Override
    public ItemStack craft(MixerInventory inventory)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean fits(int width, int height)
    {
        return false;
    }

    @Override
    public ItemStack getOutput()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public Identifier getId()
    {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return null;
    }

    @Override
    public RecipeType<?> getType()
    {
        return NMrecipeTypes.MIXING_TYPE;
    }

    public static class MixerSerializer implements RecipeSerializer<MixingRecipe>
    {
        RecipeFactory<MixingRecipe> factory;
        int processTIme;

        public MixerSerializer(RecipeFactory<MixingRecipe> recipeFactory, int processTime)
        {
            this.factory = recipeFactory;
            this.processTIme = processTime;
        }

        @Override
        public MixingRecipe read(Identifier id, JsonObject json)
        {
//            if (!JsonHelper.hasArray(json, "item"))
//                throw new JsonSyntaxException("parsing " + id + ": ingredient JSON element is not an array");
            JsonObject itemElement = JsonHelper.getObject(json, "item");
            ItemIngredient itemInput = ItemIngredient.fromJson(itemElement);

            JsonObject fluidElement1 = JsonHelper.getObject(json, "fluid1");
            FluidIngredient fluidInput1 = FluidIngredient.fromJson(fluidElement1);

            JsonObject fluidElement2 = JsonHelper.getObject(json, "fluid2");
            FluidIngredient fluidInput2 = FluidIngredient.fromJson(fluidElement2);

            JsonObject fluidElement3 = JsonHelper.getObject(json, "output");
            FluidIngredient fluidOutput = FluidIngredient.fromJson(fluidElement3);

//            String string2 = JsonHelper.getString(json, "result");
//            Identifier identifier2 = new Identifier(string2);
//            ItemStack itemStack = new ItemStack(Registry.ITEM.getOrEmpty(identifier2).orElseThrow(() -> new IllegalStateException("Item: " + string2 + " does not exist")));
            int time = JsonHelper.getInt(json, "processtime", this.processTIme);
            return this.factory.create(id, itemInput, fluidInput1, fluidInput2, fluidOutput, time);
        }

        @Override
        public MixingRecipe read(Identifier id, PacketByteBuf buf)
        {
            ItemIngredient ingredient = ItemIngredient.fromBuffer(buf);
            FluidIngredient fluidInput1 = FluidIngredient.fromPacket(buf);
            FluidIngredient fluidInput2 = FluidIngredient.fromPacket(buf);
            FluidIngredient fluidOutput = FluidIngredient.fromPacket(buf);
            int time = buf.readVarInt();

            return this.factory.create(id, ingredient, fluidInput1, fluidInput2, fluidOutput, time);
        }

        @Override
        public void write(PacketByteBuf buf, MixingRecipe recipe)
        {
            recipe.itemInput.write(buf);
            recipe.fluidInput1.write(buf);
            recipe.fluidInput2.write(buf);
            recipe.fluidOutput.write(buf);
            buf.writeVarInt(recipe.processTime);
        }

        @FunctionalInterface
        public interface RecipeFactory<T extends MixingRecipe>
        {
            T create(Identifier var1, ItemIngredient var3, FluidIngredient f1, FluidIngredient f2, FluidIngredient out, int time);
        }
    }

}
