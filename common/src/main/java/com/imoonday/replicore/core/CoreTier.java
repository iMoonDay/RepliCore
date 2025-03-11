package com.imoonday.replicore.core;

import net.minecraft.world.item.ItemStack;

public interface CoreTier {

    boolean canDuplicate(ItemStack stack);

    int getTier();
}
