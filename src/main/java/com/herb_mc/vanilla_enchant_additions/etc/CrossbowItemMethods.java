package com.herb_mc.vanilla_enchant_additions.etc;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.random.Random;

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

    public static float getSoundPitch(Random random) {
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + 0.63F;
    }

    public static float getSpeed(ItemStack stack) {
        return hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

}
