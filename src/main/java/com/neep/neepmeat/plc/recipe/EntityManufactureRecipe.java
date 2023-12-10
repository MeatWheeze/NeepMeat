package com.neep.neepmeat.plc.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.init.NMComponents;
import com.neep.neepmeat.player.implant.ImplantInstaller;
import com.neep.neepmeat.player.implant.ImplantRegistry;
import com.neep.neepmeat.plc.component.MutateInPlace;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class EntityManufactureRecipe implements ManufactureRecipe<MutateInPlace<Entity>>
{
    protected final EntityType<?> base;
    private final List<ManufactureStep<?>> steps;
    private final ImplantInstaller implant;
    private final Identifier id;

    public EntityManufactureRecipe(Identifier id, EntityType<?> base, List<ManufactureStep<?>> steps, ImplantInstaller implant)
    {
        this.id = id;
        this.base = base;
        this.steps = steps;
        this.implant = implant;
    }

    @Override
    public boolean matches(MutateInPlace<Entity> context)
    {
        Entity entity = context.get();

        if (!entity.getType().equals(base))
            return false;

        var workpiece = NMComponents.WORKPIECE.getNullable(entity);
        if (workpiece != null)
        {
            var workSteps = workpiece.getSteps();

            if (workSteps.size() < steps.size())
                return false;

            int difference = workSteps.size() - steps.size();

            int i = steps.size() - 1;
            while (i >= 0)
            {
                var workStep = workSteps.get(i + difference);
                var recipeStep = steps.get(i);

                if (!ManufactureStep.equals(workStep, recipeStep))
                {
                    return false;
                }

                i--;
            }
            return true;
        }
        return false;
    }

    @Override
    public Object getBase()
    {
        return base;
    }

    public List<ManufactureStep<?>> getSteps()
    {
        return steps;
    }

    @Override
    public boolean takeInputs(MutateInPlace<Entity> context, TransactionContext transaction)
    {
        return false;
    }

    @Override
    public boolean ejectOutputs(MutateInPlace<Entity> context, TransactionContext transaction)
    {
        implant.install(context.get());
        return false;
    }

    @Override
    public MeatRecipeType<?> getType()
    {
        return PLCRecipes.ENTITY_MANUFACTURE;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return PLCRecipes.ENTITY_MANUFACTURE_SERIALISER;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    public static class Serialiser implements MeatRecipeSerialiser<EntityManufactureRecipe>
    {
        @Override
        public EntityManufactureRecipe read(Identifier id, JsonObject json)
        {
//            Identifier baseId = Identifier.tryParse(JsonHelper.getString(json , "base"));
//            EntityType<?> base = Registry.ENTITY_TYPE.get(baseId);

            JsonObject baseElement = JsonHelper.getObject(json, "base");
            String idString = JsonHelper.getString(baseElement, "id");
            EntityType<?> base = Registry.ENTITY_TYPE.get(Identifier.tryParse(idString));

            List<ManufactureStep<?>> steps = ItemManufactureRecipe.Serialiser.readSteps(json);

            ImplantInstaller installer;
            if (JsonHelper.hasJsonObject(json, "implant_installer"))
            {
                Identifier installerId = Identifier.tryParse(JsonHelper.getString(JsonHelper.getObject(json, "implant_installer"), "id"));

                installer = ImplantInstaller.REGISTRY.get(installerId);
                if (installer == null) throw new JsonSyntaxException("Implant installer " + installerId + " does not exist.");
            }
            else throw new JsonSyntaxException("Implant installer not found.");

            return new EntityManufactureRecipe(id, base, steps, installer);
        }

        @Override
        public EntityManufactureRecipe read(Identifier id, PacketByteBuf buf)
        {
            EntityType<?> base = buf.readRegistryValue(Registry.ENTITY_TYPE);

            List<ManufactureStep<?>> steps = ItemManufactureRecipe.Serialiser.readSteps(buf);

            ImplantInstaller implant = buf.readRegistryValue(ImplantInstaller.REGISTRY);

            return new EntityManufactureRecipe(id, base, steps, implant);
        }

        @Override
        public void write(PacketByteBuf buf, EntityManufactureRecipe recipe)
        {
            buf.writeRegistryValue(Registry.ENTITY_TYPE, recipe.base);

            ItemManufactureRecipe.Serialiser.writeSteps(recipe.getSteps(), buf);

            buf.writeRegistryValue(ImplantInstaller.REGISTRY, recipe.implant);
        }
    }
}
