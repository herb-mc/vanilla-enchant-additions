package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.InfinityEnchantment;
import net.minecraft.enchantment.MendingEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InfinityEnchantment.class)
public class InfinityEnchantmentMixin extends Enchantment {

    protected InfinityEnchantmentMixin(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    @Inject(
            method = "canAccept",
            at = @At("HEAD"),
            cancellable = true)
    protected void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((!(other instanceof MendingEnchantment) || VEAMod.configMaps.get("infinityCompatibleWithMending").getBool()) && super.canAccept(other));
    }

}
