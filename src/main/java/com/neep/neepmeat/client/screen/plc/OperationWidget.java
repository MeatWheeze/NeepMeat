package com.neep.neepmeat.client.screen.plc;

import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.api.plc.instruction.InstructionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.core.util.Color;

import java.util.function.Consumer;

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
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        int col = Color.ofRGBA(255, 94, 33, 255).getColor();
        GUIUtil.renderBorder(matrices, x, y, width, height - 1, col, 0);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        textRenderer.draw(matrices, getMessage(), x + 2, (y + height) - textRenderer.fontHeight, col);
    }
}
