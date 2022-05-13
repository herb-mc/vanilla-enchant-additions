package com.herb_mc.vanilla_enchant_additions.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(
            method = "createArrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setPierceLevel(B)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity, int i) {
        // piercing slightly increases damage
        persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double) i * 0.2D);
    }

}
