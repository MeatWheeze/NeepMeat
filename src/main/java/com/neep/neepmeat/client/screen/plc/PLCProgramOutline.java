package com.neep.neepmeat.client.screen.plc;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.api.plc.program.PlcProgram;
import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.network.plc.PLCSyncProgram;
import com.neep.neepmeat.plc.editor.CompiledProgramEditorState;
import com.neep.neepmeat.plc.instruction.Instruction;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib3.core.util.Color;

import java.util.List;
import java.util.stream.Collectors;

public class PLCProgramOutline extends ScreenSubElement implements Drawable, Element, Selectable
{
    private final PLCProgramScreen parent;
    private final CompiledProgramEditorState editor;

    private final List<InstructionWidget> instructions = Lists.newArrayList();

    public PLCProgramOutline(CompiledProgramEditorState editor, PLCProgramScreen parent)
    {
        this.editor = editor;
        this.parent = parent;
        this.elementWidth = 100;
        this.elementHeight = 200;
        this.x = 0;
        this.y = 0;

        editor.addListener(this::onProgramChanged);
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
        instructions.clear();
        PlcProgram program = editor.getProgram();

        int entryHeight = 11;
        int gap = 1;
        int entryStride = entryHeight + gap;

        int count = 0;
        for (int id = 0; id < program.size(); ++id)
        {
            Instruction instruction = program.get(id);
            var widget = new InstructionWidget(instruction.getProvider(), x + 2, y + 2 + count * entryStride, elementWidth - 4, entryHeight, id);
            addDrawableChild(widget);
            instructions.add(widget);
            count++;
        }
    }

    protected void onInstructionSelect(InstructionWidget widget)
    {
        parent.getScreenHandler().setSelectedInstruction(widget.id);
    }

    private int getSelected()
    {
        return parent.getScreenHandler().getSelectedInstruction();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE)
        {
            if (getSelected() != -1)
            {
                PLCSyncProgram.Client.sendDelete(getSelected(), parent.getScreenHandler().getPlc());
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        fill(matrices, x, y, x + elementWidth, y + elementHeight, 0x90000000);
        GUIUtil.renderBorder(matrices, x, y, elementWidth, elementHeight, Color.ofRGBA(255, 94, 33, 255).getColor(), 0);
        GUIUtil.renderBorder(matrices, x + 1, y + 1, elementWidth - 2, elementHeight - 2, Color.ofRGBA(255, 94, 33, 100).getColor(), 0);

        int counter = parent.getScreenHandler().getCounter();

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

    public void update()
    {
        clearAndInit();
    }

    private void onProgramChanged(PlcProgram program)
    {
        clearAndInit();
    }

    public class InstructionWidget implements Drawable, Element, Selectable
    {
        protected final InstructionProvider instructionProvider;

        protected final int x, y;
        protected final int width, height;
        protected final int id;
        public InstructionWidget(InstructionProvider instructionProvider, int x, int y, int width, int height, int id)
        {
            this.instructionProvider = instructionProvider;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.id = id;
        }

        protected boolean isMouseInside(double mouseX, double mouseY)
        {
            return mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            boolean valid =  mouseX >= this.x && mouseY >= this.y && mouseX < (this.x + this.width) && mouseY < (this.y + this.height);
            if (button == GLFW.GLFW_MOUSE_BUTTON_1 && valid)
            {
                client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.UI_BEEP, 1.0f));
                onInstructionSelect(this);
                return true;
            }
            return false;
        }

        private boolean selected()
        {
            return id == getSelected();
        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            boolean programCounterHere = parent.getScreenHandler().getCounter() == id;
            int col = Color.ofRGBA(255, 94, 33, 255).getColor();
//            int borderCol = Color.ofRGBA(255, selected() ? 150 : 94, 33, 255).getColor();
            int borderCol = !selected() ? PLCCols.BORDER.col :PLCCols.SELECTED.col;

            Text lineNumber = Text.literal(String.valueOf(id));

            textRenderer.draw(matrices, lineNumber, x + (programCounterHere ? 2 : 1), y + (height - textRenderer.fontHeight) / 2.0f + 1, borderCol);

            int textWidth = textRenderer.getWidth(lineNumber) + 2;
            GUIUtil.renderBorder(matrices, x + textWidth, y, width - textWidth, height - 1, borderCol, 0);

            textRenderer.draw(matrices, instructionProvider.getShortName(), x + textWidth + 2, (y + height) - textRenderer.fontHeight, col);

            if (isMouseInside(mouseX, mouseY))
            {
                renderTooltipText(matrices, List.of(instructionProvider.getShortName()), x + width + 3, y, PLCCols.BORDER.col);
            }

            if (programCounterHere)
            {
                GUIUtil.renderBorder(matrices, x, y, textWidth, height - 1, borderCol, 0);
            }
        }

        @Override
        public SelectionType getType()
        {
            return selected() ? SelectionType.FOCUSED : SelectionType.NONE;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }
    }

    private void renderTooltipText(MatrixStack matrices, List<Text> texts, int x, int y, int col)
    {
        renderTooltipComponents(matrices, texts.stream().map(t -> TooltipComponent.of(t.asOrderedText())).collect(Collectors.toList()), x, y, col);
    }

    private void renderTooltipComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, int col)
    {
        if (components.isEmpty())
        {
            return;
        }

        int maxWidth = 0;
        int maxHeight = components.size() == 1 ? -2 : 0;
        for (TooltipComponent tooltipComponent : components)
        {
            int componentWidth = tooltipComponent.getWidth(this.textRenderer);
            if (componentWidth > maxWidth)
            {
                maxWidth = componentWidth;
            }
            maxHeight += tooltipComponent.getHeight();
        }

        if (x + maxWidth > this.width)
        {
            x -= 28 + maxWidth;
        }

        if (y + maxHeight + 6 > this.height)
        {
            y = this.height - maxHeight - 6;
        }

        matrices.push();
        float prevZ = this.itemRenderer.zOffset;
        this.itemRenderer.zOffset = 400.0f;
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

//        int borderCol = Color.ofRGBA(255, 94, 33, 255).getColor();
        Screen.fill(matrices, x, y, x + maxWidth + 2, y + maxHeight + 2, 0x90000000);
        drawHorizontalLine(matrices, x, x + maxWidth + 2, y, col);
        drawHorizontalLine(matrices, x, x + maxWidth + 2, y + maxHeight + 2, col);
        drawVerticalLine(matrices, x + maxWidth + 2, y, y + maxHeight + 2, col);

        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0, 0.0, 400.0);

        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        int yAdvance = y + 2;
        for (int index = 0; index < components.size(); ++index)
        {
            TooltipComponent tooltipComponent2 = components.get(index);
            tooltipComponent2.drawText(this.textRenderer, x + 2, yAdvance, matrix4f, immediate);
            yAdvance += tooltipComponent2.getHeight() + (index == 0 ? 2 : 0);
        }

        immediate.draw();
        matrices.pop();
        yAdvance = y;
        for (int index = 0; index < components.size(); ++index)
        {
            TooltipComponent tooltipComponent2 = components.get(index);
            tooltipComponent2.drawItems(this.textRenderer, x, yAdvance, matrices, this.itemRenderer, 400);
            yAdvance += tooltipComponent2.getHeight() + (index == 0 ? 2 : 0);
        }
        this.itemRenderer.zOffset = prevZ;
    }
}
