package com.neep.neepmeat.compat.emi.recipe;

import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.recipe.ManufactureStep;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.compat.emi.NMEmiPlugin;
import com.neep.neepmeat.plc.recipe.CombineStep;
import com.neep.neepmeat.plc.recipe.ImplantStep;
import com.neep.neepmeat.plc.recipe.InjectStep;
import com.neep.neepmeat.plc.recipe.ItemManufactureRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ManufactureEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    private final Item base;
    private final List<ManufactureStep<?>> steps;

    public ManufactureEmiRecipe(ItemManufactureRecipe recipe) {
        this.base = (Item) recipe.getBase();
        this.steps = recipe.getSteps();

        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(Ingredient.ofItems(base)));
        this.output = List.of(EmiStack.of(recipe.getOutput().resource(), recipe.getOutput().minAmount()));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return NMEmiPlugin.SURGERY;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 160;
    }

    @Override
    public int getDisplayHeight() {
        return 150;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        int startX = 5;
        int startY = 5;

        widgets.add(new OutlineWidget(new Bounds(0, 0, getDisplayWidth(), getDisplayHeight())));

        var widgetBase = new LabelledSlot(startX, startY, Text.of("Base: "), EmiStack.of(base), widgets);
        widgets.add(widgetBase);

        var widgetOutput = new LabelledSlot(startX + 20 + widgetBase.width(), startY, Text.of("Output: "), output.get(0), widgets, this);
        widgets.add(widgetOutput);

        int entryX = startX + 1;
        int entryY = startY + 22;
        for (var step : steps) {
            var widget = new EntryWidget(entryX, entryY, step, getDisplayWidth() - 20, widgets);
            widgets.add(widget);
            entryY += widget.height() + 2;
        }
    }

    public static int borderCol()
    {
        return PLCCols.BORDER.col;
    }

    static class LabelledSlot extends Widget {
        private final int originX;
        private final int originY;
        private final Text name;
        private final int slotOriginX;
        private final int slotOriginY;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        public LabelledSlot(int originX, int originY, Text name, EmiStack stack, WidgetHolder widgets) {
            this(originX, originY, name, stack, widgets, null);
        }

        public LabelledSlot(int originX, int originY, Text name, EmiStack stack, WidgetHolder widgets, EmiRecipe recipe) {
            this.name = name;
            this.originX = originX;
            this.originY = originY;
            this.slotOriginX = originX + textRenderer.getWidth(name) + 2;
            this.slotOriginY = originY;

            widgets.addSlot(stack, slotOriginX, slotOriginY).drawBack(false).recipeContext(recipe);
        }

        public int height() {
            return Math.max(textRenderer.fontHeight + 3, 19);
        }

        public int width() {
            return textRenderer.getWidth(name) + 2 + 20;
        }

        @Override
        public Bounds getBounds() {
            return new Bounds(originX, originY, width(), height());
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            textRenderer.drawWithShadow(matrices, name, originX, originY, borderCol());
            GUIUtil.renderBorder(matrices, slotOriginX, slotOriginY, 17, 17, borderCol(), 0);
            GUIUtil.renderBorder(matrices, slotOriginX + 1, slotOriginY + 1, 15, 15, PLCCols.TRANSPARENT.col, 0);
        }
    }

    public static class EntryWidget extends Widget {
        private final int originX;
        private final int originY;
        private final ManufactureStep<?> step;
        private final Text name;
        private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        private final int width;

        public EntryWidget(int originX, int originY, ManufactureStep<?> step, int width, WidgetHolder widgets) {
            this.originX = originX;
            this.originY = originY;
            this.step = step;
            this.name = step.getName();
            this.width = width;

            drawThing(originX + width() - 14, originY, step, widgets);
        }

        public int height() {
            return Math.max(textRenderer.fontHeight + 3, 19);
        }

        public int width() {
            return width;
        }

        @Override
        public Bounds getBounds() {
            return new Bounds(originX, originY, width(), height());
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            int x = originX + 2;
            int y = originY + 2;

            textRenderer.drawWithShadow(matrices, name, x, y, borderCol());
            GUIUtil.renderBorder(matrices, originX, originY, width() + 3, height(), borderCol(), 0);
        }
    }

    static void drawThing(int x, int y, ManufactureStep<?> step, WidgetHolder widgets) {
        if (step instanceof CombineStep combineStep)
        {
            widgets.addSlot(EmiStack.of(combineStep.getItem()), x - 1, y + 1).drawBack(false);
        }
        else if (step instanceof InjectStep injectStep)
        {
            widgets.addSlot(EmiStack.of(injectStep.getFluid()), x - 1, y + 1).drawBack(false);
        }
        else if (step instanceof ImplantStep implantStep)
        {
            widgets.addSlot(EmiStack.of(implantStep.getItem()), x - 1, y + 1).drawBack(false);
        }
        else
            widgets.addSlot(EmiStack.EMPTY, x, y + 2).drawBack(false);
    }

    static class OutlineWidget extends Widget {
        private final Bounds bounds;

        public OutlineWidget(Bounds bounds) {
            this.bounds = bounds;
        }

        @Override
        public Bounds getBounds() {
            return this.bounds;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            DrawableHelper.fill(matrices, bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height(), 0xFF000000);
            GUIUtil.renderBorder(matrices, bounds.x(), bounds.y(), bounds.width(), bounds.height(), borderCol(), 0);
            GUIUtil.renderBorder(matrices, bounds.x() + 1, bounds.y() + 1, bounds.width() - 2, bounds.height() - 2, PLCCols.TRANSPARENT.col, 0);
        }
    }
}
