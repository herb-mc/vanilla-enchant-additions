package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.etc.PersistentProjectileEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin implements PersistentProjectileEntityAccess {

    private boolean ignoresIframes = false;
    private int armorPierce = 0;

    @Override
    public void setIgnoreInvulnerability(boolean t) {
        ignoresIframes = t;
    }

    @Override
    public void setIgnoresArmor(int i) {
        armorPierce = i;
    }

    @Override
    public int getArmorPierce() {
        return armorPierce;
    }

    @Redirect(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    protected boolean ignoreIframes(Entity instance, DamageSource source, float amount) {
        boolean b;
        if (instance instanceof LivingEntity) {
            if (ignoresIframes) {
                ((LivingEntity) instance).hurtTime = 0;
                instance.timeUntilRegen = 9;
            }
            b = instance.damage(source, amount);
        }
        else b = instance.damage(source, amount);
        return b;
    }

}
