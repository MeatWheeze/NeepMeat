package com.neep.neepmeat.client.renderer;

import com.neep.neepmeat.item.MeatSteelArmourItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoArmorRenderer;

@Environment(value = EnvType.CLIENT)
public class MeatSteelArmourRenderer extends GeoArmorRenderer<MeatSteelArmourItem>
{
    public MeatSteelArmourRenderer(AnimatedGeoModel<MeatSteelArmourItem> modelProvider)
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
