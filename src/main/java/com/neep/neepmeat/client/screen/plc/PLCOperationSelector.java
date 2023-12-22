package com.neep.neepmeat.client.screen.plc;

import com.mojang.blaze3d.systems.RenderSystem;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.client.screen.ScreenSubElement;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.init.NMSounds;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;

import java.util.List;
import java.util.function.Consumer;

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
        List<? extends InstructionProvider> instructions;
//        if (parent.mode == PLCProgramScreen.RecordMode.IMMEDIATE)
//        {
//            instructions = Instructions.IMMEDIATE.stream().toList();
//        }
//        else
//        {
//        }
        instructions = Instructions.REGISTRY.stream().toList();

        int entryHeight = 20;
        int gap = 1;
        int entryStride = entryHeight + gap;

        int count = 0;
        for (var entry : instructions)
        {
            addDrawableChild(new OperationWidget(x + 3, entryStride + y + 3 + (entryStride * count), elementWidth - 6, entryHeight, entry, this::onSelect));
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

    public static Identifier STRIPES = new Identifier(NeepMeat.NAMESPACE, "textures/gui/widget/stripes.png");

    protected void drawStripes(MatrixStack matrixStack, int x, int y, int width, int height)
    {
        int maxChunk = 32; // The pattern repeats horizontally after 30 pixels.
        int whole = MathHelper.floorDiv(width, maxChunk);
        int remainder = width % maxChunk;
        int xi = 0;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, STRIPES);
        for (int i = 0; i < whole; ++i)
        {
            drawTexture(matrixStack, x + xi, y, 0, 0, maxChunk, height, 32, 32);
            xi += maxChunk;
        }
        if (remainder != 0)
        {
            drawTexture(matrixStack, x + xi, y, 0, 0, remainder, height, 32, 32);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        fill(matrices, x, y, x + elementWidth, y + elementHeight, 0x90000000);
        int borderCol = Color.ofRGBA(255, 94, 33, 255).getColor();
        int transparent =  Color.ofRGBA(255, 94, 33, 100).getColor();
        GUIUtil.renderBorder(matrices, x, y, elementWidth, elementHeight, borderCol, 0);
        GUIUtil.renderBorder(matrices, x + 1, y + 1, elementWidth - 2, elementHeight - 2, transparent, 0);

        drawHorizontalLine(matrices, x + 3, x + elementWidth - 3, y + 3, borderCol);
        drawStripes(matrices, x + 3, y + 5, elementWidth - 5, 2);
        drawStripes(matrices, x + 3, y + 16, elementWidth - 5, 2);
        drawHorizontalLine(matrices, x + 3, x + elementWidth - 3, y + 19, borderCol);
        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, Text.of("INSTRUCTIONS").asOrderedText(), x + elementWidth / 2, y + 8, borderCol);

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

    public class OperationWidget extends ClickableWidget
    {
        private final InstructionProvider provider;
        private final Consumer<InstructionProvider> action;

        public OperationWidget(int x, int y, int width, int height, InstructionProvider provider, Consumer<InstructionProvider> action)
        {
            super(x, y, width, height, provider.getShortName());
            this.provider = provider;
            this.action = action;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder)
        {

        }

        @Override
        public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            super.render(matrices, mouseX, mouseY, delta);
        }

        @Override
        public void onClick(double mouseX, double mouseY)
        {
            super.onClick(mouseX, mouseY);
            action.accept(provider);
        }

        @Override
        public void playDownSound(SoundManager soundManager)
        {
//            soundManager.play(PositionedSoundInstance.master(NMSounds.PLC_SELECT_BLOCK, 1.0f, 1));
            client.getSoundManager().play(PositionedSoundInstance.master(NMSounds.PLC_SELECT_BLOCK, 1.0f));
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
        {
            int col = PLCOperationSelector.this.instructionProvider == provider ? PLCProgramScreen.selectedCol() : PLCProgramScreen.borderCol();
            GUIUtil.renderBorder(matrices, x, y, width, height - 1, col, 0);

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            textRenderer.draw(matrices, getMessage(), x + 2, (y + height) - textRenderer.fontHeight, col);
        }
    }
}
