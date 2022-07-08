package com.herb_mc.vanilla_enchant_additions.etc;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface PlayerEntityAccess {

    void setTracked(int i, Hand h, ItemStack item, ItemStack trackedProjectile, boolean ignoreIframes);

}
