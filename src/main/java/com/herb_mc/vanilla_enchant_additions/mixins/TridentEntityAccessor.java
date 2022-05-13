package com.herb_mc.vanilla_enchant_additions.mixins;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentEntity.class)
public interface TridentEntityAccessor {

    @Accessor("dealtDamage")
    void setDealtDamage(boolean b);

    @Accessor("tridentStack")
    ItemStack getTridentStack();

}
