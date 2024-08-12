package com.neep.meatlib.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockAccessor;
import net.fabricmc.fabric.mixin.object.builder.AbstractBlockSettingsAccessor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class MeatlibBlockSettings extends FabricBlockSettings
{
    //    public final List<TagKey<Block>> tags = new ArrayList<>(Collections.singleton(BlockTags.PICKAXE_MINEABLE));
    public static final Set<TagKey<Block>> DEFAULT_TAGS = Set.of(BlockTags.PICKAXE_MINEABLE);

    private Set<TagKey<Block>> tags = DEFAULT_TAGS;
    @Nullable private Function<Block, ItemConvertible> simpleDrop;

    protected MeatlibBlockSettings()
    {

    }

    // Copied from FabricBlockSettings
    protected MeatlibBlockSettings(AbstractBlock.Settings settings)
    {
        this();
        // Mostly Copied from vanilla's copy method
        AbstractBlockSettingsAccessor thisAccessor = (AbstractBlockSettingsAccessor) this;
        AbstractBlockSettingsAccessor otherAccessor = (AbstractBlockSettingsAccessor) settings;

        // Copied in vanilla: sorted by vanilla copy order
        this.hardness(otherAccessor.getHardness());
        this.resistance(otherAccessor.getResistance());
        this.collidable(otherAccessor.getCollidable());
        thisAccessor.setRandomTicks(otherAccessor.getRandomTicks());
        this.luminance(otherAccessor.getLuminance());
        thisAccessor.setMapColorProvider(otherAccessor.getMapColorProvider());
        this.sounds(otherAccessor.getSoundGroup());
        this.slipperiness(otherAccessor.getSlipperiness());
        this.velocityMultiplier(otherAccessor.getVelocityMultiplier());
        thisAccessor.setDynamicBounds(otherAccessor.getDynamicBounds());
        thisAccessor.setOpaque(otherAccessor.getOpaque());
        thisAccessor.setIsAir(otherAccessor.getIsAir());
        thisAccessor.setBurnable(otherAccessor.getBurnable());
        thisAccessor.setLiquid(otherAccessor.getLiquid());
        thisAccessor.setForceNotSolid(otherAccessor.getForceNotSolid());
        thisAccessor.setForceSolid(otherAccessor.getForceSolid());
        this.pistonBehavior(otherAccessor.getPistonBehavior());
        thisAccessor.setToolRequired(otherAccessor.isToolRequired());
        thisAccessor.setOffsetter(otherAccessor.getOffsetter());
        thisAccessor.setBlockBreakParticles(otherAccessor.getBlockBreakParticles());
        thisAccessor.setRequiredFeatures(otherAccessor.getRequiredFeatures());
        this.emissiveLighting(otherAccessor.getEmissiveLightingPredicate());
        this.instrument(otherAccessor.getInstrument());
        thisAccessor.setReplaceable(otherAccessor.getReplaceable());

        // Not copied in vanilla: field definition order
        this.jumpVelocityMultiplier(otherAccessor.getJumpVelocityMultiplier());
        this.drops(otherAccessor.getLootTableId());
        this.allowsSpawning(otherAccessor.getAllowsSpawningPredicate());
        this.solidBlock(otherAccessor.getSolidBlockPredicate());
        this.suffocates(otherAccessor.getSuffocationPredicate());
        this.blockVision(otherAccessor.getBlockVisionPredicate());
        this.postProcess(otherAccessor.getPostProcessPredicate());

        if (settings instanceof MeatlibBlockSettings mbs)
        {
            this.tags = mbs.tags != null ? new HashSet<>(mbs.tags) : null;
        }
    }

    public static MeatlibBlockSettings create()
    {
        return new MeatlibBlockSettings();
    }

    public static MeatlibBlockSettings create(TagKey<Block> tagKey)
    {
        var settings = new MeatlibBlockSettings();
        settings.tags = new HashSet<>();
        settings.tags.add(tagKey);
        return settings;
    }

    public static MeatlibBlockSettings copyOf(AbstractBlock block)
    {
        return new MeatlibBlockSettings(((AbstractBlockAccessor) block).getSettings());
    }

    public static MeatlibBlockSettings copyOf(AbstractBlock.Settings settings)
    {
        return new MeatlibBlockSettings(settings);
    }

    public MeatlibBlockSettings copy()
    {
        return new MeatlibBlockSettings(this);
    }

    public Set<TagKey<Block>> getTags()
    {
        return tags;
    }

    @Nullable
    public Function<Block, ItemConvertible> getSimpleDrop()
    {
        return simpleDrop;
    }

    /**
     * For generating drops for blocks that do not implement MeatlibBlock.
     */
    public MeatlibBlockSettings simpleDrop(Function<Block, ItemConvertible> supplier)
    {
        this.simpleDrop = supplier;
        return this;
    }

    // Could use varargs, but that may be unsafe.
    public MeatlibBlockSettings tags()
    {
        this.tags = Set.of();
        return this;
    }

    public MeatlibBlockSettings tags(TagKey<Block> tags)
    {
        this.tags = Set.of(tags);
        return this;
    }

    public MeatlibBlockSettings tags(Set<TagKey<Block>> tags)
    {
        this.tags = Set.copyOf(tags);
        return this;
    }

    @Override
    public MeatlibBlockSettings hardness(float hardness)
    {
        super.hardness(hardness);
        return this;
    }
}
