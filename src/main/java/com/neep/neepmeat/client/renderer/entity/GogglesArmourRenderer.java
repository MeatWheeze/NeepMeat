package com.neep.neepmeat.client.renderer.entity;

import com.neep.neepmeat.item.GogglesItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Environment(value = EnvType.CLIENT)
public class GogglesArmourRenderer extends GeoArmorRenderer<GogglesItem>
{
    public GogglesArmourRenderer(AnimatedGeoModel<GogglesItem> modelProvider)
    {
        super(modelProvider);

        this.headBone = "armorHead";
        this.bodyBone = "armorBody";
        this.rightArmBone = "armorRightArm";
        this.leftArmBone = "armorLeftArm";
        this.rightLegBone = "armorRightLeg";
        this.leftLegBone = "armorLeftLeg";
        this.rightBootBone = "armorRightBoot";
        this.leftBootBone = "armorLeftBoot";
    }
}
