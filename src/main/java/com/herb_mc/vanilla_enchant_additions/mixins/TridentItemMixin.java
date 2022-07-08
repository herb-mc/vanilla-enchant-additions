package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/TypedActionResult;fail(Ljava/lang/Object;)Lnet/minecraft/util/TypedActionResult;"
            ),
            cancellable = true)
    public void overrideRiptideCheck(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        Hand other = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        if (user.getStackInHand(other) == ItemStack.EMPTY && VEAMod.configMaps.get("riptideAlways").getBool() && EnchantmentHelper.getRiptide(user.getStackInHand(hand)) > 0) {
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume(user.getStackInHand(hand)));
        }
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At("HEAD")
    )
    public void riptideInClearWeather(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (user instanceof PlayerEntity playerEntity && VEAMod.configMaps.get("riptideAlways").getBool()) {
            TridentItem t = (TridentItem) (Object) this;
            int i = t.getMaxUseTime(stack) - remainingUseTicks;
            if (i >= 10) {
                int j = EnchantmentHelper.getRiptide(stack);
                if (j > 0 && !playerEntity.isTouchingWaterOrRain()) {
                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(t));
                    float f = playerEntity.getYaw();
                    float g = playerEntity.getPitch();
                    float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                    float k = -MathHelper.sin(g * 0.017453292F);
                    float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                    float m = MathHelper.sqrt(h * h + k * k + l * l);
                    float n = 3.0F * ((1.0F + (float) j) / 4.0F);
                    h *= n / m;
                    k *= n / m;
                    l *= n / m;
                    playerEntity.addVelocity(h, k, l);
                    if(playerEntity instanceof ServerPlayerEntity && !playerEntity.world.isClient()) ((ServerPlayerEntity) playerEntity).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(playerEntity));
                    playerEntity.useRiptide(20);
                    if (playerEntity.isOnGround())
                        playerEntity.move(MovementType.SELF, new Vec3d(0.0D, 1.1999999284744263D, 0.0D));

                    SoundEvent soundEvent;
                    if (j >= 3) {
                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                    } else if (j == 2) {
                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                    } else {
                        soundEvent = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                    }

                    world.playSoundFromEntity(null, playerEntity, soundEvent, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

}
