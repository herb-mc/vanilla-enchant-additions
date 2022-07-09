package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import com.herb_mc.vanilla_enchant_additions.etc.PersistentProjectileEntityAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;

@Mixin(BowItem.class)
public class BowItemMixin {

    private boolean bl;
    private ItemStack itemStack;

    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;getArrowType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void getLocals(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl) {
        this.bl = bl;
        this.itemStack = playerEntity.getArrowType(stack);
    }

    @ModifyVariable(
            method = "onStoppedUsing",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
            ),
            ordinal = 1
    )
    private boolean bl2(boolean bl2) {
        // make infinity apply to all arrows
        return (bl2) || (bl && (itemStack.isOf(Items.SPECTRAL_ARROW) || itemStack.isOf(Items.TIPPED_ARROW)) && VEAMod.configMaps.get("infinityForAll").getBool());
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    protected void setArmorPierce(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci, PlayerEntity playerEntity, boolean bl, ItemStack itemStack, int i, float f, boolean bl2, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity) {
        ((PersistentProjectileEntityAccess) persistentProjectileEntity).setIgnoresArmor(EnchantmentHelper.getLevel(Enchantments.PIERCING, stack));
<<<<<<< HEAD
=======
        ((PersistentProjectileEntityAccess) persistentProjectileEntity).setSmiteLevel(EnchantmentHelper.getLevel(Enchantments.SMITE, stack));
>>>>>>> ea0cf2e20c66092f79f1201c243d646c80b0426d
    }

    @Inject(
            method = "onStoppedUsing",
            at = @At("TAIL")
    )
    private void updateInv(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        // send packets to update player inventory clientside
        if(user instanceof ServerPlayerEntity && VEAMod.configMaps.get("infinityForAll").getBool()) {
            int index = getArrowIndex((ServerPlayerEntity) user, stack);
            if (index >= 0 && !world.isClient) ((ServerPlayerEntity) user).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, -2, index, itemStack));
        }
    }

    public int getArrowIndex(PlayerEntity player, ItemStack stack) {
        // find index of fired arrow
        if (!(stack.getItem() instanceof RangedWeaponItem)) {
            return -1;
        } else {
            Predicate<ItemStack> predicate = ((RangedWeaponItem)stack.getItem()).getHeldProjectiles();
            if (predicate.test(player.getStackInHand(Hand.OFF_HAND))) {
                return 40;
            } else {
                predicate = ((RangedWeaponItem)stack.getItem()).getProjectiles();
                for(int i = 0; i < player.getInventory().size(); ++i) {
                    ItemStack itemStack = player.getInventory().getStack(i);
                    if (predicate.test(itemStack)) {
                        return i;
                    }
                }
                return -1;
            }
        }
    }
}
