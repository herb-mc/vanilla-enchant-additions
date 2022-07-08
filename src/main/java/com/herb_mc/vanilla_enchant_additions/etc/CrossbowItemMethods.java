package com.herb_mc.vanilla_enchant_additions.etc;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static net.minecraft.item.CrossbowItem.hasProjectile;

public class CrossbowItemMethods {

    public static ItemStack getProjectile(ItemStack crossbow) {
        NbtCompound nbtCompound = crossbow.getNbt();
        if (nbtCompound != null && nbtCompound.contains("ChargedProjectiles", 9)) {
            NbtList nbtList = nbtCompound.getList("ChargedProjectiles", 10);
            if (nbtList != null) {
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                return ItemStack.fromNbt(nbtCompound2);
            }
        }
        return ItemStack.EMPTY;
    }

    public static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, boolean ignore) {
        if (!world.isClient) {
            boolean bl = projectile.isOf(Items.FIREWORK_ROCKET);
            ProjectileEntity projectileEntity;
            if (bl) {
                projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - 0.15000000596046448D, shooter.getZ(), true);
            } else {
                projectileEntity = createArrow(world, shooter, crossbow, projectile);
                ((PersistentProjectileEntityAccess) projectileEntity).setIgnoreInvulnerability(ignore);
                if (creative || simulated != 0.0F) {
                    ((PersistentProjectileEntity)projectileEntity).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
            }
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
            Quaternion quaternion = new Quaternion(new Vec3f(vec3d), simulated, true);
            Vec3d vec3d2 = shooter.getRotationVec(1.0F);
            Vec3f vec3f = new Vec3f(vec3d2);
            vec3f.rotate(quaternion);
            projectileEntity.setVelocity((double)vec3f.getX(), (double)vec3f.getY(), (double)vec3f.getZ(), speed, divergence);

            crossbow.damage(bl ? 3 : 1, shooter, (e) -> e.sendToolBreakStatus(hand));
            world.spawnEntity(projectileEntity);
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
        }
    }

    public static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
        ArrowItem arrowItem = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrow, entity);
        persistentProjectileEntity.setCritical(true);

        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        persistentProjectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte)i);
        }

        return persistentProjectileEntity;
    }

    public static float getSoundPitch(Random random) {
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + 0.63F;
    }

    public static float getSpeed(ItemStack stack) {
        return hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

}
