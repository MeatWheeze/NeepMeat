package com.neep.neepmeat.api.live_machine;

public interface ComponentType<T extends LivingMachineComponent>
{
    int getBitIdx();

    class Simple<T extends LivingMachineComponent> implements ComponentType<T>
    {
        static int NEXT_ID = 0;

        private final int id;

        public static int size()
        {
            return NEXT_ID;
        }

        public Simple()
        {
            id = NEXT_ID++;
        }

        public int getBitIdx()
        {
            return id;
        }
    }
}
