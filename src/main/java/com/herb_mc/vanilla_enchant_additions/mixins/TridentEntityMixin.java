package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.logger.VEALogger;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    private float dmg = 8.0f;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void tick(CallbackInfo ci) {
        // handle loyalty below bottom y
        TridentEntity t = (TridentEntity) (Object) this;
        if (t.getPos().y < t.getWorld().getBottomY())
            ((TridentEntityAccessor) this).setDealtDamage(true);
    }

    @Inject(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f, LivingEntity livingEntity) {
        // calculate trident damage boost if impaling boost was not already applied
        float temp = EnchantmentHelper.getAttackDamage(((TridentEntityAccessor) this).getTridentStack(), livingEntity.getGroup());
        int i = EnchantmentHelper.getLevel(Enchantments.IMPALING, ((TridentEntityAccessor) this).getTridentStack().copy());
        if (temp <= 8.0 && i > 0 && (livingEntity.isTouchingWater() || livingEntity instanceof DrownedEntity))
            dmg = f + i * 2.5f;
    }

    @ModifyArg(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    public float modifyDamage(float f) {
        return Math.max(dmg, f);
    }

    @Inject(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/TridentEntity;getOwner()Lnet/minecraft/entity/Entity;"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void onEntityHit2(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f) {
        VEALogger.LOGGER.info("{}", f);
    }

}
