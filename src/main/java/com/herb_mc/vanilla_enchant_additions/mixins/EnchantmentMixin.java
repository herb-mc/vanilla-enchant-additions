package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.SweepingEnchantment;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Shadow @Final public EnchantmentTarget type;

    @Inject(
            method = "isAcceptableItem",
            at = @At("HEAD"),
            cancellable = true)
    protected void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Item item = stack.getItem();
        if (this.type == EnchantmentTarget.WEAPON && !(((Enchantment) (Object) this) instanceof SweepingEnchantment))
            cir.setReturnValue(item instanceof SwordItem || (item instanceof TridentItem && VEAMod.configMaps.get("extendedTridentEnchants").getBool()) || (item instanceof AxeItem && VEAMod.configMaps.get("extendedAxeEnchants").getBool()));
    }

}
