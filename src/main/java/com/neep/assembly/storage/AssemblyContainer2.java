//package com.neep.assembly.storage;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtElement;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.nbt.NbtLongArray;
//import net.minecraft.util.math.BlockPos;
//
//import java.util.HashMap;
//
//public class AssemblyContainer2 extends HashMap<Long, BlockState>
//{
//    public BlockState set(int x, int y, int z, BlockState state)
//    {
//        return put(new BlockPos(x, y, z).asLong(), state);
//    }
//
//    public BlockState set(BlockPos pos, BlockState state)
//    {
//        return this.put(pos.asLong(), state);
//    }
//
//    public BlockState get(int x, int y, int z)
//    {
//        return this.get(new BlockPos(x, y, z).asLong());
//    }
//
//    public BlockState get(BlockPos pos)
//    {
//        return this.get(pos.asLong());
//    }
//
//    public NbtCompound writeNbt(NbtCompound nbt, String namespace)
//    {
//        NbtCompound parent = new NbtCompound();
//        NbtList values = new NbtList();
//        NbtLongArray keys = new NbtLongArray(new long[this.size()]);
//        int i = 0;
//        for (Entry<Long, BlockState> entry : this.entrySet())
//        {
//            keys.setElement(i, NbtElement.)
//            ++i;
//        }
//        return nbt;
//    }
//
//    public void readNbt()
//    {
//    }
//}
