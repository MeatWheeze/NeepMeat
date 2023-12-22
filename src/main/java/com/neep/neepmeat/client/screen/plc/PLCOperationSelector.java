package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.opcode.InstructionProvider;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;

import java.util.List;

public class PLCOperationSelector extends ScreenSubElement implements Drawable, Element, Selectable
{
    private final PLCProgramScreen parent;
    @Nullable
    protected InstructionProvider instructionProvider;

    public PLCOperationSelector(PLCProgramScreen parent)
    {
        this.parent = parent;
        this.elementWidth = 100;
        this.elementHeight = 200;
        this.x = 0;
        this.y = 0;
    }

    @Override
    public void setDimensions(int screenWidth, int screenHeight)
    {
        super.setDimensions(screenWidth, screenHeight);

    }

    @Override
    public void init()
    {
        super.init();

        this.x = screenWidth - elementWidth - 2;
        this.y = screenHeight - elementHeight - 2;

        addEntries();
    }

    protected void addEntries()
    {
        List<InstructionProvider> instructions = List.of(Instructions.COMBINE, Instructions.COMBINE, Instructions.END);

        int entryHeight = 20;
        int gap = 1;
        int entryStride = entryHeight + gap;

        int count = 0;
        for (var entry : instructions)
        {
            addDrawableChild(new OperationWidget(x + 3, y + 3 + (entryStride * count), elementWidth - 6, entryHeight, entry, this::onSelect));
            count++;
        }
    }

    protected void onSelect(InstructionProvider instructionProvider)
    {
        this.instructionProvider = instructionProvider;
        parent.updateInstruction(instructionProvider);
    }

    @Nullable
    public InstructionProvider getInstructionProvider()
    {
        return instructionProvider;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        int offset = 2;
        GUIUtil.renderBorder(matrices, x, y, elementWidth, elementHeight, Color.ofRGBA(255, 94, 33, 255).getColor(), 0);
        GUIUtil.renderBorder(matrices, x + 1, y + 1, elementWidth - 2, elementHeight - 2, Color.ofRGBA(255, 94, 33, 100).getColor(), 0);

//        MinecraftClient.getInstance().textRenderer.drawWithShadow()


        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public SelectionType getType()
    {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder)
    {

    }
}
