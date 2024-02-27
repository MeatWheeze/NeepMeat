package com.neep.neepmeat.network.plc;

import com.neep.meatlib.network.PacketBufUtil;
import com.neep.neepmeat.NeepMeat;
import com.neep.neepmeat.api.plc.program.PLCProgram;
import com.neep.neepmeat.client.screen.plc.PLCProgramScreen;
import com.neep.neepmeat.client.screen.plc.RecordMode;
import com.neep.neepmeat.neepasm.compiler.variable.VariableStack;
import com.neep.neepmeat.plc.Instructions;
import com.neep.neepmeat.plc.block.entity.PLCBlockEntity;
import com.neep.neepmeat.plc.instruction.Argument;
import com.neep.neepmeat.plc.instruction.InstructionProvider;
import com.neep.neepmeat.plc.screen.PLCScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class PLCSyncThings
{
    public static final Identifier ID = new Identifier(NeepMeat.NAMESPACE, "plc_sync_program");

    public static void sendProgram(ServerPlayerEntity player, PLCBlockEntity be, PLCProgram program)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Action.PROGRAM.ordinal());
        putPlc(buf, be);

        buf.writeNbt(program.writeNbt(new NbtCompound()));

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void sendCompileStatus(ServerPlayerEntity player, String message, boolean success, int line)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Action.COMPILE.ordinal());
        buf.writeString(message);
        buf.writeBoolean(success);
        buf.writeVarInt(line);

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void sendStack(ServerPlayerEntity player, VariableStack stack)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(Action.STACK.ordinal());
        buf.writeNbt(stack.writeNbt(new NbtCompound()));

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(ID, PLCSyncThings::apply);
    }

    private static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender)
    {
        Action action = Action.values()[buf.readInt()];

        PacketByteBuf copy = PacketByteBufs.copy(buf.copy());

        server.execute(() ->
        {
            switch (action)
            {
                case ARGUMENT -> applyArgument(copy, player.getWorld());
                case OPERATION -> applyInstruction(copy, player.getWorld());
                case PAUSE -> applyPause(copy, player.getWorld());
                case RUN -> applyRun(copy, player.getWorld());
                case STOP -> applyStop(copy, player.getWorld());
                case MODE -> applyMode(copy, player.getWorld());
                case TEXT -> applyText(copy, player.getWorld());
                case COMPILE -> applyCompile(copy, player.getWorld());
            }
        });
    }

    private static void applyCompile(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        plc.getProgramEditor().compile();
    }

    private static void applyText(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        plc.getProgramEditor().setProgramSource(buf.readString(10000));
    }

    private static void applyMode(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        plc.setMode(RecordMode.values()[buf.readInt()]);
    }

    private static void applyRun(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);
        plc.runProgram(plc.getProgramEditor().getProgram());
    }

    private static void applyStop(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);
        plc.hardStop();
    }

    private static void applyPause(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);
        plc.pause();
    }

//    private static void applyDelete(PacketByteBuf buf, World world)
//    {
//        PLCBlockEntity plc = getPlc(buf, world);
//        plc.getEditor().delete(buf.readInt());
//    }

    private static void applyInstruction(PacketByteBuf buf, World world)
    {
        PLCBlockEntity plc = getPlc(buf, world);

        Identifier id = Identifier.tryParse(buf.readString());

        InstructionProvider provider = Instructions.REGISTRY.get(id);
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
        RUN,
        PAUSE,
        STOP,
        MODE,
        TEXT,
        COMPILE,
        STACK;
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
                        case COMPILE -> Client.applyCompileMessage(copy, client);
                        case STACK -> Client.applyStack(copy, client);
                    }
                });
            });
        }

        private static void applyStack(PacketByteBuf buf, MinecraftClient client)
        {
            NbtCompound nbt = buf.readNbt();

            if (client.player.currentScreenHandler instanceof PLCScreenHandler handler)
            {
                handler.getPlc().updateVariableStack(nbt);
            }
        }

        private static void applyCompileMessage(PacketByteBuf buf, MinecraftClient client)
        {
            String message = buf.readString();
            boolean success = buf.readBoolean();
            int line = buf.readVarInt();

            if (client.currentScreen instanceof PLCProgramScreen programScreen)
            {
                programScreen.getEditor().setCompileMessage(message, success, line);
            }
        }

        public static void sendArgument(Argument argument, PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();

            buf.writeInt(Action.ARGUMENT.ordinal());
            putPlc(buf, plc);

            argument.writeBuf(buf);

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

        public static void sendRun(PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.RUN.ordinal());
            putPlc(buf, plc);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendPause(PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.PAUSE.ordinal());
            putPlc(buf, plc);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendStop(PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.STOP.ordinal());
            putPlc(buf, plc);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendMode(PLCBlockEntity plc, RecordMode mode)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.MODE.ordinal());
            putPlc(buf, plc);

            buf.writeInt(mode.ordinal());

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendText(PLCBlockEntity plc, String text)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.TEXT.ordinal());
            putPlc(buf, plc);
            buf.writeString(text);

            ClientPlayNetworking.send(ID, buf);
        }

        public static void sendCompile(PLCBlockEntity plc)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(Action.COMPILE.ordinal());
            putPlc(buf, plc);

            ClientPlayNetworking.send(ID, buf);
        }
    }
}
