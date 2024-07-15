package com.neep.meatweapons.client.meatgun.animation;

import com.neep.meatweapons.component.MeatgunComponent;
import com.neep.meatweapons.component.MeatgunComponentImpl;
import com.neep.meatweapons.meatgun.module.HalberdModule;
import com.neep.meatweapons.meatgun.module.MeatgunModule;
import com.neep.meatweapons.meatgun.module.MeatgunModules;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class TridentStaffAnimation extends AnimatedAction<MeatgunComponent, TridentStaffAnimation> implements MeatgunAnimation
{
    private boolean started = false;

    private final Sequence<TridentStaffAnimation> up = new Sequence<>()
    {
        @Override
        public void tick(TridentStaffAnimation parent, MeatgunComponent component, int counter)
        {
            @Nullable MeatgunModule module = MeatgunComponentImpl.findRecursive(component.getRoot(), MeatgunModules.HALBERD);
            if (module instanceof HalberdModule halberd && halberd.triggerHeld())
            {
                started = true;
            }
            else if (started)
            {
                markFinished();
            }
        }

        @Override
        public void applyRender(MatrixStack matrices, int counter, float tickDelta)
        {
        }
    };

    @Override
    public void start()
    {
        super.start();
        started = false;
        setSequence(up);
    }

    @Override
    public boolean canStop()
    {
        return false;
    }
}
