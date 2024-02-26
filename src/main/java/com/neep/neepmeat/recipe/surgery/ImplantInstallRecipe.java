package com.neep.neepmeat.recipe.surgery;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.transfer.EntityVariant;
import com.neep.neepmeat.implant.player.EntityImplantInstaller;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.surgery_platform.SurgeryPlatformBlockEntity;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import com.neep.neepmeat.plc.component.TableComponent;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Optional;

@Deprecated
@SuppressWarnings("UnstableApiUsage")
public class ImplantInstallRecipe extends SurgeryRecipe
{
    protected final Identifier id;
    protected final RecipeInput<?> resourceInput;
    protected final EntityImplantInstaller installer;

    // Jank constants
    protected static final int MODULE_SLOT = 7;
    protected static final int MOB_SLOT = 4;

    public ImplantInstallRecipe(Identifier id, RecipeInput<?> resourceInput, EntityImplantInstaller installer)
    {
        this.id = id;
        this.resourceInput = resourceInput;
        this.installer = installer;
    }

    @Override
    public boolean matches(SurgeryTableContext context)
    {
        TableComponent<?> mobSlot = context.getStructure(MOB_SLOT);
        TableComponent<? extends TransferVariant<?>> itemSlot = context.getStructure(MODULE_SLOT);

        if (mobSlot != null && mobSlot.getType().equals(RecipeInputs.ENTITY_MUTATE_ID)
            && itemSlot != null && (itemSlot.getType().equals(RecipeInputs.ITEM_ID) || itemSlot.getType().equals(RecipeInputs.FLUID_ID)))
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                MutableBoolean b = new MutableBoolean(true);

                b.setValue(b.getValue() && resourceInput.test(itemSlot.getStorage(), transaction));

                mobSlot.getStorage().forEach(storageView ->
                    b.setValue(b.getValue() && storageView.getAmount() == 1));

                transaction.abort();
                return b.getValue();
            }
        }

        return false;
    }

    @Override
    public boolean takeInputs(SurgeryTableContext context, TransactionContext transaction)
    {
        return false;
    }


    @Override
    public MeatRecipeType<?> getType()
    {
        return NMrecipeTypes.IMPLANT_INSTALL;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerializer()
    {
        return NMrecipeTypes.IMPLANT_INSTALL_SERIALIZER;
    }

    @Override
    public Identifier getId()
    {
        return id;
    }

    @Override
    public boolean isInputEmpty(int recipeProgress)
    {
        return recipeProgress != MODULE_SLOT;
    }

    @Override
    public boolean takeInput(SurgeryTableContext context, int i, TransactionContext transaction)
    {
        TableComponent<TransferVariant<?>> component = context.getStructure(i);

        // Abort if the structure is invalid (block has probably been broken)
        if (component == null) return false;

        Storage<TransferVariant<?>> storage = component.getStorage();
        Optional<?> matching = resourceInput.getFirstMatching(storage, transaction);
        if (matching.isPresent())
        {
            Transaction inner = transaction.openNested();
            TransferVariant<?> variant = GeneralSurgeryRecipe.TRANSFER_MAP.get(resourceInput.getType()).apply(matching.get());
            if (storage.extract(variant, resourceInput.amount(), inner) == resourceInput.amount())
            {
                inner.commit();
                return true;
            }
            inner.abort();
        }
        return false;
    }

    @Override
    public boolean ejectOutputs(SurgeryTableContext context, TransactionContext transaction)
    {
        var mobSlot = context.getStructure(MOB_SLOT);

        // Subvert the type system just this once™
        TableComponent<EntityVariant<?>> component = mobSlot.as();

        // This is bad, but replacing it with something sensible would complicate everything else.
        if (component instanceof SurgeryPlatformBlockEntity.Component platform)
        {
//            PlayerImplantManager manager = PlayerImplantManager.get(player);
//            manager.installImplant(moduleId);
            installer.install(platform.getEntity());
        }
        return true;
    }

    public static class Serializer implements MeatRecipeSerialiser<ImplantInstallRecipe>
    {
        @Override
        public ImplantInstallRecipe read(Identifier id, JsonObject json)
        {
            RecipeInput<?> input;
            if (JsonHelper.hasJsonObject(json, "input"))
            {
                input = RecipeInput.fromJson(JsonHelper.getObject(json, "input"));
            }
            else throw new JsonSyntaxException("Recipe input not found.");

            EntityImplantInstaller installer;
            if (JsonHelper.hasJsonObject(json, "implant_installer"))
            {
                Identifier installerId = Identifier.tryParse(JsonHelper.getString(JsonHelper.getObject(json, "implant_installer"), "id"));

                installer = EntityImplantInstaller.REGISTRY.get(installerId);
                if (installer == null) throw new JsonSyntaxException("Implant installer " + installerId + " does not exist.");
            }
            else throw new JsonSyntaxException("Implant installer not found.");

            return new ImplantInstallRecipe(id, input, installer);
        }

        @Override
        public ImplantInstallRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<?> input = RecipeInput.fromBuffer(buf);
            Identifier installerId = buf.readIdentifier();
            EntityImplantInstaller installer = EntityImplantInstaller.REGISTRY.get(installerId);
            return new ImplantInstallRecipe(id, input, installer);
        }

        @Override
        public void write(PacketByteBuf buf, ImplantInstallRecipe recipe)
        {
            recipe.resourceInput.write(buf);
            buf.writeIdentifier(EntityImplantInstaller.REGISTRY.getId(recipe.installer));
        }
    }
}
