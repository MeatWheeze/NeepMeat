package com.neep.neepmeat.api.live_machine.metrics;

import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.Arrays;

public class DataLog
{
    private long timeStart = -1;

    private final LongDataBuffer time;
    private final FloatDataBuffer efficiency;
    private final int sampleInterval;
    private final int capacity;

    public DataLog(int capacity, int sampleInterval)
    {
        this.capacity = capacity;
        this.sampleInterval = sampleInterval;

        this.time = new LongDataBuffer(capacity);
        this.efficiency = new FloatDataBuffer(capacity);
    }

    public void log(long time, LivingMachineBlockEntity be)
    {
        if (be.getAge() % sampleInterval != 0)
            return;

        if (timeStart == -1)
        {
            timeStart = time;
        }

        time -= timeStart;

        this.time.enqueueCircular(time);

        efficiency.enqueueCircular(be.getEfficiency());
    }

    public void write(PacketByteBuf buf)
    {
        buf.writeInt(capacity);
        buf.writeInt(efficiency.size());

        buf.writeLongArray(time.encode());
        buf.writeIntArray(efficiency.encode());
    }

    public static DataView fromBuf(PacketByteBuf buf)
    {
        int capacity = buf.readInt();
        int size = buf.readInt();

        long[] time = buf.readLongArray();

        // There's no FloatStream in Java for some mysterious reason
        double[] efficiency = Arrays.stream(buf.readIntArray()).mapToDouble(Float::intBitsToFloat).toArray();

        return new DataView(capacity, size, time, efficiency);
//        return DataView.EMPTY;
    }

    public static record DataView(int capacity, int size, long[] time, double[] efficiency)
    {
        public static final DataView EMPTY = new DataView(0, 0, new long[0], new double[0]);
    }
}
