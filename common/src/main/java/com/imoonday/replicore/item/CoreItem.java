package com.imoonday.replicore.item;

import com.imoonday.replicore.core.CoreTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CoreItem extends Item {

    private final CoreTier tier;

    public CoreItem(CoreTier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public CoreTier getTier() {
        return tier;
    }

    public boolean canDuplicate(ItemStack stack) {
        return tier.canDuplicate(stack);
    }
}
