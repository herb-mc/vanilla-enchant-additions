package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @ModifyConstant(
            method = "addSoulSpeedBoostIfNeeded",
            constant = @Constant(floatValue = 0.04f)
    )
    protected float soulSpeedChance(float constant) {
        return VEAMod.configMaps.get("soulSpeedBreakChance").getFloat();
    }

}
