package com.neep.neepmeat.client.screen.living_machine;

import com.neep.neepmeat.api.live_machine.metrics.DataLog;
import com.neep.neepmeat.api.plc.PLCCols;
import com.neep.neepmeat.client.screen.tablet.GUIUtil;
import com.neep.neepmeat.client.screen.util.Border;
import com.neep.neepmeat.client.screen.util.Rectangle;
import com.neep.neepmeat.screen_handler.LivingMachineScreenHandler;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.List;

public class GraphPane extends LivingMachineScreen.PaneWidget
{
    private Rectangle windowBounds;
    private Rectangle plotBounds;

    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final LivingMachineScreenHandler handler;
    private DataLog.DataView dataView = DataLog.DataView.EMPTY;

    private final DecimalFormat yFormat = new DecimalFormat("###.#%");
    private final DecimalFormat timeFormat = new DecimalFormat("###.#");

    public GraphPane(LivingMachineScreenHandler handler)
    {
        this.handler = handler;
    }

    public void tick()
    {

    }

    @Override
    protected void init()
    {

    }

    @Override
    public void init(Rectangle parentSize)
    {
        super.init(parentSize);
        int scaleWidth = 20;
        this.windowBounds = new Rectangle.Immutable(bounds.x() + scaleWidth, bounds.y() + 10,
                bounds.w() - 3 * 10, bounds.h() - 2 * 10);
        int padding = 3;
        this.plotBounds = new Rectangle.Immutable(windowBounds.x() + padding, windowBounds.y() + padding, windowBounds.w() - 2 * padding,  windowBounds.h() - 2 * padding - 1);
        this.border = new Border(windowBounds, 0, () -> PLCCols.BORDER.col);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);

        long[] time = dataView.time();

        long period = dataView.capacity() * 40L;
        long timeStart = time.length > 0 ? time[0] : 0;
        long timeEnd = timeStart + period;

        long endTime = drawCurve(context, time, dataView.efficiency(), timeStart, timeEnd, PLCCols.SELECTED.col);
        drawCurve(context, time, dataView.health(), timeStart, timeEnd, PLCCols.BORDER.col);

