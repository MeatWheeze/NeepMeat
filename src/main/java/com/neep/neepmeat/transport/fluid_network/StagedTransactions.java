package com.neep.neepmeat.transport.fluid_network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class StagedTransactions
{
    protected static ExecutorService EXECUTOR;

    public static void init()
    {
        ServerTickEvents.END_SERVER_TICK.register(StagedTransactions::serverTick);
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> EXECUTOR = Executors.newSingleThreadExecutor());
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> EXECUTOR.shutdown());
    }

    public static Executor getExecutor()
    {
        return EXECUTOR;
    }

    public static Queue<StagedTransaction> TRANSACTIONS = new ConcurrentLinkedQueue<>();

    public static void serverTick(MinecraftServer server)
    {
        if (!server.isOnThread())
            throw new IllegalThreadStateException("Staged transactions must only be executed on the main server thread.");

        while (!TRANSACTIONS.isEmpty())
        {
            try (Transaction transaction = Transaction.openOuter())
            {
                StagedTransaction entry = TRANSACTIONS.poll();
                long transferred = entry.move(transaction);
                transaction.commit();
            }
            catch (Exception e)
            {
                // Occasionally a spooky ClassCastException is thrown when hot-swapping
                e.printStackTrace();
            }
        }
    }

    public static void queue(StagedTransaction transaction)
    {
        TRANSACTIONS.add(transaction);
    }

    public interface StagedTransaction
    {
        long move(Transaction transaction);

        static <T> StagedTransaction of(@Nullable Storage<T> from, @Nullable Storage<T> to, Predicate<T> filter, long maxAmount)
        {
            return transaction -> StorageUtil.move(from, to, filter, maxAmount, transaction);
        }
    }
}
