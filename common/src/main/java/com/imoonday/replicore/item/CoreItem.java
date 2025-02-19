package com.imoonday.replicore.item;

import com.imoonday.replicore.core.CoreTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoreItem extends Item {

    private final CoreTier tier;
    private final int tooltipLines;

    public CoreItem(CoreTier tier, int tooltipLines, Properties properties) {
        super(properties);
        this.tooltipLines = tooltipLines;
        this.tier = tier;
    }

    public CoreTier getTier() {
        return tier;
    }

    public boolean canDuplicate(ItemStack stack) {
        return tier.canDuplicate(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        for (int i = 1; i <= tooltipLines; i++) {
            tooltipComponents.add(Component.translatable(this.getDescriptionId() + ".tooltip." + i).withStyle(ChatFormatting.GRAY));
        }
    }
}
