package com.neep.neepmeat.client.screen.button;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;

public class NMToggleButtonWidget extends NMButtonWidget
{
    private final BooleanSupplier toggled;
    private final ToggleAction onToggle;

    public NMToggleButtonWidget(int x, int y, int width, int height, BooleanSupplier toggled, Text message, ToggleAction onToggle)
    {
        super(x, y, width, height, message, b -> {});
        this.toggled = toggled;
        this.onToggle = onToggle;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        super.renderButton(matrices, mouseX, mouseY, delta);
    }

    public boolean isToggled()
    {
        return toggled.getAsBoolean();
    }

    @Override
    public boolean borderActive()
    {
        return isToggled();
    }

    @Override
    protected boolean textActive()
    {
        return isHovered();
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        super.onClick(mouseX, mouseY);
        onToggle.toggle(this, !toggled.getAsBoolean());
    }

    @FunctionalInterface
    public interface ToggleAction
    {
        void toggle(NMToggleButtonWidget button, boolean toggled);
    }
}
