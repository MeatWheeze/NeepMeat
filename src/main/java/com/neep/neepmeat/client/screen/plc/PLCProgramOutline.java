package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.opcode.InstructionProvider;
import com.neep.neepmeat.plc.program.CombineInstruction;
import com.neep.neepmeat.plc.program.PLCInstruction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;

import java.util.List;

public class PLCProgramOutline extends ScreenSubElement implements Drawable, Element, Selectable
{
    private final PLCProgramScreen parent;
    @Nullable
    protected InstructionProvider instructionProvider;

    private int selectedInstruction = 0;

    public PLCProgramOutline(PLCProgramScreen parent)
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

        this.x = 2;
        this.y = 2;
        this.elementWidth = 100;
        this.elementHeight = screenHeight - 4;

        addEntries();
    }

    protected void addEntries()
    {
        List<PLCInstruction> instructions = List.of(new CombineInstruction(null, null, null), PLCInstruction.end());

        int entryHeight = 20;
        int gap = 1;
        int entryStride = entryHeight + gap;

        int count = 0;
        for (int id = 0; id < instructions.size(); ++id)
        {
            PLCInstruction instruction = instructions.get(id);
            addDrawable(new InstructionWidget(instruction.getProvider(), x + 2, y + 2 + count * entryStride, elementWidth - 4, entryHeight, id, false));
            count++;
        }
    }

//    protected void onSelect(InstructionProvider instructionProvider)
//    {
//        this.instructionProvider = instructionProvider;
//        parent.updateInstruction(instructionProvider);
//    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        fill(matrices, x, y, x + elementWidth, y + elementHeight, 0x90000000);
        GUIUtil.renderBorder(matrices, x, y, elementWidth, elementHeight, Color.ofRGBA(255, 94, 33, 255).getColor(), 0);
        GUIUtil.renderBorder(matrices, x + 1, y + 1, elementWidth - 2, elementHeight - 2, Color.ofRGBA(255, 94, 33, 100).getColor(), 0);

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

    public static class InstructionWidget implements Drawable
    {
        protected final InstructionProvider instructionProvider;
        protected final int x, y;
        protected final int width, height;
        protected final int id;

        public InstructionWidget(InstructionProvider instructionProvider, int x, int y, int width, int height, int id, boolean selected)
        {
            this.instructionProvider = instructionProvider;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.id = id;
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int col = Color.ofRGBA(255, 94, 33, 255).getColor();
            GUIUtil.renderBorder(matrices, x, y, width, height - 1, col, 0);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, instructionProvider.getShortName(), x + 2, (y + height) - textRenderer.fontHeight, col);
        }
    }
}
