package com.neep.neepmeat.transport.thread;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkRebuilding
{
    protected static ExecutorService EXECUTOR;

    public static void init()
    {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> EXECUTOR = Executors.newSingleThreadExecutor());
        ServerLifecycleEvents.SERVER_STOPPING.register((server) -> EXECUTOR.shutdown());
    }

    public static ExecutorService getExecutor()
    {
        return EXECUTOR;
    }

//    public void run(Runnable runnable)
//    {
//        try
//        {
//            runnable.run();
//        }
//        catch (Exception e)
//        {
//            System.out.println("Exception during pipe network rebuild");
//            e.printStackTrace();
//        }
//    }
//
//    public void run()
//    {
//        try
//        {
//            // Displaying the thread that is running
//            System.out.println(
//                    "Thread " + Thread.currentThread().getId()
//                            + " is running");
//        }
//        catch (Exception e)
//        {
//            System.out.println("Exception during pipe network rebuild");
//            e.printStackTrace();
//        }
//    }
}
