package com.imoonday.replicore.core;

import net.minecraft.world.item.ItemStack;

public enum CoreTier {
    DAMAGED(1) {
        @Override
        public boolean canDuplicate(ItemStack stack) {
            return stack.getRarity().ordinal() < this.getTier() && !stack.isDamageableItem() && !stack.isDamaged() && !stack.isEnchanted();
        }
    },
    STANDARD(2) {
        @Override
        public boolean canDuplicate(ItemStack stack) {
            return stack.getRarity().ordinal() < this.getTier() && !stack.isDamaged() && !stack.isEnchanted();
        }
    },
    REFINED(3) {
        @Override
        public boolean canDuplicate(ItemStack stack) {
            return stack.getRarity().ordinal() < this.getTier() && !stack.isDamaged();
        }
    },
    VOID(4) {
        @Override
        public boolean canDuplicate(ItemStack stack) {
            return true;
        }
    };

    private final int tier;

    CoreTier(int tier) {
        this.tier = tier;
    }

    public abstract boolean canDuplicate(ItemStack stack);

    public int getTier() {
        return tier;
    }
}
