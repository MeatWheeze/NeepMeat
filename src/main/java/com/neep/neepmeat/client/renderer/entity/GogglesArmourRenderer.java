package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.item.GogglesItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

@Environment(value = EnvType.CLIENT)
public class GogglesArmourRenderer extends GeoArmorRenderer<GogglesItem>
{
    public GogglesArmourRenderer(GeoModel<GogglesItem> modelProvider)
    {
        super(modelProvider);

        this.head = modelProvider.getBone("armorHead").orElse(null);
        this.body = modelProvider.getBone("armorBody").orElse(null);
        this.rightArm = modelProvider.getBone("armorRightArm").orElse(null);
        this.leftArm = modelProvider.getBone("armorLeftArm").orElse(null);
        this.rightLeg = modelProvider.getBone("armorRightLeg").orElse(null);
        this.leftLeg = modelProvider.getBone("armorLeftLeg").orElse(null);
        this.rightBoot = modelProvider.getBone("armorRightBoot").orElse(null);
        this.leftBoot = modelProvider.getBone("armorLeftBoot").orElse(null);
    }
}
