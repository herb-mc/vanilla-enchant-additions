package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.enchantment.*;
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
        // bows accept smite
        if (item instanceof BowItem && VEAMod.configMaps.get("extendedBowEnchants").getBool() && (((Object) this).equals(Enchantments.SMITE)))
            cir.setReturnValue(true);
        // tridents and bows accept piercing
        if (((item instanceof TridentItem && VEAMod.configMaps.get("extendedTridentEnchants").getBool()) || (item instanceof BowItem && VEAMod.configMaps.get("extendedBowEnchants").getBool())) && (((Enchantment) (Object) this) instanceof PiercingEnchantment))
            cir.setReturnValue(true);
        // allows sword enchantments to be put on axes and tridents if respective config options are enabled
        if (this.type == EnchantmentTarget.WEAPON && !(((Enchantment) (Object) this) instanceof SweepingEnchantment) && (item instanceof SwordItem || (item instanceof TridentItem && VEAMod.configMaps.get("extendedTridentEnchants").getBool()) || (item instanceof AxeItem && VEAMod.configMaps.get("extendedAxeEnchants").getBool())))
            cir.setReturnValue(true);
    }

}
