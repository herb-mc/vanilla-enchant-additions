package com.herb_mc.vanilla_enchant_additions.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.herb_mc.vanilla_enchant_additions.helper.VEAConfigHelper.loadConf;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(
            method = "loadWorld",
            at = @At("TAIL")
    )
    protected void configOnLoad(CallbackInfo ci) {
        loadConf((MinecraftServer) (Object) this);
    }

}