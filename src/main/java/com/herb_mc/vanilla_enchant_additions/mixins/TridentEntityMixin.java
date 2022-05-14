package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    private float dmg = 8.0f;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    public void tick(CallbackInfo ci) {
        // handle loyalty below bottom y
        if (VEAMod.defaultConfigs.get("voidLoyalty").getBool()) {
            TridentEntity t = (TridentEntity) (Object) this;
            if (t.getPos().y < t.getWorld().getBottomY())
                ((TridentEntityAccessor) this).setDealtDamage(true);
        }
    }

    @Inject(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void impalingBoost(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f, LivingEntity livingEntity) {
        // calculate trident damage boost if impaling boost was not already applied
        if (VEAMod.defaultConfigs.get("extendedImpaling").getBool()) {
            float temp = EnchantmentHelper.getAttackDamage(((TridentEntityAccessor) this).getTridentStack(), livingEntity.getGroup());
            int i = EnchantmentHelper.getLevel(Enchantments.IMPALING, ((TridentEntityAccessor) this).getTridentStack().copy());
            if (temp <= 8.0 && i > 0 && (livingEntity.isTouchingWaterOrRain() || livingEntity instanceof DrownedEntity))
                dmg = f + i * 2.5f;
        }
    }

    @Inject(
            method = "onEntityHit",
            at = @At("TAIL"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void channelingAlways(EntityHitResult entityHitResult, CallbackInfo ci, Entity entity, float f, Entity entity2, DamageSource damageSource, SoundEvent soundEvent, float g) {
        // channeling always applies
        TridentEntity t = (TridentEntity) (Object) this;
        if (t.world instanceof ServerWorld && !t.world.isThundering() && t.hasChanneling() && VEAMod.configMaps.get("channelingAlways").getBool()) {
            BlockPos blockPos = entity.getBlockPos();
            if (t.world.isSkyVisible(blockPos)) {
                LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(t.world);
                lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
                lightningEntity.setChanneler(entity2 instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity2 : null);
                t.world.spawnEntity(lightningEntity);
                soundEvent = SoundEvents.ITEM_TRIDENT_THUNDER;
                g = 5.0F;
            }
            t.playSound(soundEvent, g, 1.0F);
        }
    }

    @ModifyArg(
            method = "onEntityHit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
            )
    )
    public float modifyDamage(float f) {
        if (VEAMod.defaultConfigs.get("extendedImpaling").getBool())
            return Math.max(dmg, f);
        return f;
    }

}
