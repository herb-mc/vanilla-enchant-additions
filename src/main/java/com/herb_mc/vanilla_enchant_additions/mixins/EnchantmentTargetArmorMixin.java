package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$1")
public class EnchantmentTargetArmorMixin {

    @Inject(
            method = "isAcceptableItem",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(item instanceof ArmorItem || (item instanceof ElytraItem && VEAMod.configMaps.get("extendedElytraEnchants").getBool()));
    }

}
