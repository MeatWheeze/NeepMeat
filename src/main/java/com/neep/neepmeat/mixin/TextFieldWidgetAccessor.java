package com.neep.neepmeat.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Predicate;

@Mixin(TextFieldWidget.class)
public interface TextFieldWidgetAccessor
{
    @Accessor
    boolean getEditable();

    @Accessor
    int getSelectionStart();

    @Accessor
    int getSelectionEnd();

    @Accessor
    int getFirstCharacterIndex();

    @Accessor
    String getText();

    @Accessor
    int getFocusedTicks();

    @Accessor
    Predicate<String> getTextPredicate();

    @Accessor("text")
    void setFieldText(String text);

    @Invoker
    int callGetMaxLength();

    @Invoker
    void callDrawSelectionHighlight(DrawContext context, int x1, int y1, int x2, int y2);

}
