package com.neep.neepmeat.machine.live_machine.process;

import com.neep.neepmeat.BalanceConstants;
import com.neep.neepmeat.api.live_machine.ComponentType;
import com.neep.neepmeat.api.live_machine.LivingMachineBlockEntity;
import com.neep.neepmeat.api.live_machine.Process;
import com.neep.neepmeat.init.NMFluids;
import com.neep.neepmeat.machine.live_machine.LivingMachineComponents;
import com.neep.neepmeat.machine.live_machine.block.entity.LargeCompressorBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.text.Text;

import java.util.List;

public class LargeCompressorProcess implements Process
{
    private static final List<ComponentType<?>> REQUIRED = List.of(
            LivingMachineComponents.MOTOR_PORT,
            LivingMachineComponents.LARGE_COMPRESSOR
    );

    @Override
    public void serverTick(LivingMachineBlockEntity be)
    {
        be.withComponents(LivingMachineComponents.MOTOR_PORT, LivingMachineComponents.LARGE_COMPRESSOR).ifPresent(r ->
        {
            var motors = r.t1();
            var compressors = r.t2();

            LargeCompressorBlockEntity compressor = compressors.iterator().next();

            float progressIncrement = be.getProgressIncrement();

            long droplets = (long) (progressIncrement * BalanceConstants.LARGE_COMPRESSOR_POWER_TO_DROPLETS);

            try (Transaction transaction = Transaction.openOuter())
            {
                compressor.getOutputStorage().insert(NMFluids.COMPRESSED_AIR.variant(), droplets, transaction);
                transaction.commit();
            }
        });
    }

    @Override
    public List<ComponentType<?>> getRequired()
    {
        return REQUIRED;
    }

    @Override
    public Text getName()
    {
        return Text.of("Large Compressor");
    }
}
