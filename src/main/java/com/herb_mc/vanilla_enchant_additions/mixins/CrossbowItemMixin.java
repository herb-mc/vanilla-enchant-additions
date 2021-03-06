package com.herb_mc.vanilla_enchant_additions.mixins;

import com.herb_mc.vanilla_enchant_additions.VEAMod;
import com.herb_mc.vanilla_enchant_additions.etc.PersistentProjectileEntityInterface;
import com.herb_mc.vanilla_enchant_additions.etc.PlayerEntityAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

import static com.herb_mc.vanilla_enchant_additions.etc.CrossbowItemMethods.getProjectile;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {


    @Inject(
            method = "createArrow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setPierceLevel(B)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow, CallbackInfoReturnable<PersistentProjectileEntity> cir, ArrowItem arrowItem, PersistentProjectileEntity persistentProjectileEntity, int i) {
        // piercing increases damage, default to 0.25
        persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double) i * VEAMod.configMaps.get("piercingDmgBoost").getDouble());
    }

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void gatlingArrows(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack) {
        // initiate player burst fire
        if (EnchantmentHelper.getLevel(Enchantments.MULTISHOT, itemStack) > 0 && VEAMod.configMaps.get("multishotBurstDelay").getInt() > 0)
            if (user.isSneaking() && VEAMod.configMaps.get("multishotBurstAlt").getBool() && VEAMod.configMaps.get("multishotBurstDelay").getInt() > 0) {
                // alt burst fire
                ((PlayerEntityAccess) user).setTracked(VEAMod.configMaps.get("multishotBurstDelay").getInt() * ((VEAMod.configMaps.get("multishotCount").getInt() + 3) / 2) - 1, hand, itemStack, getProjectile(itemStack), true);
                user.getItemCooldownManager().set(itemStack.getItem(), VEAMod.configMaps.get("multishotBurstDelay").getInt() * ((VEAMod.configMaps.get("multishotCount").getInt() + 3) / 2));
            } else {
                // normal burst fire
                ((PlayerEntityAccess) user).setTracked(VEAMod.configMaps.get("multishotBurstDelay").getInt() * (VEAMod.configMaps.get("multishotCount").getInt() + 3) - (VEAMod.configMaps.get("multishotBurstDelay").getInt() == 1 ? 0 : 1), hand, itemStack, getProjectile(itemStack), false);
                user.getItemCooldownManager().set(itemStack.getItem(), VEAMod.configMaps.get("multishotBurstDelay").getInt() * (VEAMod.configMaps.get("multishotCount").getInt() + 3));
            }
    }

    @ModifyVariable(
            method = "loadProjectiles",
            at = @At(
                    value = "STORE",
                    target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"
            )
    )
    private static int burstFire(int i) {
        // disable multishot if multishotBurst enabled
        return VEAMod.configMaps.get("multishotBurstDelay").getInt() > 0 ? 0 : i;
    }

    @ModifyVariable(
            method = "loadProjectiles",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;getArrowType(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
            ),
            index = 3
    )
    private static int modifiedMultishot(int j) {
        // set count to updated multishot amount
        return j == 3 ? 3 + VEAMod.configMaps.get("multishotCount").getInt() : j;
    }

    @Inject(
            method = "shootAll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;isEmpty()Z",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void multishotScalable(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, CallbackInfo ci, List list, float[] fs, int i, ItemStack itemStack, boolean bl) {
        // multishot can fire multiple arrows
        if (i > 2 && VEAMod.configMaps.get("multishotCount").getInt() > 0) {
            float f = (i % 2 == 0 ? -1 : 1) *  (10.0f / VEAMod.configMaps.get("multishotCount").getInt() * (i - (i % 2 == 1 ? 2 : 3)));
            ((CrossbowItemAccessor) Items.CROSSBOW).shootArrow(world, entity, hand, stack, itemStack, fs[1 + i % 2], true, speed, divergence, f);
        }
    }

    @Inject(
            method = "shoot",
            at = @At(
                    target = "Lnet/minecraft/item/CrossbowItem;createArrow(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;",
                    value = "INVOKE_ASSIGN"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void modifyShoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci, boolean bl, ProjectileEntity projectileEntity) {
        if (shooter instanceof PlayerEntity && (((PlayerEntityAccess) shooter).useAlt()) && EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow) > 0 && VEAMod.configMaps.get("multishotBurstDelay").getInt() > 0 && shooter.isSneaking()) {
            if (projectileEntity instanceof PersistentProjectileEntity)
                ((PersistentProjectileEntityInterface) projectileEntity).setIgnoreInvulnerability(true);
            if (!((PlayerEntityAccess) shooter).shouldDoKnockback())
                if (projectileEntity instanceof PersistentProjectileEntity)
                    ((PersistentProjectileEntityInterface) projectileEntity).setKnockback(false);
        }
    }

    @ModifyArgs(
            method = "loadProjectiles",
            at = @At(
                    target = "Lnet/minecraft/item/CrossbowItem;loadProjectile(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;ZZ)Z",
                    value = "INVOKE"
            )
    )
    private static void applyInfinity(Args args) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, args.get(1)) >= 1) args.set(4, true);
    }

    @ModifyArgs(
            method = "shootAll",
            at = @At(
                    target = "Lnet/minecraft/item/CrossbowItem;shoot(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;FZFFF)V",
                    value = "INVOKE"
            )
    )
    private static void applyInfinityToFired(Args args) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, args.get(3)) >= 1) args.set(6, true);
    }

}
