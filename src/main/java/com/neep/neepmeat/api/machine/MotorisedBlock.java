package com.neep.neepmeat.api.machine;

import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.processing.PowerUtils;
import com.neep.neepmeat.machine.motor.MotorEntity;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static com.neep.neepmeat.api.processing.PowerUtils.*;

public interface MotorisedBlock
{
    BlockApiLookup<MotorisedBlock, Void> LOOKUP = BlockApiLookup.get(
            new Identifier(NeepMeat.NAMESPACE, "motorised_block"),
            MotorisedBlock.class, Void.class
    );

    boolean tick(MotorEntity motor);

    void setInputPower(float power);

    default boolean canConnect(Direction direction)
    {
        return true;
    }

    default void onMotorRemoved() {};

    default float getLoadTorque() { return PowerUtils.MOTOR_TORQUE_LOSS; }

    interface DiagnosticsProvider
    {
        Diagnostics get();

        BlockApiLookup<DiagnosticsProvider, Void> LOOKUP = BlockApiLookup.get(
                new Identifier(NeepMeat.NAMESPACE, "machine_diagnostics"),
                DiagnosticsProvider.class, Void.class
        );

        static void init()
        {
            LOOKUP.registerFallback((world, pos, state, blockEntity, context) ->
            {
                if (blockEntity instanceof DiagnosticsProvider d)
                    return d;

                return null;
            });
        }
    }

    class Diagnostics
    {
        protected NbtCompound nbt = new NbtCompound();

        public static Diagnostics insufficientPower(boolean problem, float power, float minPower)
        {
            Text title = Text.translatable("message.neepmeat.insufficient_power");
            Text message = Text.translatable("message.neepmeat.insufficient_power_2",
                    Text.literal(POWER_FORMAT.format(perUnitToAbsolute(power))).append(POWER_UNIT),
                    Text.literal(POWER_FORMAT.format(perUnitToAbsolute(minPower))).append(POWER_UNIT)
            );
            return new Diagnostics(problem, title, message);
        }

        public Diagnostics(boolean problem, Text title, Text message)
        {
            nbt.putBoolean("problem", problem);
            nbt.putString("title", title.getString());
            nbt.putString("message", message.getString());
        }

        public Diagnostics(NbtCompound nbt)
        {
            this.nbt = nbt;
        }

        public boolean problem()
        {
            return nbt.getBoolean("problem");
        }

        public Text title()
        {
            return Text.of(nbt.getString("title"));
        }

        public Text message()
        {
            return Text.of(nbt.getString("message"));
        }
        public NbtCompound nbt()
        {
            return nbt;
        }
    }
}
