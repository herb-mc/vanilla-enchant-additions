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
import net.minecraft.item.Items;
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
    private int activeTicks = 1;
    private boolean kb = true;
    private boolean altFire = false;

    @Override
    public void setTracked(int i, Hand h, ItemStack item, ItemStack proj, boolean b) {
        activeTicks = i;
        trackedHand = h;
        trackedItemStack = item;
        trackedProjectile = proj;
        altFire = b;
    }

    @Override
    public boolean useAlt() {
        return altFire;
    }

    @Override
    public boolean shouldDoKnockback() {
        return kb;
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
            kb = activeTicks == VEAMod.configMaps.get("multishotBurstDelay").getInt();
            if (activeTicks % VEAMod.configMaps.get("multishotBurstDelay").getInt() == 0) ((CrossbowItemAccessor) Items.CROSSBOW).shootArrow(p.world, p, trackedHand, trackedItemStack, trackedProjectile, getSoundPitch(p.getRandom()), true, getSpeed(trackedProjectile), 1.0f, 0.0f);
            activeTicks--;
        } else if (activeTicks > 1 && p.getStackInHand(trackedHand) != trackedItemStack) {
            activeTicks = 1;
        }
    }

}
