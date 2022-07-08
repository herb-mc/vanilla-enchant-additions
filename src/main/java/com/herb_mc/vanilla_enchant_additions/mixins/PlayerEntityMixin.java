package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import com.herb_mc.vanilla_enchant_additions.etc.PlayerEntityAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.herb_mc.vanilla_enchant_additions.etc.CrossbowItemMethods.*;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityAccess {

    private Entity target;
    private Hand trackedHand;
    private ItemStack trackedItemStack;
    private ItemStack trackedProjectile;
    private boolean ignoreIframes = false;
    private int activeTicks = 1;

    @Override
    public void setTracked(int i, Hand h, ItemStack item, ItemStack proj, boolean ignore) {
        activeTicks = i;
        trackedHand = h;
        trackedItemStack = item;
        trackedProjectile = proj;
        ignoreIframes = ignore;
    }

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

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void gatlingArrows(CallbackInfo ci) {
        PlayerEntity p = (PlayerEntity) (Object) this;
        if (activeTicks > 1 && p.getStackInHand(trackedHand) == trackedItemStack) {
            if (activeTicks % VEAMod.configMaps.get("multishotBurstDelay").getInt() == 0) shoot(p.world, p, trackedHand, trackedItemStack, trackedProjectile, getSoundPitch(p.getRandom()), true, getSpeed(trackedProjectile), 1.0f, 0.0f, ignoreIframes);
            activeTicks--;
        } else if (activeTicks > 1 && p.getStackInHand(trackedHand) != trackedItemStack) {
            activeTicks = 1;
        }
    }

}
