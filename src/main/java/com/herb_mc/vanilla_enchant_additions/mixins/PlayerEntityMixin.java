package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    Entity target;

    @Inject(
            method = "attack",
            at = @At("HEAD")
    )
    private void getTarget(Entity target, CallbackInfo ci) {
        this.target = target;
    }

    @ModifyVariable(
            method = "attack",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"
            ),
            ordinal = 0
    )
    public float g(float g) {
        // melee attack damage increase for impaling
        PlayerEntity p = (PlayerEntity) (Object) this;
        if (target instanceof LivingEntity l) {
            boolean b = ((!(EnchantmentHelper.getAttackDamage(p.getMainHandStack(), l.getGroup()) > 0)) && l.isTouchingWaterOrRain()) || l instanceof DrownedEntity;
            return (VEAMod.configMaps.get("extendedImpaling").getBool() && b) ? 2.5f * EnchantmentHelper.getLevel(Enchantments.IMPALING, p.getMainHandStack()) + g : g;
        }
        return g;
    }

}
