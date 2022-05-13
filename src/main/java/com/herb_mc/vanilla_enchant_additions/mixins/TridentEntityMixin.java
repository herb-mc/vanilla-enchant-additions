package com.herb_mc.vanilla_enchant_additions.mixins;

import net.minecraft.entity.projectile.TridentEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void tick(CallbackInfo ci) {
        TridentEntity t = (TridentEntity) (Object) this;
        if (t.getPos().y < t.getWorld().getBottomY())
            ((TridentEntityAccessor) this).setDealtDamage(true);
    }

}
