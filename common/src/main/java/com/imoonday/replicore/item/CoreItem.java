package com.imoonday.replicore.item;

import com.imoonday.replicore.core.CoreTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        int i = 1;
        String prefix = getTooltipKeyPrefix();
        String key = prefix + i;
        while (I18n.exists(key)) {
            tooltipComponents.add(Component.translatable(key).withStyle(ChatFormatting.GRAY));
            key = prefix + ++i;
        }
    }

    public String getTooltipKeyPrefix() {
        return this.getDescriptionId() + ".tooltip.";
    }
}