        drawYScale(context, 0, 1, yFormat);
        drawTimeScale(context, 0, period, endTime, timeFormat);
        drawLegend(context, dataView.getLegend());
    }

    private long drawCurve(DrawContext context, long[] time, double[] yVals, long timeStart, long timeEnd, int col)
    {
        long period = timeEnd - timeStart;

        long highestTime = 0;
        for (int i = 0; i < time.length; ++i)
        {
            if (i % 2 == 0)
                continue;

            long t1 = time[i];

            if (t1 >= timeStart && t1 < timeEnd && i + 1 < time.length)
            {
                long t2 = time[i + 1];

                if (t2 <= timeStart || t2 > timeEnd)
                    break;

                highestTime = t2 - timeStart;

                float y1 = (float) yVals[i];
                float y2 = (float) yVals[i + 1];

//                float period = timeEnd - timeStart;
                float xStride = (float) plotBounds.w();
                float yStride = plotBounds.h();
                float t1Scaled = plotBounds.x() + (float) (t1 - timeStart) / period * xStride;
                float t2Scaled = plotBounds.x() + (float) (t2 - timeStart) / period * xStride;

                float y1Scaled = plotBounds.y() + plotBounds.h() - y1 * yStride;
                float y2Scaled = plotBounds.y() + plotBounds.h() - y2 * yStride;

                drawLine(context, t1Scaled, y1Scaled, t2Scaled, y2Scaled, 0.5f, col);
            }
        }
        return highestTime;
    }

    private void drawLegend(DrawContext context, List<ObjectIntPair<Text>> entries)
    {
//        float entryPadding = 5;
//        float totalWidth = 0;
//        for (var entry : entries)
//        {
//            totalWidth += textRenderer.getWidth(entry.key()) + entryPadding;
//        }

        MatrixStack matrices = context.getMatrices();
        float scale = 0.7f;
        for (int i = 0; i < entries.size(); ++i)
        {
            var entry = entries.get(i);

            Text name = entry.left();
            int col = entry.rightInt();

            int squareWidth = (int) (textRenderer.fontHeight * scale);
            float legendWidth = textRenderer.getWidth(name) * scale + squareWidth + 1;
            float y = (windowBounds.y() - textRenderer.fontHeight * scale);
            float x = plotBounds.x() + (i + 0.5f) * ((float) plotBounds.w() / (entries.size())) - legendWidth / 2;

            context.fill((int) x, (int) y, (int) x + squareWidth, (int) y + squareWidth, col);
            matrices.push();
            matrices.translate(x + squareWidth + 1, y, 0);
            matrices.scale(scale, scale, 1);
            GUIUtil.drawText(context, textRenderer, name, 0, 0, PLCCols.SELECTED.col, false);
            matrices.pop();
        }
    }

    private void drawTimeScale(DrawContext context, long lower, long upper, long zero, DecimalFormat format)
    {
        MatrixStack matrices = context.getMatrices();
        long divisions = 5;

        long period = upper - lower;
        long stride = period / divisions;

        for (int i = 0; i < divisions + 1; ++i)
        {
            long value = i * stride - zero;

            String formatted = formatTime(value);

            float y = plotBounds.y() + plotBounds.h();
            float x = plotBounds.x() + (float) (i * stride) / period * plotBounds.w();

            matrices.push();
            matrices.translate(x, (y + ((float) textRenderer.fontHeight / 2f)) + 2, 0);
            matrices.scale(0.7f, 0.7f, 1);
            GUIUtil.drawText(context, textRenderer, formatted, - textRenderer.getWidth(formatted) / 2f, 0,
                    PLCCols.SELECTED.col, false);
            matrices.pop();
            int tickLength = 6; // For some reason, this length has to be 1 higher than its horizontal counterpart.
            GUIUtil.drawVerticalLine1(context, (int) x, (int) y, (int) (y + tickLength), PLCCols.SELECTED.col);
        }
    }

    private static String formatTime(long ticks)
    {
        long secs = ticks / 20;

        Duration duration = Duration.ofSeconds(secs);
        if (duration.toHoursPart() != 0)
        {
            return duration.toHoursPart() + "h " + duration.toMinutesPart() + "m";
        }
        else if (duration.toMinutesPart() != 0)
        {
            return duration.toMinutesPart() + "m " + duration.toSecondsPart() + "s";
        }
        else
        {
            return duration.toSecondsPart() + "s";
        }
    }

    private void drawYScale(DrawContext context, float lower, float upper, DecimalFormat format)
    {
        MatrixStack matrices = context.getMatrices();
        int divisions = 10;

        float stride = (upper - lower) / divisions;

        for (int i = 0; i < divisions + 1; ++i)
        {
            float value = i * stride;

            String formatted = format.format(value);
            float y = (plotBounds.y() + plotBounds.h() - (i * stride) / (upper - lower) * plotBounds.h());
            matrices.push();
            matrices.translate(bounds.x(), (y - ((float) textRenderer.fontHeight / 2f)) + 2, 0);
            matrices.scale(0.7f, 0.7f, 1);
            GUIUtil.drawText(context, textRenderer, formatted, 0, 0,
                    PLCCols.SELECTED.col, false);
            matrices.pop();
            int tickLength = 5;
            GUIUtil.drawHorizontalLine1(context, plotBounds.x() - tickLength, plotBounds.x(), (int) y, PLCCols.SELECTED.col);
        }
    }

    public void update(PacketByteBuf buf)
    {
        dataView = DataLog.fromBuf(buf);
    }

    private void drawLine(DrawContext context, float x1, float y1, float x2, float y2, float radius, int col)
    {
        float z = 10;
        double angle = MathHelper.atan2((y2 - y1), (x2 - x1));
        float l = MathHelper.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));

        float f = (float) ColorHelper.Argb.getAlpha(col) / 255.0F;
        float g = (float) ColorHelper.Argb.getRed(col) / 255.0F;
        float h = (float) ColorHelper.Argb.getGreen(col) / 255.0F;
        float j = (float) ColorHelper.Argb.getBlue(col) / 255.0F;

        Matrix4f matrix4f = new Matrix4f()
                .identity()
                .translate(x1, y1, 0)
                .rotateZ((float) angle);

        VertexConsumer vertexConsumer = context.getVertexConsumers().getBuffer(RenderLayer.getGui());
        vertexConsumer.vertex(matrix4f, 0, - radius, z).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, 0, + radius, z).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, l, + radius, z).color(g, h, j, f).next();
        vertexConsumer.vertex(matrix4f, l, - radius, z).color(g, h, j, f).next();
    }
}
