/*
 * Decompiled with CFR 0.1.1 (FabricMC 57d88659).
 */
package com.neep.neepmeat.client.model.entity;

import com.neep.neepmeat.entity.keeper.KeeperEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.entity.mob.HostileEntity;

@Environment(value=EnvType.CLIENT)
public class KeeperEntityModel
extends BipedEntityModel<com.neep.neepmeat.entity.keeper.KeeperEntity>
{
    public KeeperEntityModel(ModelPart modelPart)
    {
        super(modelPart);
    }

    @Override
    public void setAngles(KeeperEntity hostileEntity, float f, float g, float h, float i, float j)
    {
        super.setAngles(hostileEntity, f, g, h, i, j);
        CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, this.isAttacking(hostileEntity), this.handSwingProgress, h);
    }

    public boolean isAttacking(KeeperEntity entity)
    {
        return (entity).isAttacking();
    }
}

