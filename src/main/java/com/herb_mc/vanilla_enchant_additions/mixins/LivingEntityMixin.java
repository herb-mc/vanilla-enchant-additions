package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import com.herb_mc.vanilla_enchant_additions.etc.PersistentProjectileEntityAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    private int level = 0;

    @ModifyVariable(
            method = "applyArmorToDamage",
            at = @At(
                    value = "HEAD",
                    target = "Lnet/minecraft/entity/LivingEntity;applyArmorToDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"
            ),
            ordinal = 0
    )
    private DamageSource getArmorPierceLevel(DamageSource source) {
        level = 0;
        // cases to apply armor pierce
        if (source.getAttacker() != null)
            // melee trident
            if (source.getAttacker() instanceof LivingEntity && !source.isProjectile() && ((LivingEntity) source.getAttacker()).getActiveHand() != null)
                level = EnchantmentHelper.getLevel(Enchantments.PIERCING, ((LivingEntity) source.getAttacker()).getStackInHand(((LivingEntity) source.getAttacker()).getActiveHand()));
            // thrown trident
            else if (source.getSource() instanceof TridentEntity)
                level = EnchantmentHelper.getLevel(Enchantments.PIERCING, ((TridentEntityAccessor)source.getSource()).getTridentStack());
            // arrow
            else if (source.getSource() instanceof ArrowEntity)
                level = ((PersistentProjectileEntityAccess) source.getSource()).getArmorPierce();
        return source;
    }

    @ModifyArg(
            method = "applyArmorToDamage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/DamageUtil;getDamageLeft(FFF)F"
            ),
            index = 1
    )
    private float armorPiercing(float armor) {
        // piercing applies armor piercing
        armor *= 1.0D - level * VEAMod.configMaps.get("piercingIgnoreArmorPercent").getDouble();
        return armor > 0 ? armor : 0;
    }

    @ModifyConstant(
            method = "addSoulSpeedBoostIfNeeded",
            constant = @Constant(floatValue = 0.04f)
    )
    protected float soulSpeedChance(float constant) {
        return VEAMod.configMaps.get("soulSpeedBreakChance").getFloat();
    }

}
