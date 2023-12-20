# NEEPMeat Development Log

2023-03-27
I am currently in the middle of completely overhauling the pipe plant (yet again). This time I am going for a more traditional plant where fluid is temporarily stored in buffers in the pipes, rather than being transported directly from storage to storage. The main driver for this decision was the performance issues with the storage-to-storage method due to its quadratically increasing algorithmic complexity.

So far, I have:

- Added a method of converting pipe blocks into a simplified graph.
- Added a fairly robust (as robust as I could manage) plant for moving fluid between vertices.

Advantages and disadvantages of the storage-to-storage method:

- There is no danger of fluid getting lost in pipes.
- O(n$^2$) where n is the number of nodes.
- Instability arises when multi-threading when a large network is ticking.
- Causes memory to fill up faster leading to more frequent GC calls (not entirely sure why)

Advantages and disadvantages of the vertex method:

- Fluid takes some time to fill the network.
- Multiple fluid types in the same network can cause blockages.
- Instability can arise for unknown reasons.
- O(n) where n is the number of pipes, performance much better for systems with many nodes

I have now implemented a method of modifying 'head' to simulate suction.

2023-04-05
The agony of pipe network serialisation! I have no idea what's going on. I'm trying to re-implement persistent pipe networks, but no approaches seem to be working. Last time, I spent one day on this. Now I've already spent two.

The old approach used FluidNodes to save the network when their parent block entity was unloaded. To distinguish between block entity removal and unloading I used World::isPosLoaded. I tried this approach again, but it turns out that block entities can be unloaded while their chunk is still loaded. I have no idea why.
