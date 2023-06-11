package com.neep.neepmeat.recipe.surgery;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.neep.meatlib.recipe.MeatRecipeSerialiser;
import com.neep.meatlib.recipe.MeatRecipeType;
import com.neep.meatlib.recipe.ingredient.RecipeInput;
import com.neep.meatlib.recipe.ingredient.RecipeInputs;
import com.neep.meatlib.transfer.EntityVariant;
import com.neep.neepmeat.init.NMrecipeTypes;
import com.neep.neepmeat.machine.surgery_platform.SurgeryPlatformBlockEntity;
import com.neep.neepmeat.machine.surgical_controller.SurgeryTableContext;
import com.neep.neepmeat.player.upgrade.PlayerUpgradeManager;
import com.neep.neepmeat.player.upgrade.PlayerUpgradeRegistry;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.TransferVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class UpgradeInstallRecipe extends SurgeryRecipe
{
    protected final Identifier id;
    protected final RecipeInput<?> resourceInput;
    protected final Identifier moduleId;

    // Jank constants
    protected static final int MODULE_SLOT = 7;
    protected static final int MOB_SLOT = 4;

    public UpgradeInstallRecipe(Identifier id, RecipeInput<?> resourceInput, Identifier moduleId)
    {
        this.id = id;
        this.resourceInput = resourceInput;
        this.moduleId = moduleId;
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

                mobSlot.getStorage().iterable(transaction).forEach(storageView ->
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
        return NMrecipeTypes.UPGRADE_INSTALL;
    }

    @Override
    public MeatRecipeSerialiser<?> getSerialiser()
    {
        return NMrecipeTypes.UPGRADE_INSTALL_SERIALIZER;
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
        if (component instanceof SurgeryPlatformBlockEntity.Component platform && platform.getEntity() instanceof ServerPlayerEntity player)
        {
            PlayerUpgradeManager manager = PlayerUpgradeManager.get(player);
            manager.installUpgrade(moduleId);
        }
        return true;
    }

    public static class Serializer implements MeatRecipeSerialiser<UpgradeInstallRecipe>
    {
        @Override
        public UpgradeInstallRecipe read(Identifier id, JsonObject json)
        {
            RecipeInput<?> input;
            if (JsonHelper.hasJsonObject(json, "input"))
            {
                input = RecipeInput.fromJson(JsonHelper.getObject(json, "input"));
            }
            else throw new JsonSyntaxException("Recipe input not found.");

            Identifier moduleId;
            if (JsonHelper.hasJsonObject(json, "module"))
            {
                moduleId = Identifier.tryParse(JsonHelper.getString(JsonHelper.getObject(json, "module"), "id"));

                PlayerUpgradeRegistry.PlayerUpgradeConstructor constructor = PlayerUpgradeRegistry.REGISTRY.get(moduleId);
                if (constructor == null) throw new JsonSyntaxException("Module " + moduleId + " does not exist.");
            }
            else throw new JsonSyntaxException("Module not found.");

            return new UpgradeInstallRecipe(id, input, moduleId);
        }

        @Override
        public UpgradeInstallRecipe read(Identifier id, PacketByteBuf buf)
        {
            RecipeInput<?> input = RecipeInput.fromBuffer(buf);
            Identifier moduleId = buf.readIdentifier();
            return new UpgradeInstallRecipe(id, input, moduleId);
        }

        @Override
        public void write(PacketByteBuf buf, UpgradeInstallRecipe recipe)
        {
            recipe.resourceInput.write(buf);
            buf.writeIdentifier(recipe.moduleId);
        }
    }
}
