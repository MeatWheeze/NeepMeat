package com.neep.neepmeat.network.plc;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.PLCBlockEntity;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.ImmediateInstructionProvider;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.program.PlcProgram;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PLCSyncProgram
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_sync_program");

    public static void sendProgram(ServerPlayerEntity player, PLCBlockEntity be, PlcProgram program)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Action.PROGRAM.ordinal());
        putPlc(buf, be);

        buf.writeNbt(program.writeNbt(new NbtCompound()));

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, PLCSyncProgram::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        Action action = Action.values()[buf.readInt()];

        PacketByteBuf copy = PacketByteBufs.copy(buf.copy());

        server.execute(() ->
        {
            switch (action)
            {
                case ARGUMENT -> applyArgument(copy, player.world);
                case OPERATION -> applyInstruction(copy, player.world);
                case OPERATION_IMMEDIATE -> applyInstructionImmediate(copy, player.world);
                case DELETE -> applyDelete(copy, player.world);
                case RUN -> applyRun(copy, player.world);
            }
        });
    }

    private static void applyRun(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);
        plc.runProgram(plc.getEditProgram());
    }

    private static void applyDelete(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);
        plc.getEditor().delete(buf.readInt());
    }

    private static void applyInstruction(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        Identifier id = Identifier.tryParse(buf.readString());

        InstructionProvider provider = Instructions.REGISTRY.get(id);
        if (provider != null)
        {
            plc.getEditor().setInstructionBuilder(provider);
        }
    }

    private static void applyInstructionImmediate(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        Identifier id = Identifier.tryParse(buf.readString());

        InstructionProvider provider = Instructions.IMMEDIATE.get(id);
        if (provider != null)
        {
            plc.getState().setInstructionBuilder(provider);
        }
    }

    private static void applyArgument(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        Argument argument = Argument.fromBuf(buf);
        plc.getState().argument(argument);
    }

    private static void putPlc(PacketByteBuf buf, PLCBlockEntity be)
    {
        PacketBufUtil.writeBlockPos(buf, be.getPos());
    }

    private static PLCBlockEntity getPlc(PacketByteBuf buf, World world)
    {
        return (PLCBlockEntity) world.getBlockEntity(PacketBufUtil.readBlockPos(buf));
    }

    enum Action
    {
        ARGUMENT,
        OPERATION,
        OPERATION_IMMEDIATE,
        PROGRAM,
        DELETE,
        RUN
    }

    @Environment(value = EnvType.CLIENT)
    public static class Client
    {
        public static void registerReceiver()
        {
            ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) ->
            {
                Action action = Action.values()[buf.readInt()];

                PacketByteBuf copy = PacketByteBufs.copy(buf.copy());

                client.execute(() ->
                {
                    switch (action)
                    {
                        case PROGRAM -> applySyncProgram(copy, client.world);
                    }
                });
            });
        }

        private static void applySyncProgram(PacketByteBuf buf, World world)
        {
            PLCBlockEntity plc = getPlc(buf, world);

            NbtCompound nbt = buf.readNbt();

            plc.getEditor().receiveProgram(nbt);
        }

        public static void sendArgument(Argument argument, PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeInt(Action.ARGUMENT.ordinal());
            putPlc(buf, plc);

            argument.writeBuf(buf);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void switchOperationImmediate(ImmediateInstructionProvider provider, PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeInt(Action.OPERATION_IMMEDIATE.ordinal());
            putPlc(buf, plc);

            String id = Instructions.IMMEDIATE.getId(provider).toString();
            buf.writeString(id);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void switchOperation(InstructionProvider provider, PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeInt(Action.OPERATION.ordinal());
            putPlc(buf, plc);

            String id = Instructions.REGISTRY.getId(provider).toString();
            buf.writeString(id);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendDelete(int index, PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.DELETE.ordinal());
            putPlc(buf, plc);

            buf.writeInt(index);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendRun(PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.RUN.ordinal());
            putPlc(buf, plc);

            ClientPlayNetworking.send(ID, buf);
        }
    }
}
